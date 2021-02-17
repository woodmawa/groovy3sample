package WorkSheet

interface Table {
    void setColumnName (long colNumber, final String name)
    void setRowName (long rowNumber, final String name)
    DatasetColumn getColumn (long colNumber)
    DatasetRow getRow (long rowNumber)
    void setCell (Cell cell)
    void setCell (final List ref, def value)
    void setCell (CoOrdinate coOrdRef, value)
    Cell getCell (final List ref)
    Cell getCell (final CoOrdinate coOrdRef)

}