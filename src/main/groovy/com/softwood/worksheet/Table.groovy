package com.softwood.worksheet

interface Table {
    void setColumnName (long colNumber, final String name)
    void setRowName (long rowNumber, final String name)
    DatasetColumn getColumn (long colNumber)
    DatasetColumn getColumn (final String colName)
    DatasetRow getRow (long rowNumber)
    DatasetRow getRow (final String colName)
    void setCell (Cell cell)
    void setCell (final long x_col_ref, final long y_row_ref, final def value)
    void setCell (final List<Long> ref, def value)
    void setCell (final CoOrdinate coOrdRef, def value)
    Cell getCell (final List<Long> ref)
    Cell getCell (final CoOrdinate coOrdRef)

}