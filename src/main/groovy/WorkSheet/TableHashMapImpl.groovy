package WorkSheet

import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Stream
import static java.util.stream.Collectors.*

/**
 * table is named block of cells in a grid.  cells are stored in a map
 * a cell instance can be indexed by its CoOrdinate
 */
class TableHashMapImpl implements Table {

    String name
    ConcurrentHashMap rows = new ConcurrentHashMap<long, DatasetRow>()
    ConcurrentHashMap columns = new ConcurrentHashMap<long, DatasetColumn>()

    Closure error = {println "error didnt save cell ref - if was invalid $it.cellReference", "error invalid co-ordinate for cell"}
    Closure success = {println "all went well saved $it in table", "OK"}

    //look at jigsaw table to help here
    ConcurrentHashMap cellsGrid = new ConcurrentHashMap<CoOrdinate, Cell>()

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

    private void addCellToRow (long rowNumber, final Cell cell) {
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

    private void addCellToColumn (long colNumber, final Cell cell) {
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

    void setCell (final long x_col_ref, final long y_row_ref, final def value) {
        CoOrdinate coOrdRef = new CoOrdinate(x_col_ref, y_row_ref)
        Cell cell = new Cell (cellReference: coOrdRef, value: value )
        setCell (cell)
    }

    void setCell (final Cell cell) {
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

    void setCell (final List aref, def value) {
        CoOrdinate coOrdRef = new CoOrdinate(aref)
        Cell cell = new Cell (cellReference: coOrdRef, value: value )
        setCell (cell)
    }

    void setCell (final CoOrdinate coOrdRef, def value) {
        Cell cell = new Cell (cellReference: coOrdRef, value: value )
        setCell (cell)
    }

    Cell getCell (final List ref) {
        CoOrdinate coOrdRef = new CoOrdinate(ref)
        cellsGrid[coOrdRef]
    }

    Cell getCell (final CoOrdinate coOrdRef) {
        cellsGrid[coOrdRef]
    }


}
