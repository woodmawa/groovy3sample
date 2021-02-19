package com.softwood.worksheet

import java.util.stream.Stream

interface DatasetRow {
    void setName (final String name)
    String getName ()
    void putCell (final Cell cell)
    Collection getCellsCollection ()
    List<Cell> getCellsAsList()
    int size()
    void times (double multiplier)

    Stream<Cell> stream()
}