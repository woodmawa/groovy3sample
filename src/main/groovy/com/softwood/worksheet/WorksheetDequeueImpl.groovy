package com.softwood.worksheet

import java.util.concurrent.ConcurrentLinkedDeque
import java.util.stream.Stream

class WorksheetDequeueImpl implements Worksheet {
    private Optional<String> name
    private ConcurrentLinkedDeque<Table> tables = new ConcurrentLinkedDeque<Table>()


    void setName (final String name) {
        this.name = Optional<String>.of(name)
    }

    String getName () {
        name.orElse("--Unnamed Worksheet--")
    }

    void addTable (Table table) {
        tables.add(table)
    }

    boolean deleteTable (Table table) {
        tables.remove(table)
    }

    Optional<Table> findTable (String name) {
        tables?.stream().filter(table -> table.name == name).findFirst()
    }

    Stream<Table> stream() {
        tables.stream()
    }
}
