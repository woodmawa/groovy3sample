package com.softwood.util.scripts

import com.softwood.util.ListRange
import org.codehaus.groovy.runtime.IteratorClosureAdapter

/*
 * what step does is get an iterator and runs through the ListRange and calls the adapterClosure
 * which adds each new item into the closures internal list of iterated values
 */
void step (ListRange lr, int step, Closure closure){
    final Iterator<Comparable> iter = new StepIterator(lr, step)

    boolean res = iter.hasNext()
    while (iter.hasNext()) {
        def nxt = iter.next()
        closure.call(nxt)  //adds next value in ListRange into closures internal list
    }
}


ListRange lr = new ListRange([0,0], [2,2])

/*
* what this does is take the arg and makes it the delegate of teh closure
*
* further when doCall(arg)  is invoked, doCall adds that arg to an internal list of each arg.
* the toList() returns the list of all the added args
*/
IteratorClosureAdapter<Comparable> adapter = new IteratorClosureAdapter<Comparable>(lr)

assert adapter instanceof Closure
assert adapter.delegate.is (lr)     //closure's delegate is set to the ListRange
List lst =  adapter.asList()
assert lst.size() == 0 //starts empty

step(lr, 3, adapter)

//when step is executed it takes the iterated values in ListRange adds them to adapterClosures internal list
lst =  adapter.asList()

println "adapterClosures collected list is " + lst
println "all done in script "

//internal iterator for stepping through ListRange generated entries
class StepIterator implements Iterator<Comparable> {
    // actual step, can be +1 when desired step is -1 and direction is from high to low
    private final int step
    private final ObjectRange range
    private int index = -1
    private Comparable value
    private boolean nextFetched = true

    StepIterator(ObjectRange range, final int desiredStep) {
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
            for (int i = 0; i < step; i++) {
                peekValue = (Comparable) range.increment(peekValue)
                // handle back to beginning due to modulo incrementing
                if (range.compareTo(peekValue, range.from) <= 0) return null
            }
            if (range.compareTo(peekValue, range.to) <= 0) {
                return peekValue
            }
        } else {
            final int positiveStep = -step
            Comparable peekValue = value
            for (int i = 0; i < positiveStep; i++) {
                peekValue = (Comparable) range.decrement(peekValue)
                // handle back to beginning due to modulo decrementing
                if (range.compareTo(peekValue, range.to) >= 0) return null
            }
            if (range.compareTo(peekValue, range.from) >= 0) {
                return peekValue
            }
        }
        return null
    }
}

