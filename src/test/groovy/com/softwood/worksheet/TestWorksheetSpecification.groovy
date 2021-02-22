package com.softwood.worksheet

import spock.lang.Specification

class TestWorksheetSpecification extends Specification {

    def "new worksheet " () {
        given:
        Worksheet ws = new WorksheetDequeueImpl()
        Table table = new TableHashMapImpl()

        when:
        ws.addTable(table)
        ws.name = "worksheet 1"

        then:
        ws.stream().count() == 1
        ws.name == "worksheet 1"
        ws.worksheets.size() == 2 //default + this one 
        ws.defaultWorksheet.name == "--Default Worksheet--"

        and:
        //detach this worksheet from master static list
        ws.removeWorksheet(ws)

        then:
        ws.worksheets.size() == 0

    }

    def "add named worksheet " () {
        given:
        Worksheet ws = new WorksheetDequeueImpl()
        Table table = new TableHashMapImpl()
        table.name = "myTable"

        when:
        ws.addTable(table)
        ws.name = "worksheet 2"

        Optional<Table> opt = ws.findTable("myTable")

        then:
        ws.stream().count() == 1
        ws.name == "worksheet 2"
        opt.isPresent()
        opt.get().name == "myTable"
        opt.get() == table
        ws.tables[0] == table
    }
}
