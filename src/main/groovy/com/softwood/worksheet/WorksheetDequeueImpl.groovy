package com.softwood.worksheet

import java.util.concurrent.ConcurrentLinkedDeque
import java.util.stream.Stream

class WorksheetDequeueImpl implements Worksheet {

    static ConcurrentLinkedDeque<Worksheet> worksheets = new ConcurrentLinkedDeque<Worksheet>()
    static defaultWorksheet = new WorksheetDequeueImpl ("--Default Worksheet--")
    static {
        worksheets.add(defaultWorksheet)
    }


    private Optional<String> name
    private ConcurrentLinkedDeque<Table> tables = new ConcurrentLinkedDeque<Table>()

    /**
     * constructor
     * @param name - optional name for the worksheet
     */
    WorksheetDequeueImpl (final String name = null) {
        if (name) {
            this.name = Optional.of (name)
        }

        worksheets.add(this)
        this
    }

    /**
     * static methods to add or remove a ws from the the static list of worksheets
     * @param ws
     */
    static void addWorksheet (Worksheet ws) {
        if (!worksheets.contains(ws)) {
            worksheets.add(ws)
        }
    }

    static boolean removeWorksheet (Worksheet ws) {
        if (worksheets.contains(ws)) {
            worksheets.remove(ws)
        } else
            false
    }

    /**
     * delete this worksheet - and remove from static list if present
     *
     */
    void delete () {
        removeWorksheet(this)
        tables = null
        name = Optional.of("--deleted--")
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
        table.linkWorksheet(this)
    }

    boolean deleteTable (Table table) {
        tables.remove(table)
        table.unlinkWorksheet ()
    }

    Optional<Table> findTable (String name) {
        tables?.stream().filter(table -> table.name == name).findFirst()
    }

    Stream<Table> stream() {
        tables.stream()
    }


}
