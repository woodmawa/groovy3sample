package com.softwood.worksheet

import spock.lang.Specification

class TestCoOrdinateSpecification extends Specification {

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
        [2,2]  == cOrd.twoDimensionalReference
        new Tuple2 (2,2).equals( cOrd.twoDimensionalTuple)


    }
}
