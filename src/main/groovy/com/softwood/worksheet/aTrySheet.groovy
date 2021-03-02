package com.softwood.worksheet

import java.util.concurrent.ConcurrentHashMap

import static java.util.stream.Collectors.*;

CellCoOrdinate origin = new CellCoOrdinate (0,0)
println origin.hashCode()
CellCoOrdinate oneAndOne = new CellCoOrdinate (1,1)
println oneAndOne.hashCode()
CellCoOrdinate twoAndTwo = new CellCoOrdinate (2,2)
CellCoOrdinate again = new CellCoOrdinate (2,2)

assert again == twoAndTwo

Map m1 = new ConcurrentHashMap<CellCoOrdinate, float>([(origin): 0, (oneAndOne): 1.0])
Map m2 = new ConcurrentHashMap<CellCoOrdinate, float>([(oneAndOne): 1.01, (twoAndTwo): 2.0])

Map m3 = m1.intersect(m2)

def m4

m4 = m1.entrySet().stream().collect(toMap(Map.Entry::getKey, Map.Entry::getValue))


TableHashMapImpl table = new TableHashMapImpl(name:'myFirstTable')
table.setCell(0,0, 10)
table.setCell([1,1], "cell 1:1")
table.setCell([1,2], "cell 1:2")
Cell c = table.getCell([0,0])
Cell c2 = table.getCell(origin)

table.setRowName(1,"row 1")
table.setColumnName(1,"thingy dooby")

DatasetRow row = table.getRow (1)
println "got row: " + row.name

row = table.getRow ('row 1')
assert row.name == 'row 1'

List rowCells = row.cellsAsList

println "rowCells : $rowCells"

DatasetColumn col = table.getColumn("thingy dooby")
println "got col: " + col.name

println table.name
println c
println c2
println table.getCell([1,1]).valueAsText

List tabCells = table.stream().collect(toList())
println "cells in table " +tabCells
println "table values : "+ table.stream().map(cell -> cell.value).collect(toList())