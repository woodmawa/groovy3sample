package WorkSheet

import groovy.transform.Immutable
import groovy.transform.MapConstructor

import java.util.concurrent.ConcurrentHashMap

@MapConstructor
class CoOrdinate {
    //todo use tuple
    long x
    long y
    long z

    CoOrdinate (List coOrds) {
        x = coOrds?[0] ?: 0
        y = coOrds?[1] ?: 0
        z = coOrds?[2] ?: 0
    }


    List get2DReference () {
        //new Imm
        [x,y] as Immutable
    }

    Tuple get2DTuple () {
        new Tuple2 (x,y)
    }

    List get3DReference () {
        [x, y, z] as Immutable
    }

    Tuple get3DTuple () {
        new Tuple3 (x,y,z)
    }

    String toString () {
        "[$x,$y,$z]"
    }
}

class Cell {
    Optional<String> name = Optional.ofNullable(null)
    CoOrdinate cellReference
    def value

    String toString () {
        "$cellReference : $value"
    }

}

class Table {

    String name
    ConcurrentHashMap namedRows = new ConcurrentHashMap<long, String>()
    ConcurrentHashMap namedColumns = new ConcurrentHashMap<long, String>()

    ConcurrentHashMap cells = new ConcurrentHashMap<CoOrdinate, Cell>()

    void setCell (Cell cell) {

    }

    void setCell (List ref, value) {
        def cell = cells[ref]
        if (cell) {
            cell.value = value
        } else {
            cell = new Cell(cellReference:new CoOrdinate(ref),value:value)
            cells.putIfAbsent(ref, cell)
        }
    }

    Cell getCell (List ref) {
        cells[ref]
    }

}

Table table = new Table(name:'myTab')
table.setCell([0,0], 10)
Cell c = table.getCell([0,0])

println table.name
println c