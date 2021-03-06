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
        Worksheet ws = table.getWorksheet().orElse(null)

        then:
        table.name == "my first table"
        ws.name == "--Default Worksheet--"
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
        table.getCell([2,3]).valueAsText == "cell 2:3"
    }

    def "test multiplier action " () {
        given:
        Table table = new TableHashMapImpl()

        when:
        table.setName ("my third table")
        def c = table.setCell([2,1], "cell 2:1")
        c = table.setCell([2,2], 2)
        c = table.setCell([2,3], 4.0)

        DatasetRow row = table.getRow(3)
        DatasetColumn col = table.getColumn (2)
        col.times (2)

        then:
        row.stream().count() == 1
        col.stream().count() == 3
        table.getCell([2,3]).value == 8.0D
        table.getCell([2,2]).value == 4.0D
        table.getCell([2,1]).value == "cell 2:1"
    }

    def "test multiplierAsLong action " () {
        given:
        Table table = new TableHashMapImpl()

        when:
        table.setName ("my third table")
        def c = table.setCell([2,1], "cell 2:1")
        c = table.setCell([2,2], 2.5)
        c = table.setCell([2,3], 4.0)

        DatasetRow row = table.getRow(3)
        DatasetColumn col = table.getColumn (2)
        col.timesAsLong (2)

        then:
        row.stream().count() == 1
        col.stream().count() == 3
        table.getCell([2,3]).value == 8L
        table.getCell([2,2]).value == 5L
        table.getCell([2,1]).value == "cell 2:1"
    }

    def "test intersection of two tables action " () {
        given:
        Table table1 = new TableHashMapImpl()
        Table table2 = new TableHashMapImpl()

        when:
        table1.setName ("my primary table")
        table2.setName ("my secondary table")

        def c = table1.setCell([0,0], "cell 0:0 primary")
        table1.setCell([1,0], "cell 1:0 primary")
        c = table2.setCell([1,0], "cell 1:0 - secondary")
        c = table2.setCell([2,0], "cell 2:0 - secondary")

        Optional<Table> intersection = table1.intersectionByKey(table2)
        Table t = intersection.orElse (new TableHashMapImpl())
        Cell ref = t.getCell(1,0)

        then:
        t.cellsGrid.size() == 1
        t.getCell(1,0).valueAsText == "cell 1:0 primary"
    }
}
