package com.softwood.worksheet

import java.util.stream.Stream

interface Worksheet {
    void setName (final String name)
    String getName ()
    void addTable (Table table)
    boolean deleteTable (Table table)
    Optional<Table> findTable (String name)
    List<Table> getTables()
    Stream<Table> stream()
    void delete ()
    //boolean removeWorksheet (Worksheet ws)  // static function
}