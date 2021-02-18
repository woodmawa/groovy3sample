package com.softwood.worksheet

interface DatasetColumn {
    void setName (final String name)
    String getName()
    void putCell (final Cell cell)
    Collection getCellsCollection ()
    List<Cell> getCellsAsList ()
    int size()

}