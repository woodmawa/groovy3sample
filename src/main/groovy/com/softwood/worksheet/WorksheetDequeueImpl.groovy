package com.softwood.worksheet

import java.util.concurrent.ConcurrentLinkedDeque
import java.util.stream.Stream

class WorksheetDequeueImpl implements Worksheet {

    static ConcurrentLinkedDeque<Worksheet> worksheets = new ConcurrentLinkedDeque<Worksheet>()

    private Optional<String> name
    private ConcurrentLinkedDeque<Table> tables = new ConcurrentLinkedDeque<Table>()

    static void addWorksheet (Worksheet ws) {
        if (!worksheets.contains(ws)) {
            worksheets.add(ws)
        }
    }

    WorksheetDequeueImpl (final String name = null) {
        if (name) {
            this.name = Optional.of (name)
        }

        worksheets.add(this)
        this
    }

    static boolean removeWorksheet (Worksheet ws) {
        if (worksheets.contains(ws)) {
            worksheets.remove(ws)
        } else
            false
    }
    
    void delete () {
        removeWorksheet(this)
    }

    void setName (final String name) {
        this.name = Optional<String>.of(name)
    }

    String getName () {
        name.orElse("--Unnamed Worksheet--")
    }

    List<Table> getTables() {
        tables.asList().asImmutable()
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
