package com.softwood.util

import com.softwood.worksheet.CellCoOrdinate
import spock.lang.Specification

class TestComparableArrayList extends Specification {

    def "test comparing two lists " () {
        given:
        ComparableArrayList l1 = new ComparableArrayList([0,1])
        ComparableArrayList l2 = new ComparableArrayList([0,2])
        ComparableArrayList l3 = new ComparableArrayList([0,1,2])

        expect:

        l1 == l1
        l2 > l1     //last digit is greater than l1 entry
        l1 < l3     //l2 is smaller size than l3
        l1 instanceof ArrayList

    }

    def "test compound arrays" () {
        given:
        ComparableArrayList l0 = new ComparableArrayList([[0,1], [0,1]])
        ComparableArrayList l1 = new ComparableArrayList([[0,1], [0,2]])
        ComparableArrayList l2 = new ComparableArrayList([[0,1], [0,2]])
        ComparableArrayList l3 = new ComparableArrayList([[0,1], [0,2], [0,3]])
        ComparableArrayList l4 = ComparableArrayList.of (l3)
        ComparableArrayList l5 = ComparableArrayList.of (l3)

        def ans = l5.remove([0,3])      //returns true if done

        expect:
        l0 < l2
        l1 == l2
        l2 < l3
        ans == true
        l2 == l5

    }


    def "test compound arrays2" () {
        given:
        ComparableArrayList l0 = new ComparableArrayList([[0,1], [0,1], [0,2]])

        expect:
        l0.size() == 3
        l0.get(0) == l0[0]
        l0[0] instanceof ArrayList
        l0[0].size() == 2

    }

    def "test as Type conversion " () {
        given:
        ComparableArrayList l0 = new ComparableArrayList([[0,1], [0,1], [0,2]])

        ArrayList converted = l0.asType( ArrayList)
        Object[]  elements = l0.asType (Object[])

        expect:
        converted == [[0,1], [0,1], [0,2]]
        converted.getClass () == ArrayList
        elements.getClass() == Object[]
        elements.size() == 3

    }

    def "test static of() " () {
        given:
        ComparableArrayList l0 = ComparableArrayList.of ([[0, 1], [0, 1], [0, 2]])

        ArrayList converted = l0.asType(ArrayList)
        Object[] elements = l0.asType(Object[])
        Queue q = l0 as Queue

        expect:
        converted == [[0, 1], [0, 1], [0, 2]]
        converted.getClass() == ArrayList
        elements.getClass() == Object[]
        elements.size() == 3
        q.size() == 3
    }

    def "test creation of ComparableArrayList using  CellCoOrdinates " () {
        given:
        ComparableArrayList cal = ComparableArrayList.of (new CellCoOrdinate (1,1,1) )

        ArrayList<CellCoOrdinate> converted = cal as ArrayList
        CellCoOrdinate first = converted[0]

        expect:
        first.getAs3DList() == [1, 1, 1]
    }

    def "test strings not int " () {
        given:
        ComparableArrayList cal1 = ComparableArrayList.of (["abc", "def"])
        ComparableArrayList caldup1 = ComparableArrayList.of (["abc", "def"])

        ComparableArrayList cal2 = ComparableArrayList.of (["abc", "ghi"])

        expect:
        cal1 < cal2
        cal1.compareTo(caldup1) == 0
    }
}
