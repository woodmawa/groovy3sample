package com.softwood.worksheet

import spock.lang.Specification

class TestCellCoOrdinateSpecification extends Specification {

    def "basic Cordinate test" () {
        given:
        CellCoOrdinate c1 = new CellCoOrdinate(0,0)
        CellCoOrdinate c2 = [0, 0] as CellCoOrdinate
        CellCoOrdinate c3

        when:
        c3 = c1.translate(1,1)

        then:
        c1.equals(c2)
        [1,1] == c3.as2DList

    }


    def "with new Cell" () {
        given:
        Cell cell1 = new Cell (0, 0, "cell 0:0")
        Cell cell2 = new Cell([1,1], "cell 1:1")

        when:
        cell1.name = "first cell"
        CellCoOrdinate cOrd = [2, 2] as CellCoOrdinate

        then:
        cell1.cellReference == new CellCoOrdinate (0,0)
        cell1.name == "first cell"
        cell1.valueAsText == "cell 0:0"
        [2,2]  == cOrd.as2DList
        new Tuple2 (2,2).equals( cOrd.as2DTuple)

    }

    def "as type conversion test" (){
        given:
        CellCoOrdinate cOrd = [2, 2] as CellCoOrdinate

        when:
        List arr = cOrd as List
        Tuple tup = cOrd as Tuple<Long>

        then:
        arr == [2,2,0]
        tup.equals(new Tuple(2,2,0))

    }

    def "add and subtract locations" () {
        given:
        CellCoOrdinate cOrd1 = [1, 1] as CellCoOrdinate
        CellCoOrdinate cOrd2 = [2, 2] as CellCoOrdinate

        when:
        CellCoOrdinate add = cOrd1 + cOrd2
        CellCoOrdinate minus = cOrd2 - cOrd1
        CellCoOrdinate translated = cOrd1.translate(5,5)

        then:
        add.getAs2DList() == [3, 3]
        minus.getAs2DList() == [1, 1]
        translated.getAs2DList() == [6, 6]

        and:
        CellCoOrdinate relocated = cOrd1.relocate(5,5)  //relocates moves this instance to new place

        then:
        relocated.is(cOrd1)
        !translated.is(cOrd1)
        relocated == translated  //have same co-ord values but no the same instances
    }
}
