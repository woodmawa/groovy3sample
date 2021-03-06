package com.softwood.worksheet

import com.softwood.worksheet.io.ReaderRegistry

import java.util.stream.Stream

interface Table {
    void setName (String name)
    String getName ()
    void setColumnName (long colNumber, final String name)
    void setRowName (long rowNumber, final String name)
    void setHeaders (boolean hasHeaders)
    Table insertColumn (final int index, final DatasetColumn col)
    DatasetColumn getColumn (long colNumber)
    DatasetColumn getColumn (final String colName)
    DatasetRow getRow (long rowNumber)
    DatasetRow getRow (final String colName)
    Map<CellCoOrdinate, Cell> getCellsGrid()
    Cell setCell (Cell cell)
    Cell setCell (final long x_col_ref, final long y_row_ref, final def value)
    Cell setCell (final List<Long> ref, def value)
    Cell setCell (final CellCoOrdinate coOrdRef, def value)
    Cell getCell (final long x, final long y)
    Cell getCell (final long x, final long y, final long z)
    Cell getCell (final List<Long> ref)
    Cell getCell (final CellCoOrdinate coOrdRef)
    Optional<Table> intersectionByKey (final Table table2)
    void linkWorksheet (Worksheet ws)
    void unlinkWorksheet ()
    void setWorksheet (Worksheet ws)
    Optional<Worksheet> getWorksheet()
    boolean hasHeaders()

    void clearError()

    Stream<Cell> stream()
}