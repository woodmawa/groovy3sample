package com.softwood.worksheet

import groovy.transform.EqualsAndHashCode

import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Stream
import static java.util.stream.Collectors.*

/**
 * table is named block of cells in a grid.  cells are stored in a map
 * a cell instance can be indexed by its CellCoOrdinate
 */
@EqualsAndHashCode (includeFields = true)
class TableHashMapImpl implements Table {

    private Optional<String> name = Optional.empty()
    private ConcurrentHashMap rows = new ConcurrentHashMap<long, DatasetRow>()
    private ConcurrentHashMap columns = new ConcurrentHashMap<long, DatasetColumn>()
    private Optional<Worksheet> currentWorksheet = Optional.of (WorksheetDequeueImpl.defaultMasterWorksheet)
    private Map<Long,Metadata> columnMetadata = new ConcurrentHashMap<Long, Metadata>()
    private boolean hasColumnHeaders = false

    //look at jigsaw table to help here
    private ConcurrentHashMap cellsGrid = new ConcurrentHashMap<CellCoOrdinate, Cell>()

    boolean hasError = false

    TableHashMapImpl () {super()}


    TableHashMapImpl (List<Cell> cellList) {

        assert cellList
        cellList.stream().forEach ({ cell ->
            addCellToColumn(cell)
            addCellToRow(cell)
            cellsGrid.put(cell.coOrdinate, cell)
        })
        this
    }

    /**
     * override the default worksheet assignment
     */
    void setWorksheet (Worksheet ws) {
        currentWorksheet.ifPresent (wsheet -> wsheet.removeTable(this))    //unlink from loosing worksheet list of tables
        currentWorksheet = Optional.ofNullable(ws)
    }

    Optional<Worksheet> getWorksheet () {
        currentWorksheet
    }

    boolean hasHeaders () {
        return columnMetadata.size() > 0
    }

    void clearError() {hasError = false}

    Closure error = {val, status -> println "$val", hasError=true, status}
    Closure success = {println "$it", hasError=false, "OK"}

    void setName (String name) {
        this.name = Optional.of (name)
    }

    String getName () {
        name.orElse("--UnNamed Table--")
    }

    void setColumnName (final long colNumber, final String name) {

        DatasetColumn col = columns?.get (colNumber)
        if (col)
            col.setName(name)
        else
            error.call ("error couldn't find column $colNumber", "No such Column")
    }

    void setRowName (final long rowNumber, final String name) {

        DatasetRow row = rows?.get (rowNumber)
        if (row)
            row.setName(name)
        else
            error.call ("error couldn't find row $rowNumber", "No such Row")
    }

    Map<CellCoOrdinate, Cell> getCellsGrid () {
        cellsGrid.asImmutable()
    }

    DatasetColumn getColumn (final long colNumber) {

        columns?.get (colNumber)
    }

    //direct index access
    DatasetRow getRow (final long rowNumber) {
        rows?[rowNumber]
    }

    /** not as efficient as it has to filter out looking for name match
     *
     * @param rowName
     * @return
     */
    //todo make this optional ?
    DatasetColumn getColumn (final String colName) {
        Optional res = columns.values().stream()
                .filter(col -> col.name == colName)
                .findFirst()

        def col = res.orElse(null)
        col
    }

    /** not as efficient as it has to filter out looking for name match
     *
     * @param rowName
     * @return
     */
    //todo make this optional ?
    DatasetRow getRow (final String rowName) {
         Optional res = rows.values().stream()
                 .filter(row -> row.name == rowName)
                 .findFirst()

        def row = res.orElse(null)
        row
   }

    private void addCellToRow (final long rowNumber, final Cell cell) {
        DatasetRow row = rows.get(rowNumber)
        if (row) {
            row.putCell(cell)
        } else {
            row = new DatasetRowHashMapImpl()
            row.rowNumber = rowNumber
            row.putCell(cell)
            rows.put (rowNumber, row)

        }
    }

    private void addCellToRow (final CellCoOrdinate coOrd, final Cell cell) {
        assert CellCoOrdinate
        assert cell.coOrdinate

        long rowNumber = coOrd.y
        addCellToRow (rowNumber, cell)
    }

    private void addCellToRow (final Cell cell) {
        assert cell
        assert cell.coOrdinate

        long rowNumber = cell.coOrdinate.y
        addCellToRow (rowNumber, cell)

    }

    private void addCellToColumn (final long colNumber, final Cell cell) {
        DatasetColumn col = columns.get(colNumber)
        if (col) {
            col.putCell(cell)
        } else {
            col = new DatasetColumnHashMapImpl()
            col.columnNumber = colNumber
            col.putCell(cell)
            columns.put(colNumber, col)

        }
    }

    private void addCellToColumn (final CellCoOrdinate coOrd, final Cell cell) {
        assert coOrd
        assert cell.coOrdinate

        long colNumber = coOrd.x
        addCellToColumn (colNumber, cell)
    }

    private void addCellToColumn (final Cell cell) {
        assert cell
        assert cell.coOrdinate

        long colNumber = cell.coOrdinate.x
        addCellToColumn (colNumber, cell)

    }

    void setCellList (List<Cell> cellList) {
        assert cellList
        cellList.stream().forEach ({ cell ->
            addCellToColumn(cell)
            addCellToRow(cell)
            cellsGrid.put(cell.coOrdinate, cell)
        })
    }

    Cell setCell (final long x_col_ref, final long y_row_ref, final def value) {
        CellCoOrdinate coOrdRef = new CellCoOrdinate(x_col_ref, y_row_ref)
        Cell cell = new Cell (coOrdRef, value )
        setCell (cell)
    }

    /**
     * main method for doing the work of setting a sell into the grid
     *
     * @param cell
     * @return
     */
    Cell setCell (final Cell cell) {
        if (cell.coOrdinate) {
            cellsGrid.put ((cell.coOrdinate), cell)
            //get row number (which is y axis ref) and add cell to this row
            addCellToRow (cell.coOrdinate.y, cell)
            //get column number (which is x axis ref) and add cell to this column
            addCellToColumn (cell.coOrdinate.x, cell)

            cell
        }
        else {
            error.call (cell, "error cell has invalid coOrdinate")
            cell
        }
    }

    Cell setCell (final List<Long> aref, def value) {
        CellCoOrdinate coOrdRef = new CellCoOrdinate(aref)
        Cell cell = new Cell (coOrdRef, value )
        setCell (cell)
    }

    Cell setCell (final CellCoOrdinate coOrdRef, def value) {
        Cell cell = new Cell (coOrdRef, value )
        setCell (cell)
    }

    //should this return optional<Cell>
    Cell getCell (final long x, final long y, final long z=0) {
        CellCoOrdinate coOrdRef = new CellCoOrdinate(x,y,z)
        cellsGrid[coOrdRef]
    }

    Cell getCell (final List<Long> ref) {
        CellCoOrdinate coOrdRef = new CellCoOrdinate(ref)
        cellsGrid[coOrdRef]
    }

    Cell getCell (final CellCoOrdinate coOrdRef) {
        cellsGrid[coOrdRef]
    }

    Optional<Table> intersectionByKey(final Table table2) {
        //Map<CellCoOrdinate, Cell> intersectionMap = this.cellsGrid.intersect(table2.cellsGrid)
        List<CellCoOrdinate> keys = cellsGrid.entrySet().stream()
                .map(entry -> entry.getKey())
                .collect(toList())
        List<CellCoOrdinate> keys2 = table2.cellsGrid.entrySet().stream()
                .map(entry -> entry.key)
                .collect(toList())

        List keyIntersect = keys.intersect(keys2)
        //cellsGrid.stream().map({ })

        Map intersectionMap = new ConcurrentHashMap<>()
        cellsGrid.entrySet().stream()
                .filter(entry -> keyIntersect.contains(entry.key))
                //.map(entry -> entry)
                //.collect(toMap(Map.Entry::getKey , Map.Entry::getValue))
                .forEach(entry -> intersectionMap.put(entry.key, entry?.value))
        Optional.ofNullable (buildTableRowsAndColumns (intersectionMap))
    }

    private Table buildTableRowsAndColumns (Map<CellCoOrdinate, Cell> mapOfCells) {
        if (mapOfCells) {
            TableHashMapImpl iTable = new TableHashMapImpl()
            //use private field access here to make sure its mutable
            //iTable.@cellsGrid.putAll(mapOfCells)
            mapOfCells.entrySet().stream()
                    .forEach(entry -> iTable.setCell(entry.value))
            iTable
        } else
            return null
    }

    void linkWorksheet (Worksheet ws) {
        this.setWorksheet (ws)
    }

    void unlinkWorksheet() {
        currentWorksheet = Optional.empty()
    }

    Stream<Cell> stream () {
        cellsGrid.values().stream()
    }

    String toString() {
        "Table ($name)"
    }
}
