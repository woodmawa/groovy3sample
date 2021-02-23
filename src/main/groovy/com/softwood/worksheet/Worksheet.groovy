package com.softwood.worksheet

import java.util.stream.Stream

interface Worksheet {
    void setName (final String name)
    String getName ()
    void addTable (Table table)
    boolean deleteTable (Table table)
    boolean removeTable (Table table)
    Optional<Table> findTable (String name)
    List<Table> getTables()
    Stream<Table> streamOfTables()
    void delete ()
    Worksheet getDefaultWorksheet()
    Collection<Worksheet> getWorksheets ()
    Stream<Worksheet> streamOfWorksheets()
    String toString()
}
