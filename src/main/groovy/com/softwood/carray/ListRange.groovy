package com.softwood.carray

import groovy.transform.EqualsAndHashCode
import groovy.transform.InheritConstructors
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
    public Iterator iterator() {
        return new Iterator() {
            private int index;
            private Object value = reverse ? to : from;

            public boolean hasNext() {
                return index < size();
            }

            public Object next() {
                if (index++ > 0) {
                    if (index > size()) {
                        value = null;
                    } else {
                        if (reverse) {
                            value = decrement(value);
                        } else {
                            value = increment(value);
                        }
                    }
                }
                return value;
            }

            public void remove() {
                ListRange.this.remove(index);
            }
        }
    }

    @ Override
    public Comparable get(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + " should not be negative")
        }
        if (index >= size()) {
            throw new IndexOutOfBoundsException("Index: " + index + " is too big for range: " + this)
        }
        Object value;
        if (reverse) {
            value = to

            for (int i = 0; i < index; i++) {
                value = decrement(value)
            }
        } else {
            value = from;
            for (int i = 0; i < index; i++) {
                value = increment(value)
            }
        }
        return value
    }

    public int size() {
        if (size == -1) {
            if ((from instanceof Integer || from instanceof Long)
                    && (to instanceof Integer || to instanceof Long)) {
                // let's fast calculate the size
                long fromNum = ((Number) from).longValue()
                long toNum = ((Number) to).longValue()
                size = (int) (toNum - fromNum + 1)
            } else if (from instanceof Character && to instanceof Character) {
                // let's fast calculate the size
                char fromNum = (Character) from;
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
                ArrayList upper = to.getElements() [-1] as ArrayList
                ArrayList lower = from.getElements() [0] as ArrayList
                int upperBoundOfRows = upper[1]
                int lowerBoundOfColumns = lower[0]
                int numberOfColumns = (upper[0] - lower[0]) + 1
                int numberOfRows = (upper[1] - lower[1]) + 1
                size = (numberOfColumns*numberOfRows)
            }
            else {
                // let's lazily calculate the size
                size = 0;
                Comparable first = from;
                Comparable value = from;
                while (compareTo(to, value) >= 0) {
                    value = (Comparable) increment(value)
                    size++
                    if (compareTo(first, value) >= 0) break // handle back to beginning due to modulo incrementing
                }
            }
        }
        return size;
    }

    public String inspect() {
        String toText = InvokerHelper.inspect(to);
        String fromText = InvokerHelper.inspect(from);
        return reverse ? "" + toText + ".." + fromText : "" + fromText + ".." + toText;
    }

    /**
     * Increments by one
     *
     * @param value the value to increment
     * @return the incremented value
     */
    protected Object increment(Object value) {
        return InvokerHelper.invokeMethod(value, "next", null);
    }

    /**
     * Decrements by one
     *
     * @param value the value to decrement
     * @return the decremented value
     */
    protected Object decrement(Object value) {
        return InvokerHelper.invokeMethod(value, "previous", null);
    }


}
