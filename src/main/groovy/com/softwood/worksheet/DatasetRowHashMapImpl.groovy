package com.softwood.worksheet

import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Stream

class DatasetRowHashMapImpl implements DatasetRow {

    Optional<String> name = Optional.empty()
    long rowNumber
    ConcurrentHashMap rowCells = new ConcurrentHashMap<Long, Cell> ()

    @Override
    int size() {
        rowCells.size()
    }

    @Override
    void setName (final String name) {
        this.name = Optional.of (name)
    }

    @Override
    String getName() {
        //returns value if present of the default otherwise
        name.orElse( "--UnNamed Row--")
    }

    @Override
    Collection getCellsCollection () {
        rowCells.values().asImmutable()
    }

    @Override
    List<Cell> getCellsAsList () {
        rowCells.values().asList().asImmutable()
    }

    void putCell (final Cell cell) {
        assert cell
        long rowNumber = cell.cellReference.y
        long col = cell.cellReference.x

        if (rowNumber == this.rowNumber) {
            rowCells.put(col, cell)
        }
    }

    /**
     * if cell.value is a number, then multiply and save the new value
     * @param multiplier
     */
    void times (double multiplier ) {
        rowCells?.values().stream()
        .filter (cell -> cell.value instanceof Number)
        .forEach(cell -> cell.updateValue(cell.value as Double * multiplier))
    }

    Stream<Cell> stream () {
        rowCells?.values().stream()
    }
}
