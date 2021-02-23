package com.softwood.worksheet

import java.util.concurrent.ConcurrentLinkedDeque
import java.util.stream.Stream

/**
 * Worksheet is  a container for tables
 * 
 * a defaultMasterWorksheet is automatically setup, and any new table defaults to this worksheet
 *
 *
 */
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
        tables?.clear()
        name = Optional.of("--Deleted Worksheet--")
    }

    void setName (final String name) {
        this.name = Optional<String>.of(name)
    }

    String getName () {
        name.orElse("--UnNamed Worksheet--")
    }

    List<Table> getTables() {
        tables?.asList().asImmutable()
    }

    void addTable (Table table) {
        Optional<Worksheet> tableWorksheet = table?.worksheet
        tableWorksheet.ifPresent({ tableCurrentWorksheet ->
            if (this != tableCurrentWorksheet) {
                //adding a table to another worksheet
                tableCurrentWorksheet.removeTable(table)
            }
        })


        //set tables worksheet to this worksheet
        table.setWorksheet(this)

        //if this table is not in this worksheets list of tables, add it
        if (!tables?.contains(table))
            tables?.add(table)

    }

    boolean removeTable (Table table) {
        table.unlinkWorksheet ()
        tables?.remove(table)
    }

    boolean deleteTable (Table table) {
        table.unlinkWorksheet ()
        tables?.remove(table)
    }

    Optional<Table> findTable (String name) {
        tables?.stream().filter(table -> table?.name == name).findFirst()
    }

    Stream<Table> streamOfTables() {
        tables?.stream()
    }

    Stream<Worksheet> streamOfWorksheets() {
        worksheets?.stream()
    }

    String toString() {
        "Worksheet ($name)"
    }

}
