package com.softwood.worksheet

import spock.lang.Specification

class TestCoOrdinateSpecification extends Specification {

    def "basic Cordinate test" () {
        given:
        CoOrdinate c1 = new CoOrdinate(0,0)
        CoOrdinate c2 = [0,0] as CoOrdinate
        CoOrdinate c3

        when:
        c3 = c1.translate(1,1)

        then:
        c1.equals(c2)
        [1,1] == c3.twoDimensionalCoOrdinateAsList

    }


    def "with new Cell" () {
        given:
        Cell cell1 = new Cell (0, 0, "cell 0:0")
        Cell cell2 = new Cell([1,1], "cell 1:1")

        when:
        cell1.name = "first cell"
        CoOrdinate cOrd = [2,2] as CoOrdinate

        then:
        cell1.cellReference == new CoOrdinate (0,0)
        cell1.name == "first cell"
        cell1.valueAsText == "cell 0:0"
        [2,2]  == cOrd.twoDimensionalCoOrdinateAsList
        new Tuple2 (2,2).equals( cOrd.twoDimensionalTuple)

    }

    def "as type convertion test" (){
        given:
        CoOrdinate cOrd = [2,2] as CoOrdinate

        when:
        List arr = cOrd as List
        Tuple tup = cOrd as Tuple

        then:
        arr == [2,2,0]
        tup.equals(new Tuple(2,2,0))

    }
}
