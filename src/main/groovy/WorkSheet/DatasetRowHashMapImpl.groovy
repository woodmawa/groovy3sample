package WorkSheet

import java.util.concurrent.ConcurrentHashMap

class DatasetRowHashMapImpl implements DatasetRow {

    Optional<String> name = Optional.empty()
    long rowNumber
    ConcurrentHashMap rowCells = new ConcurrentHashMap<Long, Cell> ()

    @Override
    void setName (final String name) {
        this.name = Optional.of (name)
    }

    @Override
    String getName() {
        //returns value if present of the default otherwise
        name.orElse( "empty")
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
