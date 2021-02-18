package com.softwood.worksheet

import spock.lang.Specification
import com.softwood.worksheet.*

class TestTableSpecification extends Specification {

    def "new table test with one cell" () {
        given:
        Table table = new TableHashMapImpl()

        when:
        table.setName ("my first table")

        then:
        table.name == "my first table"
    }
}
