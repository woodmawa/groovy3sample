package com.softwood.util

import spock.lang.Specification

class TestListRangeSpecification extends Specification {

    def "basic list range " () {
        given :

        ListRange lr0 = new ListRange ([0,0], [1,0])  //essentially row is bounded to zero so can only change column
        ListRange lr1 = new ListRange ([0,0], [1,1])  //row, and column can vary

        ArrayList results = []
        for (i in lr1){
            results << i
        }

        expect:
        lr0.from == [0,0]
        lr0.to == [1,0]
        lr0.gradient == RangeGradient.upward
        lr0.size == 2
        lr0.reverse == false

        and:
        lr1.size() == 4
        lr1.reverse == false
        lr1.listRangeFillStyle == ListRangeFill.byRowFirst
        lr1.get(2) == [1,0]
        lr1[-2] == [1,0]
        results.size() == 4
        results.toString() == [[0,0],[0,1],[1,0],[1,1]].toString()

    }

    def "negative range test " () {
        given:
        ListRange lr = new ListRange([-1,-1],[0,0])

        ArrayList results = []
        for (i in lr){
            results << i
        }

        expect:
        results.toString() == [[-1,-1],[-1,0],[0,-1],[0,0]].toString()


    }

    def "test column fill first "() {
        given:
        ListRange lr = new ListRange([0,0],[1,1])
        lr.setListRangeFillStyle(ListRangeFill.byColumnFirst)

        ArrayList results = []
        for (i in lr){
            results << i
        }

        expect:
        results.toString() == [[0,0],[1,0],[0,1],[1,1]].toString()
    }

    def "test reverse range  "() {
        given:
        ListRange lr = new ListRange([0,0],[1,1], true)

        ArrayList results = []
        for (i in lr){
            results << i
        }

        expect:
        results.reverse()
        results.toString() == [[1,1],[1,0],[0,1],[0,0]].toString()
    }



    def "test range with downward gradient range   "() {
        given:
        ListRange lr = new ListRange([1,1],[0,0])

        ArrayList results = []
        for (i in lr){
            results << i
        }

        expect:
        results.toString() == [[1,1],[1,0],[0,1],[0,0]].toString()
    }

    def "test range with upward gradient range   "() {
        given:
        ListRange lr = new ListRange([0,0],[1,1])

        ArrayList results = []
        for (i in lr){
            results << i
        }

        expect:
        results.toString() == [[0,0],[0,1],[1,0],[1,1]].toString()
    }

    def "test reverse range with downward gradient range    "() {
        given:
        ListRange lr = new ListRange([1,1],[0,0], true)

        ArrayList results = []
        for (i in lr){
            results << i
        }

        expect:
        results.toString() == [[0,0],[0,1],[1,0],[1,1]].toString()
    }

    def "test reverse range with upward gradient range    "() {
        given:
        ListRange lr = new ListRange([0,0],[1,1], true)

        ArrayList results = []
        for (i in lr){
            results << i
        }

        expect:
        results.toString() == [[1,1],[1,0],[0,1],[0,0]].toString()
    }

    def "test contains within range    "() {
        given:
        ListRange lr = new ListRange([0,0],[1,1])


        expect:
        lr.contains([1,1])
        lr.contains([1,0])
        lr.contains([0,1])
        !lr.contains([0,2])
    }

    def "test sublist within range    "() {
        given:
        ListRange lr = new ListRange([0,0],[1,2])
        ListRange revlr = new ListRange([0,0],[1,2], true)

        List sl1 = lr.subList(1,2)
        List sl2 = revlr.subList(1,2)


        expect:
        sl1.toString () == [[0,1],[0,2]].toString()
        lr.reverse == false
        lr.gradient == RangeGradient.upward

        sl2.toString () == [[1,1],[1,0]].toString()

    }


    def "test step by increment through a range " () {
        given:
        ListRange lr = new ListRange([0,0],[1,2])

        List steps = lr.step (2)

        expect:
        steps.toString() == [[0,0],[0,2],[1,1]].toString()
    }
}
