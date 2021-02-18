package com.softwood.worksheet

import java.util.concurrent.ConcurrentHashMap

class DatasetColumnHashMapImpl implements DatasetColumn {

    Optional<String> name = Optional.empty()
    long columnNumber
    ConcurrentHashMap columnCells = new ConcurrentHashMap<Long, Cell> ()

    @Override
    int size() {
        columnCells.size()
    }

    void setName (final String name) {
        this.name = Optional.of (name)
    }

    @Override
    String getName() {
        //returns value if present of the default otherwise
        name.orElse( "empty")
    }

    @Override
    Collection getCellsCollection () {
        columnCells.values().asImmutable()
    }

    @Override
    List<Cell> getCellsAsList () {
        columnCells.values().asList().asImmutable()
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
