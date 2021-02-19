package com.softwood.worksheet

import spock.lang.Specification
import com.softwood.worksheet.*
import static java.util.stream.Collectors.*

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
        def origin = table.setCell([0,0], "origin")
        DatasetRow row = table.getRow(0)
        DatasetColumn col = table.getColumn(0)
        List<Cell> l = row.cellsAsList
        Cell cell1 = l?[0]
        Cell cell2 = col.cellsAsList?[0]

        then:
        table.name == "my second table"
        table.rows.size() == 1
        table.columns.size() == 1
        row.size() == 1
        l.size() == 1
        l[0].valueAsText == "origin"
        cell1.getName() == "--Unnamed Cell--"
        cell1.equals(cell2)
        cell1 == table.getCell([0,0])
        cell1 == table.getCell(0,0)

    }

    def "row and column streams test " () {
        given:
        Table table = new TableHashMapImpl()

        when:
        table.setName ("my second table")
        def origin = table.setCell([0,0], "origin")
        DatasetRow row = table.getRow(0)
        DatasetColumn col = table.getColumn(0)

        then:
        row.stream().count() == 1
        col.stream().count() == 1
        col.name == "--UnNamed Column--"
        row.name == "--UnNamed Row--"
        table.stream().count() == 1

    }

    def "add two cells in same row" () {
        given:
        Table table = new TableHashMapImpl()

        when:
        table.setName ("my third table")
        def c = table.setCell([2,1], "cell 2:1")
         c = table.setCell([2,2], "cell 2:2")
        c = table.setCell([2,3], "cell 2:3")

        DatasetRow row = table.getRow(3)
        DatasetColumn col = table.getColumn (2)
        List colValList = col.stream().map(ce -> ce.valueAsText).collect(toList())

        List row3ValList = row.stream().map(ce -> ce.valueAsText).collect(toList())


        then:
        row.stream().count() == 1
        col.stream().count() == 3
        colValList == ["cell 2:1","cell 2:2", "cell 2:3"]
        row3ValList == ["cell 2:3"]
        table.stream().count() == 3

    }
}
