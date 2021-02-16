package WorkSheet

import groovy.transform.EqualsAndHashCode
import groovy.transform.Immutable
import groovy.transform.MapConstructor

import java.util.concurrent.ConcurrentHashMap

@MapConstructor
@EqualsAndHashCode  //need this to ensure unique access via the cells map
class CellCoOrdinate {
    //todo use tuple
    long x
    long y
    long z

    CellCoOrdinate(final List coOrds) {
        x = coOrds?[0] ?: 0
        y = coOrds?[1] ?: 0
        z = coOrds?[2] ?: 0
    }

    CellCoOrdinate(final x, final y, final z = 0) {
        this.x = x as long
        this.y = y as long
        this.z = z as long
    }

    /**
     * moves the cell co-ordinates relative to existing location
     * @param dx
     * @param dy
     * @param dz
     */
    CellCoOrdinate move (final long dx, final long dy, final long dz=0L) {
        x = x + dx
        y = y + dy
        z = z + dz
        this
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
        "CellCoOrdinate[$x,$y,$z]"
    }

    CellCoOrdinate asType (Class clazz) {
        assert clazz
        if (clazz instanceof List) {
            new CellCoOrdinate(ref)
        } else null
    }
}

/**
 * cells can be optionally named
 * CellCoOrdinate:value pairing
 *
 */
class Cell {
    Optional<String> name = Optional.ofNullable(null)
    CellCoOrdinate cellReference
    def value

    void setName (final String name) {
        this.name = Optional<String>.of(name)
    }

    String toString () {
        "cell @{$cellReference : $value}"
    }

}

/**
 * table is named block of cells in a grid.  cells are stored in a map
 * a cell instance can be indexed by its CellCoOrdinate
 */
class Table {

    String name
    ConcurrentHashMap namedRows = new ConcurrentHashMap<long, String>()
    ConcurrentHashMap namedColumns = new ConcurrentHashMap<long, String>()

    Closure error = {println "error didnt save cell ref - if was invalid $it.cellReference", "error invalid co-ordinate for cell"}
    Closure success = {println "all went well saved $it in table", "OK"}

    ConcurrentHashMap cellsGrid = new ConcurrentHashMap<CellCoOrdinate, Cell>()

    void setCell (Cell cell) {
        if (cell.cellReference) {
            cellsGrid.putIfAbsent(cell.cellReference, cell)
            cell
        }
        else {
            error (cell)
        }
    }

    void setCell (final List ref, def value) {
        CellCoOrdinate coOrdRef = new CellCoOrdinate(ref)
        def cell = cellsGrid[coOrdRef]
        if (cell) {
            cell.value = value
        } else {
            cell = new Cell(cellReference:coOrdRef,value:value)
            cellsGrid.putIfAbsent(coOrdRef, cell)
        }
    }

    void setCell (CellCoOrdinate coOrdRef, value) {
        def cell = cellsGrid[coOrdRef]
        if (cell) {
            cell.value = value
        } else {
            cell = new Cell(cellReference:coOrdRef,value:value)
            cellsGrid.putIfAbsent(coOrdRef, cell)
        }
    }

    Cell getCell (final List ref) {
        CellCoOrdinate coOrdRef = new CellCoOrdinate(ref)
        cellsGrid[coOrdRef]
    }

    Cell getCell (final CellCoOrdinate coOrdRef) {
        cellsGrid[coOrdRef]
    }


}

CellCoOrdinate origin = new CellCoOrdinate (0,0)


Table table = new Table(name:'myTab')
table.setCell([0,0], 10)
table.setCell([1,1], "cell 1:1")
Cell c = table.getCell([0,0])
Cell c2 = table.getCell(origin)

println table.name
println c
println c2
println table.getCell([1,1]).value