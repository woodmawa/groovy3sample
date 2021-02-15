package WorkSheet

import groovy.transform.EqualsAndHashCode
import groovy.transform.Immutable
import groovy.transform.MapConstructor

import java.util.concurrent.ConcurrentHashMap

@MapConstructor
@EqualsAndHashCode  //need this to ensure unique access via the cells map
class CoOrdinate {
    //todo use tuple
    long x
    long y
    long z

    CoOrdinate (final List coOrds) {
        x = coOrds?[0] ?: 0
        y = coOrds?[1] ?: 0
        z = coOrds?[2] ?: 0
    }

    CoOrdinate (final x, final y, final z = 0) {
        this.x = x as long
        this.y = y as long
        this.z = z as long
    }

    List get2DReference () {
        //new Immutable
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
        "CoOrdinate[$x,$y,$z]"
    }
}

/**
 * cells can be optionally named
 * CoOrdinate:value pairing
 *
 */
class Cell {
    Optional<String> name = Optional.ofNullable(null)
    CoOrdinate cellReference
    def value

    String toString () {
        "cell @{$cellReference : $value}"
    }

}

/**
 * table is named block of cells in a grid.  cells are stored in a map
 * a cell instance can be indexed by its CoOrdinate
 */
class Table {

    String name
    ConcurrentHashMap namedRows = new ConcurrentHashMap<long, String>()
    ConcurrentHashMap namedColumns = new ConcurrentHashMap<long, String>()

    ConcurrentHashMap cells = new ConcurrentHashMap<CoOrdinate, Cell>()

    void setCell (Cell cell) {

    }

    void setCell (List ref, value) {
        CoOrdinate coOrdRef = new CoOrdinate(ref)
        def cell = cells[coOrdRef]
        if (cell) {
            cell.value = value
        } else {
            cell = new Cell(cellReference:coOrdRef,value:value)
            cells.putIfAbsent(coOrdRef, cell)
        }
    }

    void setCell (CoOrdinate coOrdRef, value) {
        def cell = cells[coOrdRef]
        if (cell) {
            cell.value = value
        } else {
            cell = new Cell(cellReference:coOrdRef,value:value)
            cells.putIfAbsent(coOrdRef, cell)
        }
    }

    Cell getCell (final List ref) {
        CoOrdinate coOrdRef = new CoOrdinate(ref)
        cells[coOrdRef]
    }

    Cell getCell (final CoOrdinate coOrdRef) {
        cells[coOrdRef]
    }


}

def origin = new CoOrdinate (0,0)

Table table = new Table(name:'myTab')
table.setCell([0,0], 10)
Cell c = table.getCell([0,0])
Cell c2 = table.getCell(origin)

println table.name
println c
println c2