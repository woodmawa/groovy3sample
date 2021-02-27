package com.softwood.carray

import groovy.transform.EqualsAndHashCode
import groovy.transform.InheritConstructors
import groovy.transform.ToString
import org.codehaus.groovy.runtime.InvokerHelper

@InheritConstructors
@EqualsAndHashCode (includeFields = true)
class ListRange extends ObjectRange  implements Range<Comparable>{
    //Object[] elements = super.toArray().sort()         //get private reference to supers elements

    int size = -1 //will be -1 if not computed

    ListRange (ComparableArrayList from, ComparableArrayList to){
        super(from, to)
        this
    }

    @ Override
    Comparable getFrom () {
        def from = super.from
        from
    }

    @ Override
    Comparable getTo () {
        def to = super.to
        to
    }

    //returns new anonymous inner class morphed to Iterator
    Iterator iterator() {
        return new Iterator() {
            private int index
            private Object value = reverse ? to : from

            boolean hasNext() {
                return index < size()
            }

            Object next() {
                if (index++ > 0) {
                    if (index > size()) {
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
            value = to

            for (int i = 0; i < index; i++) {
                value = decrement(value)
            }
        } else {
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
                def upper = to.getElements() [-1]
                def lower = from.getElements() [0]
                if (upper instanceof ArrayList && lower instanceof ArrayList ) {
                    int upperBoundOfRows = upper[1] as int
                    int lowerBoundOfColumns = lower[0] as int
                    int numberOfColumns = (upper[0] - lower[0]) + 1
                    int numberOfRows = (upper[1] - lower[1]) + 1
                    size = (numberOfColumns * numberOfRows)
                } else if (upper instanceof Number && lower instanceof Number) {
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
        Comparable first = from
        Comparable value = from
        while (compareTo(to, value) >= 0) {
            value = (Comparable) increment(value)
            size++
            if (compareTo(first, value) >= 0) break // handle back to beginning due to modulo incrementing
        }
    }

    @Override
    String inspect() {
        String toText = InvokerHelper.inspect(to)
        String fromText = InvokerHelper.inspect(from)
        return reverse ? "" + toText + ".." + fromText : "" + fromText + ".." + toText
    }

    /**
     * Increments by one
     *
     * @param value the value to increment
     * @return the incremented value
     */
    @Override
    protected Object increment(Object value) {

        println "range increment for $value of class ${value.class}"

        if (value instanceof ArrayList) {
            def upper = ((ArrayList) super.to) [-1]
            def lower = ((ArrayList) super.from) [0]
            ComparableArrayList element = new ComparableArrayList()
            if (upper instanceof ArrayList && lower instanceof ArrayList) {
                int upperBoundOfRows = upper[1] as int
                int lowerBoundOfRows = lower[1] as int

                int upperBoundOfColumns = upper[0] as int
                int lowerBoundOfColumns = lower[0] as int

                int currentRow
                int currentColumn
                boolean multiArrayValue = false
                if (value[0] instanceof ArrayList) {
                    currentRow = value[0][1]
                    currentColumn = value[0][0]
                    multiArrayValue = true
                } else if (value[0] instanceof Number) {
                    currentRow = value[1] as int
                    currentColumn = value[0] as int

                    //todo - can this happen ?
                }
                if (multiArrayValue) {
                    if (currentRow + 1 <= upperBoundOfRows) {
                        element.add([[currentColumn, currentRow + 1]])
                    } else if (currentColumn + 1 <= upperBoundOfColumns) {
                        element.add([[currentColumn + 1, lowerBoundOfRows]])
                    } else {
                        element.add([[]])
                    }
                } else {
                    if (currentRow + 1 <= upperBoundOfRows) {
                        element.add([currentColumn, currentRow + 1])
                    } else if (currentColumn + 1 <= upperBoundOfColumns) {
                        element.add([currentColumn + 1, lowerBoundOfRows])
                    } else {
                        element.add([])
                    }
                }
                return element
            }
        } else
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
        return InvokerHelper.invokeMethod(value, "previous", null)
    }

    @Override
    String toString() {
        return reverse ? "" + to + ".." + from : "" + from + ".." + to
    }
}
