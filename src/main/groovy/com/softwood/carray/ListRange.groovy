package com.softwood.carray

import groovy.transform.EqualsAndHashCode
import groovy.transform.InheritConstructors
import org.codehaus.groovy.runtime.InvokerHelper
import org.codehaus.groovy.runtime.IteratorClosureAdapter
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation

@InheritConstructors
@EqualsAndHashCode (includeFields = true)
class ListRange<E> extends ObjectRange  implements Range<Comparable>{
    private long size = -1 //will be -1 if not computed

    ListRange (ArrayList fromAL, ArrayList toAL) {
        super (new ComparableArrayList(fromAL),  new ComparableArrayList(toAL))
        size()
        this
    }

    ListRange (ComparableArrayList from, ComparableArrayList to){
        super(from, to)
        size()
        this
    }

    ListRange (ComparableArrayList from, ComparableArrayList to, boolean reverse){
        super(from, to)
        size()
        this.reverse(true)
        this
    }

    @ Override
    Comparable getFrom () { super.from }

    @ Override
    Comparable getTo () { super.to }

    //use protected checkBoundaryCompatibility () from ObjectRange parent
    //https://github.com/apache/groovy/blob/master/src/main/java/groovy/lang/ObjectRange.java

    //returns new anonymous inner class morphed to Iterator
    Iterator<E> iterator() {
        new com.softwood.carray.StepIterator(this, 1)
    }
    
    /**
     * Non-thread-safe iterator which lazily produces the next element only on calls of hasNext() or next()
     */
    private static final class StepIterator implements Iterator<Comparable> {
        // actual step, can be +1 when desired step is -1 and direction is from high to low
        private final int step
        private final ObjectRange range
        private int index = -1
        private Comparable value
        private boolean nextFetched = true

        private StepIterator(ObjectRange range, final int desiredStep) {
            if (desiredStep == 0 && range.compareTo(range.getFrom(), range.getTo()) != 0) {
                throw new GroovyRuntimeException("Infinite loop detected due to step size of 0")
            }
            this.range = range
            if (range.isReverse()) {
                step = -desiredStep
            } else {
                step = desiredStep
            }
            if (step > 0) {
                value = range.getFrom()
            } else {
                value = range.getTo()
            }
        }

        @Override
        void remove() {
            range.remove(index)
        }

        @Override
        Comparable next() {
            // not thread safe
            if (!hasNext()) {
                throw new NoSuchElementException()
            }
            nextFetched = false
            index++
            return value
        }

        @Override
        boolean hasNext() {
            // not thread safe
            if (!nextFetched) {
                value = peek()
                nextFetched = true
            }
            return value != null
        }

        private Comparable peek() {
            if (step > 0) {
                Comparable peekValue = value
                int compared
                for (int i = 0; i < step; i++) {
                    peekValue = (Comparable) range.increment(peekValue)
                    // handle back to beginning due to modulo incrementing
                    if (peekValue.compareTo(range.from) <= 0) return null
                }
                if (peekValue.compareTo(range.to) <= 0) {
                        return peekValue
                }
            } else {
                final int positiveStep = -step
                Comparable peekValue = value
                for (int i = 0; i < positiveStep; i++) {
                    peekValue = (Comparable) range.decrement(peekValue)
                    // handle back to beginning due to modulo decrementing
                    if (peekValue.compareTo(range.from) >= 0) return null
                }
                if (peekValue.compareTo(range.from) >= 0) {
                    return peekValue
                }
            }
            return null
        }
    }


    @ Override
    Comparable get(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + " should not be negative")
        }
        if (index >= size()) {
            throw new IndexOutOfBoundsException("Index: " + index + " is too big for range: " + this)
        }
        Object value
        if (reverse) {
            //if nested array unpack first level and get first element
            if (from[0] instanceof List)
                value = from[0]
            else
                value = from

            for (int i = 0; i < index; i++) {
                value = decrement(value)
            }
        } else {
            //if nested array unpack first level and get first element
            if (from[0] instanceof List)
                value = from[0]
            else
                value = from
            for (int i = 0; i < index; i++) {
                value = increment(value)
            }
        }
        return value as Comparable
    }

    @SuppressWarnings("unused")
    void setSize(int size) {
        throw new UnsupportedOperationException("size must not be changed")
    }

    int getSize() {
        if (size == -1)
            size()
        else
            size
    }

    @Override
    int size() {
        if (size == -1) {
            if ((from instanceof Integer || from instanceof Long)
                    && (to instanceof Integer || to instanceof Long)) {
                // let's fast calculate the size
                long fromNum = ((Number) from).longValue()
                long toNum = ((Number) to).longValue()
                size = (int) (toNum - fromNum + 1)
            } else if (from instanceof Character && to instanceof Character) {
                // let's fast calculate the size
                char fromNum = (Character) from
                char toNum = (Character) to
                size = toNum - fromNum + 1
            } else if (from instanceof BigDecimal || to instanceof BigDecimal ||
                    from instanceof BigInteger || to instanceof BigInteger) {
                // let's fast calculate the size
                BigDecimal fromNum = new BigDecimal(from.toString())
                BigDecimal toNum = new BigDecimal(to.toString())
                BigInteger sizeNum = toNum.subtract(fromNum).add(new BigDecimal(1.0)).toBigInteger()
                size = sizeNum.intValue()
            } else if (from instanceof ComparableArrayList || to instanceof ComparableArrayList) {
                //calculate the number of entries in the range between the list start and end
                def upper, lower
                if (from?[0] instanceof ArrayList) {
                    //its nested array  of array so get last and first entries
                    upper = to.getElements() [-1]
                    lower = from.getElements() [0]
                } else {
                    //not nested so just get the entry as an array directly
                    upper = to.getElements() as ArrayList
                    lower = from.getElements() as ArrayList
                }
                if (upper instanceof ArrayList && lower instanceof ArrayList ) {
                    long upperBoundOfZindex = upper?[2] ? upper[2] as long : 0
                    long lowerBoundOfZindex = lower?[2] ? lower[2] as long : 0
                    long upperBoundOfRows = upper?[1] ? upper[1] as long : 0
                    long lowerBoundOfRows = lower?[1] ? lower[1] as long : 0
                    long upperBoundOfColumns = upper?[0] ? upper[0] as long : 0
                    long lowerBoundOfColumns = lower?[0] ? lower[0] as long : 0
                    long numberOfZindex = (upperBoundOfZindex - lowerBoundOfZindex) + 1
                    long numberOfColumns = (upperBoundOfColumns - lowerBoundOfColumns) + 1
                    long numberOfRows = (upperBoundOfRows - lowerBoundOfRows) + 1
                    size = (numberOfColumns * numberOfRows * numberOfZindex)
                } else if (upper instanceof Number && lower instanceof Number) {
                    //to and from are just numbers in a range
                    size = (upper - lower) + 1
                } else {
                    size = lazySizeCalculator ()
                }
            }
            else {
                // let's lazily calculate the size
                size = lazySizeCalculator ()
           }
        }
        return size
    }

    //general comparable - calculate size by walking to point where next incremented value is >=0
    private int lazySizeCalculator () {
        long tempsize = 0
        Comparable first = from
        Comparable value = from
        final iter = iterator()

        while (iter.hasNext()) {
            tempsize++
            // integer overflow
            if (tempsize < 0) {
                break
            }
            iter.next()
        }
        // integer overflow
        if (tempsize < 0) {
            tempsize = Long.MAX_VALUE
        }

        size = tempsize
    }

    @Override
    List<Comparable> step(int step) {
        //create new subclass of Closure, from groovy runtime that wraps ListRange
        //see https://github.com/apache/groovy/blob/master/src/main/java/org/codehaus/groovy/runtime/IteratorClosureAdapter.java
        //A closure which stores calls in a List so that method calls
        //can be iterated over in a 'yield' style way - err what ?
        final IteratorClosureAdapter<Comparable> adapter = new IteratorClosureAdapter<Comparable>(this)
        step(step, adapter)
        return adapter.asList()
    }


    @Override
    void step(int step, Closure closure) {
        if (step == 0 && compareTo(from, to) == 0) {
            return // from == to and step == 0, nothing to do, so return
        }
        final Iterator<Comparable> iter = new StepIterator(this, step)
        while (iter.hasNext()) {
            closure.call(iter.next())
        }
    }

    @Override
    List<Comparable> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex)
        }
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")")
        }
        if (fromIndex == toIndex) {
            return new EmptyRange<Comparable>(from)
        }

        // Performance detail:
        // not using get(fromIndex), get(toIndex) in the following to avoid stepping over elements twice
        final Iterator<Comparable> iter = new StepIterator(this, 1)

        Comparable toValue = iter.next();
        int i = 0
        for (; i < fromIndex; i++) {
            if (!iter.hasNext()) {
                throw new IndexOutOfBoundsException("Index: " + i + " is too big for range: " + this)
            }
            toValue = iter.next()
        }
        final Comparable fromValue = toValue;
        for (; i < toIndex - 1; i++) {
            boolean hasMore = iter.hasNext()
            if (!hasMore) {
                throw new IndexOutOfBoundsException("Index: " + i + " is too big for range: " + this)
            }
            toValue = iter.next()
        }

        return new ListRange(fromValue, toValue, reverse)
    }


    /**
     * Checks whether a value is between the from and to values of a Range
     *
     * @param value the value of interest
     * @return true if the value is within the bounds
     */
    @Override
    boolean containsWithinBounds(Object value) {
        if (value instanceof Comparable) {
            final int result = compareTo(from, (Comparable) value);
            return result == 0 || result < 0 && compareTo(to, (Comparable) value) >= 0
        }
        return contains(value)
    }

    /**
     * Iterates over all values and returns true if one value matches.
     *
     * @see #containsWithinBounds(Object)
     */
    @Override
    boolean contains(Object value) {
        final Iterator<Comparable> iter = new StepIterator(this, 1);
        if (value == null) {
            return false
        }
        while (iter.hasNext()) {
            if (DefaultTypeTransformation.compareEqual(value, iter.next())) return true
        }
        return false
    }

    /**
     * Increments by one
     *
     * @param value the value to increment
     * @return the incremented value
     */
    @Override
    protected Object increment(Object value) {
        /*  value might be a [[x,y]] or just [x,y] */
        boolean nested = false
        def upper, lower
        if (value?[0] instanceof ArrayList){
            upper = ((ArrayList) super.to) [-1]
            lower = ((ArrayList) super.from) [0]
            nested = true
        } else {
            upper =  super.to as ArrayList
            lower =  super.from as ArrayList
        }

        long currentRow, currentColumn, currentZindex
        long upperBoundOfRows, lowerBoundOfRows, upperBoundOfColumns, lowerBoundOfColumns
        long upperBoundOfZindex, lowerBoundOfZindex
        upperBoundOfRows = upper?[1] ? upper[1] as long: 0  //set as 0 if no rows
        lowerBoundOfRows = lower?[1] ? lower[1] as long: 0

        upperBoundOfColumns = upper?[0]? upper[0] as long : 0
        lowerBoundOfColumns = upper?[0]? lower[0] as long : 0

        upperBoundOfZindex = upper?[2] ? upper[2] as long : 0
        lowerBoundOfZindex = lower?[2] ? lower[2] as long: 0

        ComparableArrayList element = new ComparableArrayList()

        if (value instanceof ArrayList) {
            if (upper instanceof ArrayList && lower instanceof ArrayList) {
                if (nested) {
                    currentZindex = value[0]?[2] ? value[0][2] as long : 0
                    currentRow = value[0]?[1] ? value[0][1] as long: 0
                    currentColumn = value[0]?[0] ? value[0][0] as long: 0
                } else if (value[0] instanceof Number) {
                    currentZindex = value?[2] ? value[2] as long : 0
                    currentRow = value?[1] ? value[1] as long : 0
                    currentColumn = value?[0] ? value[0] as long : 0
                }
                if (nested) {
                    if (currentRow + 1 <= upperBoundOfRows) {
                        element.add([currentColumn, currentRow + 1])
                    } else if (currentColumn + 1 <= upperBoundOfColumns) {
                        element.add([currentColumn + 1, lowerBoundOfRows])
                    } else {
                        element.add([[]])
                    }
                } else {
                    if (currentRow + 1 <= upperBoundOfRows) {
                        element.addAll([currentColumn, currentRow + 1])
                    } else if (currentColumn + 1 <= upperBoundOfColumns) {
                        element.addAll([currentColumn + 1, lowerBoundOfRows])
                    } else {
                        element.add(null)
                    }
                }
                return element
            } else if (upper instanceof Number && lower instanceof Number) {
                def currentValue = value as int
                def incrementedValue = currentValue + 1
                upperBoundOfRows = upper?[1] ? upper[1] as long : 0
                lowerBoundOfRows = lower?[1] ? lower[1] as long : 0

                upperBoundOfColumns = upper?[0] ? upper[0] as long : 0
                lowerBoundOfColumns = lower?[0] ? lower[0] as long : 0

                //todo - need to think about how to handle this
                element.add(incrementedValue)
                return element

            }
        } else
            //todo - cant call next on value, have to use an iterator
            return InvokerHelper.invokeMethod(value, "next", null)
    }

    /**
     * Decrements by one
     *
     * @param value the value to decrement
     * @return the decremented value
     */
    @Override
    protected Object decrement(Object value) {
        /*  value might be a [[x,y]] or just [x,y] */
        boolean nested = false
        def upper, lower
        if (value?[0] instanceof ArrayList){
            upper = ((ArrayList) super.to) [-1]
            lower = ((ArrayList) super.from) [0]
            nested = true
        } else {
            upper =  super.to as ArrayList
            lower =  super.from as ArrayList
        }

        long currentRow, currentColumn, currentZindex
        long upperBoundOfRows, lowerBoundOfRows, upperBoundOfColumns, lowerBoundOfColumns, upperBoundOfZindex, lowerBoundOfZindex
        upperBoundOfZindex = upper?[2] ? upper[2] as long : 0
        lowerBoundOfZindex = lower?[2] ? lower[2] as long : 0

        upperBoundOfRows = upper?[1] ? upper[1] as long : 0
        lowerBoundOfRows = lower?[1] ? lower[1] as long : 0

        upperBoundOfColumns = upper[0] as long
        lowerBoundOfColumns = lower[0] as long

        ComparableArrayList element = new ComparableArrayList()

        if (value instanceof ArrayList) {
            if (upper instanceof ArrayList && lower instanceof ArrayList) {
                if (nested) {
                    currentZindex = value[0]?[2]? value[0][2] as long: 0
                    currentRow = value[0]?[1] ? value[0][1] as long : 0
                    currentColumn = value[0][0] as long
                } else if (value?[0] instanceof Number) {
                    currentZindex = value?[2] ? value[2] as long : 0
                    currentRow = value?[1] ? value[1] as long : 0
                    currentColumn = value?[0] ? value[0] as long : 0
                }
                if (nested) {
                    if (currentRow - 1 >= lowerBoundOfRows) {
                        element.add([currentColumn, currentRow - 1])
                    } else if (currentColumn - 1 >= lowerBoundOfColumns) {
                        element.add([currentColumn - 1, lowerBoundOfRows])
                    } else {
                        element.add([[]])
                    }
                } else {
                    if (currentRow - 1 >= lowerBoundOfRows) {
                        element.addAll([currentColumn, currentRow - 1])
                    } else if (currentColumn - 1 >= lowerBoundOfColumns) {
                        element.addAll([currentColumn - 1, lowerBoundOfRows])
                    } else {
                        element.add()
                    }
                }
            } else if (upper instanceof Number && lower instanceof Number) {
                def currentValue = value as int
                def decrementedValue = currentValue - 1
                upperBoundOfRows = upper?[1] ? upper[1] as long : 0
                lowerBoundOfRows = lower?[1] ? lower[1] as long : 0

                upperBoundOfColumns = upper?[0] ? upper[0] as long : 0
                lowerBoundOfColumns = lower?[0] ? lower[0] as long : 0

                //todo - need to think about how to handle this
                element.add(decrementedValue)
            }
        } else {
            //to - cant previous on value - fix this
            return InvokerHelper.invokeMethod(value, "previous", null)
        }
        return element

    }

    @Override
    String inspect() {
        String toText = InvokerHelper.inspect(to)
        String fromText = InvokerHelper.inspect(from)
        return reverse ? "new ListRange (from, to) -- " + toText + ".." + fromText: "new ListRange (from, to) -- " + fromText + ".." + toText
    }

    @Override
    String toString() {
        return reverse ? "" + to + ".." + from : "" + from + ".." + to
    }
}
