package com.softwood.carray

import com.softwood.worksheet.CellCoOrdinate
import groovy.transform.EqualsAndHashCode
import groovy.transform.InheritConstructors
import org.codehaus.groovy.runtime.InvokerHelper
import org.codehaus.groovy.runtime.IteratorClosureAdapter
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation

import java.util.stream.Collectors

enum Gradient {
    upward,
    downward
}

enum Direction {
    forward,
    backward
}

enum ListRangeFill {
    byRowFirst,
    byColumnFirst
}

//@InheritConstructors
@EqualsAndHashCode (includeFields = true)
//ObjectRange
class ListRange<E> extends AbstractList  implements Range<Comparable>{
    private long size = -1 //will be -1 if not computed

    //Indicates whether this is a reverse range which iterates backwards starting from the 'to' value and ending on the 'from' value
    protected boolean reverse = false
    protected ComparableArrayList to
    protected ComparableArrayList from
    protected Gradient gradient
    protected ListRangeFill processFillEntries = ListRangeFill.byRowFirst  //default
    private LinkedHashMap arrayIndexLimits

    /**
     * calculates the upper and lower bound ranges when calcualting how to increment/decrement to next entry in range
     * @param low - lower bound ComparableArrayList
     * @param hi- - upper bound ComparableArrayList
     * @return hash of upper and lower bounds for each column radix in the list, indexed by radix column number
     */
    private LinkedHashMap calcArrayIndexRange (low, hi) {
        assert hi.size() == low.size()

        HashMap arrayIndexLimits =  [:]
        for (i in 0..<hi.size()){
            //start with x, y, z

            def upper = hi[i]
            def lower = low[i]
            def range = [:]  //java.util.LinkedHashMap
            range << [upper: upper]
            range << [lower: lower]
            arrayIndexLimits << [(i): range]
        }

        arrayIndexLimits
    }

    ListRange (CellCoOrdinate startCoOrd, CellCoOrdinate endCoOrd, reverseDirection = false) {
        assert startCoOrd
        assert endCoOrd
        List start, end
        start = startCoOrd.getThreeDimensionalCoOrdinateAsList()
        end = endCoOrd.getThreeDimensionalCoOrdinateAsList()

        if (start.size() != end.size()) {
            throw new ExceptionInInitializerError ("to and from array list sizes must have the same number of elements")
        }
        reverse = reverseDirection
        from = new ComparableArrayList(start)
        to = new ComparableArrayList(end)
        calculateGradient()
        size()
        this
    }

    ListRange (def fromObj, def toObj, boolean reverseDirection = false) {
        assert fromObj
        assert toObj

        ArrayList start, end
        start = ArrayList.of (fromObj)
        end = ArrayList.of (toObj)

        ListRange(start, end, reverseDirection)
    }

    ListRange (List fromAL, List toAL, boolean reverseDirection = false) {
        assert fromAL
        assert toAL
        if (fromAL.size() != toAL.size()) {
            throw new ExceptionInInitializerError ("to and from array list sizes must have the same number of elements")
        }
        reverse = reverseDirection
        from = new ComparableArrayList(fromAL)
        to = new ComparableArrayList(toAL)
        calculateGradient()
        size()
        this
    }


    ListRange (ComparableArrayList fromCAL, ComparableArrayList toCAL, boolean reverseDirection = false){
        assert fromCAL
        assert toCAL
        if (fromCAL.size() != toCAL.size()) {
            throw new ExceptionInInitializerError ("to and from array list sizes must have the same number of elements")
        }

        //Indicates whether this is a reverse range which iterates backwards starting from the 'to' value and ending on the 'from' value
        reverse = reverseDirection
        from = fromCAL
        to = toCAL
        calculateGradient()
        size()
        this
    }

    /**
     * determines whether the range is going upwards or downwards
     *
     * @return gradient from 'from' to 'to'
     */
    private calculateGradient () {
        def result = from.compareTo (to)
        if (result < 0)
            gradient = Gradient.upward
        else
            gradient = Gradient.downward
    }

    @ Override
    Comparable getFrom () { from }

    @ Override
    Comparable getTo () { to }

    @Override
    boolean isReverse() {
        return reverse
    }

    void setListRangeFillStyle (ListRangeFill style) {
        assert style
        processFillEntries = style
    }

    ListRangeFill getListRangeFillStyle () {
        processFillEntries
    }

    //use protected checkBoundaryCompatibility () from ObjectRange parent
    //https://github.com/apache/groovy/blob/master/src/main/java/groovy/lang/ObjectRange.java

    //returns new anonymous inner class morphed to Iterator
    Iterator<E> iterator() {
        new com.softwood.carray.ListRange.StepIterator(this, 1)
    }

    @Override
    boolean add (Object o) {
        //can't add into a range
        return false
    }

    /**
     * Non-thread-safe iterator which lazily produces the next element only on calls of hasNext() or next()
     */
    private static final class StepIterator implements Iterator<Comparable> {
        // actual step, can be +1 when desired step is -1 and direction is from high to low
        private final int step
        private final ListRange range
        private int index = -1
        private ComparableArrayList value
        private boolean nextFetched = true
        private LinkedHashMap arrayIndexLimits

        private StepIterator(ListRange range, final int desiredStep) {
            if (desiredStep == 0 && range.from.compareTo (range.to) != 0) {
                throw new GroovyRuntimeException("Infinite loop detected due to step size of 0")
            }
            this.range = range
            if (range.isReverse()) {
                step = -desiredStep
            } else {
                step = desiredStep
            }

            if(range.gradient = Gradient.upward)
                arrayIndexLimits = range.calcArrayIndexRange(range.from, range.to)
            else
                //todo - do i need to do this reorder?
                arrayIndexLimits = range.calcArrayIndexRange(range.to, range.from)

            //if reverse range then from will be larger value than the to
            //value = range.getFrom()
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
            if (++index > range.size() - 1 ) {
                throw new NoSuchElementException()
            }
            if (!hasNext()) {
                throw new NoSuchElementException()
            }
            nextFetched = false
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
                ComparableArrayList peekValue = value
                if (peekValue >= range.to)  //if cached value has reached the upper limit, nothing more to peek
                    return null

                int compared
                for (int i = 0; i < step; i++) {
                    peekValue = (ComparableArrayList) range.increment(range.processFillEntries, arrayIndexLimits, peekValue)
                    // handle back to beginning due to modulo incrementing
                    if (peekValue == null || peekValue.isEmpty() )
                        return null
                    if (peekValue.compareTo(range.to) > 0)
                        return null
                }
                if (range.gradient == Gradient.upward) {
                    if (peekValue.compareTo(range.to) <= 0) {
                        return peekValue
                    }
                } else {
                    if (peekValue.compareTo(range.from) >= 0) {
                        return peekValue
                    }
                }
            } else {
                final int positiveStep = -step
                ComparableArrayList peekValue = value
                if (peekValue <= range.from)  //if cached value has reached the upper limit, nothing more to peek
                    return null

                for (int i = 0; i < positiveStep; i++) {
                    peekValue = (ComparableArrayList) range.decrement(range.processFillEntries, arrayIndexLimits, peekValue)
                    // handle back to beginning due to modulo decrementing
                    if (peekValue == null || peekValue.isEmpty() )
                        return null
                    if (peekValue.compareTo(range.from) < 0)
                        return null
                }
                if (range.gradient == Gradient.upward) {
                    //stepping backwards on upward gradient so check against 'from' limit
                    if (peekValue.compareTo(range.from) >= 0) {
                        return peekValue
                    }
                } else {
                    //stepping backwards on downward gradient so check against 'to' limit
                    if (peekValue.compareTo(range.to) >= 0) {
                        return peekValue
                    }
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

        LinkedHashMap arrayIndexLimits = calcArrayIndexRange(from, to)
        if (reverse) {
            //if nested array unpack first level and get first element
            if (from[0] instanceof List)
                value = from[0]
            else
                value = from

            for (int i = 0; i < index; i++) {
                value = decrement(processFillEntries, arrayIndexLimits, value)
            }
        } else {
            //if nested array unpack first level and get first element
            if (from[0] instanceof List)
                value = from[0]
            else
                value = from
            for (int i = 0; i < index; i++) {
                value = increment(processFillEntries, arrayIndexLimits, value)
            }
        }
        return value as ComparableArrayList
    }

    void add(int index, ComparableArrayList element) {
        throw new UnsupportedOperationException("can't add an element on ListRange, elements are computed based on 'to' and 'from' values ")
    }

    Object set(int index, ComparableArrayList element) {
        throw new UnsupportedOperationException("can't set element on ListRange, elements are computed based on 'to' and 'from' values ")
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
            if (from instanceof ComparableArrayList || to instanceof ComparableArrayList) {
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

                ComparableArrayList difference = calculateDifference(upper, lower)

                if (upper instanceof ArrayList && lower instanceof ArrayList ) {
                    size = difference
                            .collect{Math.abs(it)+1}
                            .inject(1){carryOver, value -> carryOver * value}


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

    private ComparableArrayList calculateDifference (ArrayList a, ArrayList b) {
        ComparableArrayList difference = new ComparableArrayList()
        for (i in 0..a.size()-1) {
            difference << (a[i] - b[i])
        }
        difference
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
        if (step == 0 && from.compareTo(to) == 0) {
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

        Comparable endValue, startValue
        startValue = iter.next()  //start with pre cached start value
        int i = 0
        for (; i < fromIndex; i++) {
            if (!iter.hasNext()) {
                throw new IndexOutOfBoundsException("Index: " + i + " is too big for range: " + this)
            }
            startValue = iter.next()
        }

        for (; i < toIndex; i++) {
            boolean hasMore = iter.hasNext()
            if (!hasMore) {
                throw new IndexOutOfBoundsException("Index: " + i + " is too big for range: " + this)
            }
            endValue = iter.next()
        }

        if (reverse)
            return new ListRange(endValue, startValue, reverse)
        else
            return new ListRange (startValue, endValue)
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
            final int result = from.compareTo((Comparable) value)
            return result == 0 || result < 0 && to.compareTo((Comparable) value) >= 0
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
    protected Object increment(fillBy, LinkedHashMap arrayIndexLimits, Object arrayValue) {
        /*  value might be a [[x,y]] or just [x,y] */
        boolean nested = false
        def upperAV, lowerAV
        if (arrayValue?[0] instanceof ArrayList){
            upperAV = ((ArrayList) to) [-1]
            lowerAV = ((ArrayList) from) [0]
            nested = true
        } else {
            upperAV =  to as ArrayList
            lowerAV =  from as ArrayList
        }

        if (reverse) {
            //switch order so lower < upper for the calculation
            def temp = upperAV
            upperAV = lowerAV
            lowerAV = temp
        }

        ArrayList next = ArrayList.copyOf(arrayValue)  //create a copy of the received arrayValue
        int startingColumn
        int currentColumn

        switch (fillBy) {
            case ListRangeFill.byColumnFirst:

                startingColumn = 0
                currentColumn = 0

                def highLow = arrayIndexLimits[startingColumn]
                int upper = highLow['upper']
                int lower = highLow['lower']

                //start with currentValue as value in the column 0
                def columnValue = next[currentColumn]

                for (col in 0..<next.size()) {
                    if (columnValue < upper) {
                        next[currentColumn] = columnValue + 1
                        break
                    }
                    else {
                        //if we have stepped past the last column
                        if (currentColumn + 1 >= next.size())
                            return null

                        next[currentColumn++] = lower
                        columnValue =next[currentColumn] //get the start value point for next column
                        highLow = arrayIndexLimits[currentColumn]
                        upper = highLow['upper']
                        lower = highLow['lower']
                    }
                }
                break

            case ListRangeFill.byRowFirst:
                startingColumn = 1
                currentColumn = 1

                def highLow = arrayIndexLimits[startingColumn]
                int upper = highLow['upper']
                int lower = highLow['lower']

                //start with currentValue as value in the column 0
                def columnValue = next[currentColumn]

                for (col in 1..<next.size()) {
                    if (columnValue < upper) {
                        next[currentColumn] = columnValue + 1
                        break
                    }
                    else {
                        //if we have stepped past the last column
                        if (currentColumn + 1 >= next.size())
                            return null

                        next[currentColumn++] = lower  //, reset this column, and post increment to the start value point for next column
                        //if we are processing the rows - handle the row column precedence first before handling columns 2...n
                        if (col == 1 && next[0] < arrayIndexLimits[0]['upper']) {
                            next[0] = next[0] + 1
                            break
                        } else {
                            next[0] = arrayIndexLimits[0]['lower']
                        }
                        columnValue =next[currentColumn] //get the start value point for next column
                        highLow = arrayIndexLimits[currentColumn]
                        upper = highLow['upper']
                        lower = highLow['lower']
                    }
                }
                break

            default:
                next = null
        }

        next

 /*       long currentRow, currentColumn, currentZindex
        long upperBoundOfRows, lowerBoundOfRows, upperBoundOfColumns, lowerBoundOfColumns
        long upperBoundOfZindex, lowerBoundOfZindex
        upperBoundOfRows = upper?[1] ? upper[1] as long: 0  //set as 0 if no rows
        lowerBoundOfRows = lower?[1] ? lower[1] as long: 0

        upperBoundOfColumns = upper?[0]? upper[0] as long : 0
        lowerBoundOfColumns = upper?[0]? lower[0] as long : 0

        upperBoundOfZindex = upper?[2] ? upper[2] as long : 0
        lowerBoundOfZindex = lower?[2] ? lower[2] as long: 0

        boolean multiDimensional = false

        ComparableArrayList element = new ComparableArrayList()

        if (value instanceof ArrayList) {
            if (upper instanceof ArrayList && lower instanceof ArrayList) {
                if (nested) {
                    currentZindex = value[0]?[2] ? value[0][2] as long : 0
                    currentRow = value[0]?[1] ? value[0][1] as long: 0
                    currentColumn = value[0]?[0] ? value[0][0] as long: 0
                    multiDimensional = (lower?[0].size () > 2 && upper?[0].size() > 2)
                } else if (value[0] instanceof Number) {
                    currentZindex = value?[2] ? value[2] as long : 0
                    currentRow = value?[1] ? value[1] as long : 0
                    currentColumn = value?[0] ? value[0] as long : 0
                    multiDimensional = (lower.size () > 2 && upper.size() > 2)
                }
                if (nested) {
                    if (currentRow + 1 <= upperBoundOfRows) {
                        element.add([currentColumn, currentRow + 1])
                    } else if (currentColumn + 1 <= upperBoundOfColumns) {
                        element.add([currentColumn + 1, lowerBoundOfRows])
                    }
                } else {
                    if (multiDimensional) {
                        //todo
                        if (currentRow + 1 > upperBoundOfRows && currentColumn + 1 > upperBoundOfColumns && currentZindex + 1 <= upperBoundOfZindex) {
                            element.addAll (lowerBoundOfRows, lowerBoundOfColumns, currentZindex + 1)
                        } else {
                            if (currentRow + 1 <= upperBoundOfRows) {
                                element.addAll([currentColumn, currentRow + 1, currentZindex])
                            } else if (currentColumn + 1 <= upperBoundOfColumns) {
                                element.addAll([currentColumn + 1, lowerBoundOfRows, currentZindex])
                            }
                        }
                    } else {
                        if (currentRow + 1 <= upperBoundOfRows) {
                            element.addAll([currentColumn, currentRow + 1])
                        } else if (currentColumn + 1 <= upperBoundOfColumns) {
                            element.addAll([currentColumn + 1, lowerBoundOfRows])
                        }
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

            }
        } else {
            //todo - cant call next on value, have to use an iterator
            return InvokerHelper.invokeMethod(value, "next", null)
        }
        return element
*/
    }

    /**
     * Decrements by one
     *
     * @param value the value to decrement
     * @return the decremented value
     */
    protected Object decrement(fillBy, LinkedHashMap arrayIndexLimits, Object arrayValue) {
        /*  value might be a [[x,y]] or just [x,y] */
        boolean nested = false
        def upperAV, lowerAV
        if (arrayValue?[0] instanceof ArrayList){
            upperAV = ((ArrayList) to) [-1]
            lowerAV = ((ArrayList) from) [0]
            nested = true
        } else {
            upperAV =  to as ArrayList
            lowerAV =  from as ArrayList
        }

        if (gradient == Gradient.downward) {
            //switch order so lower < upper for the calculation
            def temp = upperAV
            upperAV = lowerAV
            lowerAV = temp
        }

        ArrayList next = ArrayList.copyOf(arrayValue)
        int startingColumn
        int currentColumn

        switch (fillBy) {
            case ListRangeFill.byColumnFirst:

                startingColumn = 0
                currentColumn = 0

                def highLow = arrayIndexLimits[startingColumn]
                int upper = highLow['upper']
                int lower = highLow['lower']

                //start with currentValue as value in the column 0
                def columnValue = arrayValue[currentColumn]

                for (col in 0..<next.size()) {
                    if (columnValue > lower) {
                        next[currentColumn] = columnValue - 1
                        break
                    }
                    else {
                        //if we have stepped past the last column
                        if (currentColumn + 1 >= next.size())
                            return null

                        next[currentColumn++] = upper
                        columnValue =next[currentColumn] //get the start value point for next column

                        highLow = arrayIndexLimits[currentColumn]
                        upper = highLow['upper']
                        lower = highLow['lower']
                    }
                }
                break

            case ListRangeFill.byRowFirst:
                startingColumn = 1
                currentColumn = 1

                def highLow = arrayIndexLimits[startingColumn]
                int upper = highLow['upper']
                int lower = highLow['lower']

                //start with currentValue as value in the column 0
                def columnValue = arrayValue[currentColumn]

                for (col in 1..<next.size()) {
                    if (columnValue > lower) {
                        next[currentColumn] = columnValue - 1
                        break
                    }
                    else {
                        //if we have stepped past the last column
                        if (currentColumn + 1 >= next.size())
                            return null

                        next[currentColumn++] = upper  //, reset this column, and post increment to the start value point for next column
                        //if we are processing the rows - handle the row column precedence first before handling columns 2...n
                        if (col == 1 && next[0] > arrayIndexLimits[0]['lower']) {
                            next[0] = next[0] - 1
                            break
                        } else {
                            next[0] = arrayIndexLimits[0]['upper']
                        }
                        columnValue =next[currentColumn] //get the start value point for next column
                        highLow = arrayIndexLimits[currentColumn]
                        upper = highLow['upper']
                        lower = highLow['lower']
                    }
                }
                break

            default:
                next = null
        }

        next
    }

    @Override
    String inspect() {
        String toText = InvokerHelper.inspect(to)
        String fromText = InvokerHelper.inspect(from)
        return reverse ? "new ListRange (from, to) -- " + toText + ".." + fromText: "new ListRange (from, to) -- " + fromText + ".." + toText
    }

    @Override
    String toString() {
        return reverse ? "" + to + ".." + from + "(R)" : "" + from + ".." + to
    }
}
