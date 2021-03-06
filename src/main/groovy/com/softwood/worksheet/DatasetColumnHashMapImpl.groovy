package com.softwood.worksheet

import com.softwood.worksheet.io.DataValueType

import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Stream

class DatasetColumnHashMapImpl implements DatasetColumn {

    Optional<String> name = Optional.empty()
    long columnNumber
    ConcurrentHashMap columnCells = new ConcurrentHashMap<Long, Cell> ()
    Optional<DataValueType> type = Optional.of (DataValueType.UNDEFINED)

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
        name.orElse( "--UnNamed Column--")
    }

    long getColumnNumber () {
        columnNumber
    }

    DataValueType getType () {
        type.orElse (DataValueType.UNDEFINED)
    }

    void setType (DataValueType dataType) {
        type = Optional.ofNullable(dataType)
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
        long rowNumber = cell.getCoOrdinate().y
        long colNumber = cell.getCoOrdinate().x

        if (columnNumber == colNumber) {
            columnCells.put(rowNumber, cell)
        }
    }

    /**
     * if cell.value is a number, then multiply and save the new value
     * @param multiplier
     */
    void times (double multiplier ) {
        columnCells?.values().stream()
                .filter (cell -> cell.value instanceof Number)
                .forEach(cell -> cell.updateValue(cell.value as Double * multiplier))
    }

    void timesAsLong (double multiplier ) {
        columnCells?.values().stream()
                .filter (cell -> cell.value instanceof Number)
                .forEach(cell -> cell.updateValue( Math.round(cell.value as Double * multiplier)) )
    }

    Stream<Cell> stream () {
        columnCells?.values().stream()
    }
}
