package WorkSheet

import groovy.transform.EqualsAndHashCode
import groovy.transform.Immutable
import groovy.transform.MapConstructor

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentNavigableMap

@MapConstructor
@EqualsAndHashCode  //need this to ensure unique access via the cells map
class CellCoOrdinate {
    //todo use tuple
    long x
    long y
    long z

    CellCoOrdinate(final List coOrds) {
        x = coOrds?[0] ?: 0
        y = coOrds?[1] ?: 0
        z = coOrds?[2] ?: 0
    }

    CellCoOrdinate(final x, final y, final z = 0) {
        this.x = x as long
        this.y = y as long
        this.z = z as long
    }

    /**
     * moves the cell co-ordinates relative to existing location
     * @param dx
     * @param dy
     * @param dz
     */
    CellCoOrdinate move (final long dx, final long dy, final long dz=0L) {
        x = x + dx
        y = y + dy
        z = z + dz
        this
    }

    List get2DReference () {
        //new Immutable
        [x,y] as Immutable
    }

    Tuple get2DTuple () {
        new Tuple2 (x,y)
    }

    List get3DReference () {
        [x, y, z] as Immutable
    }

    Tuple get3DTuple () {
        new Tuple3 (x,y,z)
    }

    String toString () {
        "CellCoOrdinate[$x,$y,$z]"
    }

    CellCoOrdinate asType (Class clazz) {
        assert clazz
        if (clazz instanceof List) {
            new CellCoOrdinate(ref)
        } else null
    }
}

/**
 * cells can be optionally named
 * CellCoOrdinate:value pairing
 *
 */
class Cell {
    Optional<String> name = Optional.ofNullable(null)
    CellCoOrdinate cellReference
    def value

    void setName (final String name) {
        this.name = Optional<String>.of(name)
    }

    void updateValue (final update) {
        value = update
    }

    String getValueAsText () {
        String BLANK_STRING = ""
        if (value) {
            value.toString()
        } else {
            BLANK_STRING
        }
    }

    String toString () {
        "cell @{$cellReference : $value}"
    }

}

class DatasetRow<Long, Cell> {
    Optional<String> name = Optional.empty()
    long rowNumber
    ConcurrentHashMap rowCells = new ConcurrentHashMap<Long, Cell> ()

    void setName (final String name) {
        this.name = Optional.of (name)
    }

    void putCell (final Cell cell) {
        assert cell
        long row = cell.cellReference.x
        long col = cell.cellReference.y

        if (rowNumber == row) {
            rowCells.put(col, cell)
        }
    }
}

class DatasetColumn<Long, Cell> {
    Optional<String> name = Optional.empty()
    long columnNumber
    ConcurrentHashMap columnCells = new ConcurrentHashMap<Long, Cell> ()

    void setName (final String name) {
        this.name = Optional.of (name)
    }

    void putCell (final Cell cell) {
        assert cell
        long row = cell.cellReference.x
        long col = cell.cellReference.y

        if (columnNumber == col) {
            columnCells.put(row, cell)
        }
    }
}

/**
 * table is named block of cells in a grid.  cells are stored in a map
 * a cell instance can be indexed by its CellCoOrdinate
 */
class Table {

    String name
    ConcurrentHashMap rows = new ConcurrentHashMap<long, DatasetRow>()
    ConcurrentHashMap columns = new ConcurrentHashMap<long, DatasetColumn>()

    Closure error = {println "error didnt save cell ref - if was invalid $it.cellReference", "error invalid co-ordinate for cell"}
    Closure success = {println "all went well saved $it in table", "OK"}

    //look at jigsaw table to help here
    ConcurrentHashMap cellsGrid = new ConcurrentHashMap<CellCoOrdinate, Cell>()

    void setColumnName (long colNumber, final String name) {

        DatasetColumn col = columns.get (colNumber)
        col.setName(name)
    }

    void setRowName (long rowNumber, final String name) {

        DatasetRow row = rows.get (rowNumber)
        row.setName(name)
    }

    DatasetColumn getColumn (long colNumber) {

        columns.get (colNumber)
    }

    DatasetRow getRow (long rowNumber) {

        def row = rows.get (rowNumber)
        row
    }

    private void addCellToRow (long rowNumber, Cell cell) {
        DatasetRow row = rows.get(rowNumber)
        if (row) {
            row.putCell(cell)
        } else {
            row = new DatasetRow()
            row.rowNumber = rowNumber
            row.putCell(cell)
            rows.put (rowNumber, row)

        }
    }

    private void addCellToColumn (long colNumber, Cell cell) {
        DatasetColumn col = columns.get(colNumber)
        if (col) {
            col.putCell(cell)
        } else {
            col = new DatasetColumn()
            col.columnNumber = colNumber
            col.putCell(cell)
            columns.put(colNumber, col)

        }
    }

    void setCell (Cell cell) {
        if (cell.cellReference) {
            cellsGrid.put (cell.cellReference, cell)
            addCellToRow (cell.cellReference.x, cell)
            addCellToColumn (cell.cellReference.y, cell)

            cell
        }
        else {
            error (cell)
        }
    }

    void setCell (final List ref, def value) {
        CellCoOrdinate coOrdRef = new CellCoOrdinate(ref)
        Cell cell = new Cell (cellReference: coOrdRef, value: value )
        setCell (cell)
    }

    void setCell (CellCoOrdinate coOrdRef, value) {
        Cell cell = new Cell (cellReference: coOrdRef, value: value )
        setCell (cell)
    }

    Cell getCell (final List ref) {
        CellCoOrdinate coOrdRef = new CellCoOrdinate(ref)
        cellsGrid[coOrdRef]
    }

    Cell getCell (final CellCoOrdinate coOrdRef) {
        cellsGrid[coOrdRef]
    }


}

CellCoOrdinate origin = new CellCoOrdinate (0,0)


Table table = new Table(name:'myTab')
table.setCell([0,0], 10)
table.setCell([1,1], "cell 1:1")
table.setCell([1,2], "cell 1:2")
Cell c = table.getCell([0,0])
Cell c2 = table.getCell(origin)

table.setRowName(1,"row 1")

DatasetRow row = table.getRow (1)
println "got row " + row.name

println table.name
println c
println c2
println table.getCell([1,1]).valueAsText