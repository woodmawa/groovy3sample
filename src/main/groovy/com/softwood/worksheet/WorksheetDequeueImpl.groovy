package com.softwood.worksheet

import java.util.concurrent.ConcurrentLinkedDeque
import java.util.stream.Stream

class WorksheetDequeueImpl implements Worksheet {

    static ConcurrentLinkedDeque<Worksheet> worksheets = new ConcurrentLinkedDeque<Worksheet>()
    static defaultMasterWorksheet = new WorksheetDequeueImpl ("--Default Worksheet--")

    private Optional<String> name = Optional.empty()
    private ConcurrentLinkedDeque<Table> tables = new ConcurrentLinkedDeque<Table>()

    /**
     * constructor
     * @param name - optional name for the worksheet
     */
    WorksheetDequeueImpl (final String name = null) {
        if (name) {
            this.name = Optional.of (name)
        }

        if ( !worksheets.contains(this) ) {
            worksheets.add(this)
        }
        this
    }

    /**
     * return reference to static default worksheet
     * @return defaultWorksheet
     */
    Worksheet getDefaultWorksheet () {
        defaultMasterWorksheet
    }

    Collection<Worksheet> getWorksheets () {
        this.@worksheets.asImmutable()
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
        tables.clear()
        name = Optional.of("--Deleted Worksheet--")
    }

    void setName (final String name) {
        this.name = Optional<String>.of(name)
    }

    String getName () {
        name.orElse("--UnNamed Worksheet--")
    }

    List<Table> getTables() {
        tables.asList().asImmutable()
    }

    void addTable (Table table) {
        table.linkWorksheet(this)
        if (!tables.contains(table))
            tables.add(table)
    }

    boolean deleteTable (Table table) {
        table.unlinkWorksheet ()
        tables.remove(table)
    }

    Optional<Table> findTable (String name) {
        tables?.stream().filter(table -> table?.name == name).findFirst()
    }

    Stream<Table> stream() {
        tables.stream()
    }

    String toString() {
        "Worksheet ($name)"
    }

}
