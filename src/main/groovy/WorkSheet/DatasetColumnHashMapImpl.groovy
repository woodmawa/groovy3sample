package WorkSheet

import java.util.concurrent.ConcurrentHashMap

class DatasetColumnHashMapImpl implements DatasetColumn {

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
