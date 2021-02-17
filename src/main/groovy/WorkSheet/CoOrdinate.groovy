package WorkSheet

import groovy.transform.EqualsAndHashCode
import groovy.transform.Immutable
import groovy.transform.MapConstructor

@MapConstructor
@EqualsAndHashCode  //need this to ensure unique access via the cells map
class CoOrdinate {
    //todo use tuple
    long x
    long y
    long z

    CoOrdinate(final List coOrds) {
        x = coOrds?[0] ?: 0
        y = coOrds?[1] ?: 0
        z = coOrds?[2] ?: 0
    }

    CoOrdinate(final x, final y, final z = 0) {
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
    CoOrdinate move (final long dx, final long dy, final long dz=0L) {
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
        "CoOrdinate[$x,$y,$z]"
    }

    CoOrdinate asType (Class clazz) {
        assert clazz
        if (clazz instanceof List) {
            new CoOrdinate(ref)
        } else null
    }
}

