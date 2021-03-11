package com.softwood.util

import spock.lang.Specification

class TestListRangeSpecification extends Specification {

    def "basic list range " () {
        given :

        ListRange lr0 = new ListRange ([0,0], [1,0])  //essentially row is bounded to zero so can only change column
        ListRange lr1 = new ListRange ([0,0], [1,1])  //row, and column can vary

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
    }
}
