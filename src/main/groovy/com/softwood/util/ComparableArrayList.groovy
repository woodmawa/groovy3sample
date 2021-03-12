package com.softwood.util

import groovy.transform.EqualsAndHashCode
import groovy.transform.InheritConstructors

@InheritConstructors
@EqualsAndHashCode(includeFields = true)
class ComparableArrayList<E> extends ArrayList implements List, Comparable {

    String name


    ComparableArrayList (List args) {
        super.addAll(args)
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

    def asType (Class clazz) {
        if (clazz == ArrayList) {
            return ArrayList.of (this)
        }
        if (clazz == Object[]) {
            return toArray()
        }
        throw new ClassCastException("User cannot be coerced into $clazz")
    }

    @Override
    String toString() {
        def firstElement = elements[0]
        "ComparableArrayList ${name ? "(name: $name)" : "(--unnamed--)"} size:${size()} (starts at $firstElement)"
    }
}
