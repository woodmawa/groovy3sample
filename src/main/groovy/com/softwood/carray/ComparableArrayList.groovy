package com.softwood.carray

import groovy.transform.InheritConstructors

@InheritConstructors
class ComparableArrayList extends ArrayList implements Comparable {

    @Override
    List next () {
        //this
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
                long m,n
                n = obj[i]
                m = this[i]
                if (m == n ) {
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
}
