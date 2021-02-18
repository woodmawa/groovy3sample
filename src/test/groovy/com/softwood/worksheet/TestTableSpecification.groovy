package com.softwood.worksheet

import spock.lang.Specification
import com.softwood.worksheet.*

class TestTableSpecification extends Specification {

    def "new table test" () {
        given:
        Table table = new TableHashMapImpl()

        when:
        table.setName ("my first table")

        then:
        table.name == "my first table"
    }

    def "new table test with one cell" () {
        given:
        Table table = new TableHashMapImpl()


        when:
        table.setName ("my second table")
        table.setCell([0,0], "origin")
        DatasetRow row = table.getRow(0)
        List<Cell> l = row.cellsAsList

        then:
        table.name == "my second table"
        table.rows.size() == 1
        table.columns.size() == 1
        row.size() == 1
        l.size() == 1
        l[0].valueAsText == "origin"


    }
}
