package com.softwood.worksheet

import com.softwood.worksheet.io.DataValueType

import java.util.stream.Stream

interface DatasetColumn {
    void setName (final String name)
    long getColumnNumber ()
    String getName()
    void putCell (final Cell cell)
    Collection getCellsCollection ()
    List<Cell> getCellsAsList ()
    int size()
    void times (double multiplier)
    void timesAsLong (double multiplier)
    DataValueType getType()
    void setType (DataValueType type)


    Stream<Cell> stream()
}