package com.softwood.carray

import groovy.transform.EqualsAndHashCode
import groovy.transform.InheritConstructors
import groovy.transform.ToString
import org.codehaus.groovy.runtime.InvokerHelper

@InheritConstructors
@EqualsAndHashCode (includeFields = true)
class ListRange<E> extends ObjectRange  implements Range<Comparable>{
    //Object[] elements = super.toArray().sort()         //get private reference to supers elements

    int size = -1 //will be -1 if not computed

    ListRange (ComparableArrayList from, ComparableArrayList to){
        super(from, to)
        this
    }

    @ Override
    Comparable getFrom () {
        super.from
    }

    @ Override
    Comparable getTo () {
        super.to
    }

    //returns new anonymous inner class morphed to Iterator
    Iterator<E> iterator() {
        def iter = new Iterator() {
            private int index
            private Object value = reverse ? to : from
            private ListRange parent = ListRange.this  //get the containing class instance


            boolean hasNext() {
                return index < parent.size()
            }

            Object next() {
                if (index++ > 0) {
                    if (index > parent.size()) {
                        value = null
                    } else {
                        if (reverse) {
                            value = decrement(value)
                        } else {
                            value = increment(value)
                        }
                    }
                }
                return value
            }

            void remove() {
                ListRange.this.remove(index)
            }
        }
        return iter
    }

    @ Override
    Comparable get(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + " should not be negative")
        }
        if (index >= size()) {
            throw new IndexOutOfBoundsException("Index: " + index + " is too big for range: " + this)
        }
        List value
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
                boolean nested = false
                def upper, lower
                if (from?[0] instanceof ArrayList) {
                    //its nested array  of array so get last and first entries
                    upper = to.getElements() [-1]
                    lower = from.getElements() [0]
                    nested = true
                } else {
                    //not nested so just get the entry as an array directly
                    upper = to.getElements() as ArrayList
                    lower = from.getElements() as ArrayList
                }
                if (upper instanceof ArrayList && lower instanceof ArrayList ) {
                    int upperBoundOfRows = upper[1] as int
                    int lowerBoundOfRows = lower[1] as int
                    int upperBoundOfColumns = upper[0] as int
                    int lowerBoundOfColumns = lower[0] as int
                    int numberOfColumns = (upperBoundOfColumns - lowerBoundOfColumns) + 1
                    int numberOfRows = (upperBoundOfRows - lowerBoundOfRows) + 1
                    size = (numberOfColumns * numberOfRows)
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
        int size = 0
        Comparable first = from
        Comparable value = from
        while (compareTo(to, value) >= 0) {
            value = (Comparable) increment(value)
            size++
            if (compareTo(first, value) >= 0) break // handle back to beginning due to modulo incrementing
        }
        size
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

        int currentRow, currentColumn
        int upperBoundOfRows, lowerBoundOfRows, upperBoundOfColumns, lowerBoundOfColumns
        upperBoundOfRows = upper[1] as int
        lowerBoundOfRows = lower[1] as int

        upperBoundOfColumns = upper[0] as int
        lowerBoundOfColumns = lower[0] as int

        ComparableArrayList element = new ComparableArrayList()

        if (value instanceof ArrayList) {
            if (upper instanceof ArrayList && lower instanceof ArrayList) {
                if (nested) {
                    currentRow = value[0][1]
                    currentColumn = value[0][0]
                } else if (value[0] instanceof Number) {
                    currentRow = value[1] as int
                    currentColumn = value[0] as int
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
                        element.add()
                    }
                }
                return element
            } else if (upper instanceof Number && lower instanceof Number) {
                def currentValue = value as int
                def incrementedValue = currentValue + 1
                upperBoundOfRows = upper[1] as int
                lowerBoundOfRows = lower[1] as int

                upperBoundOfColumns = upper[0] as int
                lowerBoundOfColumns = lower[0] as int

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

        int currentRow, currentColumn
        int upperBoundOfRows, lowerBoundOfRows, upperBoundOfColumns, lowerBoundOfColumns
        upperBoundOfRows = upper[1] as int
        lowerBoundOfRows = lower[1] as int

        upperBoundOfColumns = upper[0] as int
        lowerBoundOfColumns = lower[0] as int

        ComparableArrayList element = new ComparableArrayList()

        if (value instanceof ArrayList) {
            if (upper instanceof ArrayList && lower instanceof ArrayList) {
                if (nested) {
                    currentRow = value[0][1]
                    currentColumn = value[0][0]
                } else if (value[0] instanceof Number) {
                    currentRow = value[1] as int
                    currentColumn = value[0] as int
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
                return element
            } else if (upper instanceof Number && lower instanceof Number) {
                def currentValue = value as int
                def decrementedValue = currentValue - 1
                upperBoundOfRows = upper[1] as int
                lowerBoundOfRows = lower[1] as int

                upperBoundOfColumns = upper[0] as int
                lowerBoundOfColumns = lower[0] as int

                //todo - need to think about how to handle this
                element.add(decrementedValue)
                return element
            }
        } else {
            //to - cant previous on value - fix this
            return InvokerHelper.invokeMethod(value, "previous", null)
        }
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
