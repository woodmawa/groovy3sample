package com.softwood.worksheet

import spock.lang.Specification

class TestWorksheetSpecification extends Specification {

    def "new worksheet " () {
        given:
        Worksheet ws = new WorksheetDequeueImpl()
        Table table = new TableHashMapImpl()

        when:
        def initialName = ws.name
        ws.addTable(table)
        ws.name = "worksheet 1"

        then:
        initialName == "--UnNamed Worksheet--"
        ws.streamOfTables().count() == 1
        ws.name == "worksheet 1"
        ws.worksheets.size() == 2 //default + this one
        ws.defaultWorksheet.name == "--Default Worksheet--"

        and:
        //detach this worksheet from master static list
        ws.delete()

        then:
        ws.worksheets.size() == 1
        ws.name == "--Deleted Worksheet--"

    }

    def "add named table to new worksheet" () {
        given:
        Worksheet ws = new WorksheetDequeueImpl()
        Table table = new TableHashMapImpl()
        table.name = "myTable"

        when:
        ws.name = "worksheet 2"     //name the ws
        ws.addTable(table)          //add the table

        Optional<Table> opt = ws.findTable("myTable")

        then:
        ws.streamOfTables().count() == 1    //confirm only one table in ws
        ws.name == "worksheet 2"
        opt.isPresent()
        opt.get().name == "myTable"
        opt.get() == table
        ws.tables[0] == table
    }

    def "move table between two worksheets" () {
        given:
        Worksheet ws1 = new WorksheetDequeueImpl("worksheet 1")
        Worksheet ws2 = new WorksheetDequeueImpl("worksheet 2")
        Table table = new TableHashMapImpl()
        table.name = "myTable"

        when:
        ws1.addTable(table)          //assign table from default to the ws
        Worksheet defaultWS = WorksheetDequeueImpl.defaultMasterWorksheet
        List defaultTables = defaultWS.tables

        Optional<Table> opt = ws1.findTable("myTable")

        then:
        defaultTables.size() == 0
        ws1.streamOfTables().count() == 1    //confirm only one table in ws
        ws1.name == "worksheet 1"
        opt.isPresent()
        opt.get().name == "myTable"
        opt.get() == table
        ws1.tables[0] == table

        when:
        ws2.addTable(table)
        Optional<Table> opt2 = ws2.findTable("myTable")


        then:
        ws2.streamOfTables().count() == 1
        opt2.isPresent()
        opt2.get().name == "myTable"
        ws2.tables[0] == table

        ws1.tables.size() == 0

    }
}
