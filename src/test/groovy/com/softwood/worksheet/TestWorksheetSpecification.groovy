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


    }
}
