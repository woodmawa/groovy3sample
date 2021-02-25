package com.softwood.carray

import groovy.transform.InheritConstructors

@InheritConstructors
class ComparableArrayList<E> extends ArrayList implements Comparable {

    String name

    ComparableArrayList (List args) {
        println "ComparableArrayList constructor called with list "
        super.addAll(args)
        this
    }

    Object[] getElements () {
        super.toArray()
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
                else if (m.equals(n) ) {
                    res = 0
                } else if (m < n) {
                    res = -1
                } else {
                    res = 1
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
        for (int i=0; i< a.size(); i++ ) {
            def m = a[i]
            def n = b[i]
            if (m instanceof List && n instanceof List ) {
                compareTwoEqualSizedLists (m, n)
            } else {
                if (m.equals(n))
                    return 0
                else if (m < n)
                    return -1
                else
                    return 1
            }
        }
    }

    @Override
    Iterator<E> iterator() {
        println "iterator() called to get a new Iterator"
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
            println "next() was called at index ($current) and about to return (${elements[current]})"
            current++

            return elem
        }

        public void remove() {
            ComparableArrayList.this.remove(--current) // reference the outer class
        }
    }

    String toString() {
        "ComparableArrayList (${name ?: "--unnamed--"}) (starts at $elements[0])"
    }
}
