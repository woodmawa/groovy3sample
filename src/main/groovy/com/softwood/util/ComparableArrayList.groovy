package com.softwood.util

import groovy.transform.EqualsAndHashCode
import groovy.transform.InheritConstructors

import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.ConcurrentLinkedQueue

@InheritConstructors
@EqualsAndHashCode(includeFields = true)
class ComparableArrayList<E> extends ArrayList implements List, Comparable {


    Optional<String> name = Optional.empty()

    void setName (String givenName) {
        name = Optional.of (givenName)
    }

    String getName () {
        name.orElse ("--Unnamed--")
    }

    ComparableArrayList (Collection args) {
        if (args) super.addAll(args)
        this
    }

    Object[] getElements () {
        super.toArray()
    }

    int size() {
        getElements().size()
    }

    /**
     * if no entries in the list then its empty
     * @return true or false
     */
    boolean isEmpty () {
        size() == 0
    }

    @Override
    int compareTo(Object obj) {
        if (this.size() < obj.size()) {
            -1
        } else if (this.size() > obj.size()) {
            + 1
        } else {

            def res = 0
            for (int i=0; i< obj.size(); i++ ) {
                def m,n
                n = obj[i]
                m = this[i]
                if (m instanceof List && n instanceof List)
                    res = compareTwoEqualSizedLists(m, n)
                else if (m < n ) {
                    res = -1
                } else if (m > n) {
                    res = 1
                } else {
                    res = 0
                }
                if (res != 0)
                    break
            }
            res
        }
    }

    private int compareTwoEqualSizedLists (List a, List b) {
        if (a.equals(b))
            return 0
        def result
        for (int i=0; i< a.size(); i++ ) {
            def m = a[i]
            def n = b[i]
            if (m instanceof List && n instanceof List ) {
                return compareTwoEqualSizedLists (m, n)
            } else {
                if (m.equals(n))
                    result = 0
                else if (m < n)
                    result = -1
                else
                    result = 1
            }
            if (result != 0)
                break
        }
        result
    }

    @Override
    Iterator<E> iterator() {
        return new ArrayListIterator()
    }

    ListIterator<E> listIterator() {iterator() as List }

    // inner class
    @InheritConstructors
    private class ArrayListIterator implements java.util.Iterator<E> {

        int current = 0;
        def parentList = ComparableArrayList.this
        def elements = parentList.toArray()

        ArrayListIterator () {
            //println "new ArrayListIterator constructor called "
            this
        }
        public boolean hasNext() {
            def psize = parentList.size()
            boolean compare = current < psize

            //println "hasNext() was called index at ($current) where parentsSize is ($psize)"

            return compare
        }

        public E next() {
            if (!hasNext()) throw new java.util.NoSuchElementException()

            def elem = elements[current]
            current++

            return elem
        }

        public void remove() {
            ComparableArrayList.this.remove(--current) // reference the outer class
        }
    }

    def asType (clazz) {
        if (clazz == ArrayList || clazz == Collection || clazz == List) {
            return ArrayList<E>.of (this)
        }
        if (clazz == Object[]) {
            return toArray()
        }
        if (clazz == ConcurrentLinkedQueue || clazz == Queue) {
            new ConcurrentLinkedQueue<E> (getElements() as Collection)
        }
        if (clazz == ConcurrentLinkedDeque || clazz == Deque) {
            new ConcurrentLinkedDeque<E> (getElements() as Collection)
        }

        throw new ClassCastException("ComparableArrayClass cannot be coerced into $clazz")
    }

    @Override
    String toString() {
        def firstElement = elements[0]
        "ComparableArrayList (name: ${getName()}) size:${size()} (starts at $firstElement)"
    }
}
