package WorkSheet

import groovy.transform.EqualsAndHashCode
import groovy.transform.Immutable
import groovy.transform.MapConstructor

import java.util.concurrent.ConcurrentHashMap

CoOrdinate origin = new CoOrdinate (0,0)


TableHashMapImpl table = new TableHashMapImpl(name:'myTab')
table.setCell([0,0], 10)
table.setCell([1,1], "cell 1:1")
table.setCell([1,2], "cell 1:2")
Cell c = table.getCell([0,0])
Cell c2 = table.getCell(origin)

table.setRowName(1,"row 1")
table.setColumnName(1,"thingy dooby")

DatasetRow row = table.getRow (1)
println "got row " + row.name

println table.name
println c
println c2
println table.getCell([1,1]).valueAsText