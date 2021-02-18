package com.softwood.worksheet

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

    CoOrdinate(final List<Long> coOrds) {
        x = coOrds?[0] ?: 0
        y = coOrds?[1] ?: 0
        z = coOrds?[2] ?: 0
    }

    CoOrdinate(final long x_col_index, final long y_row_index, final long z_index = 0) {
        this.x = x_col_index as long
        this.y = y_row_index as long
        this.z = z_index as long
    }

    /**
     * moves the cell co-ordinates relative to existing location
     * returns same instance but relocated co-ordinates
     * @param dx
     * @param dy
     * @param dz
     */
    CoOrdinate relocate (final long dx, final long dy, final long dz=0L) {
        x = x + dx
        y = y + dy
        z = z + dz
        this
    }

    /**
     * moves the cell co-ordinates returns a new CoOrdinate instance
     * @param dx
     * @param dy
     * @param dz
     */
    CoOrdinate translate (final long dx, final long dy, final long dz=0L) {
        new CoOrdinate (x + dx, y + dy, z + dz )
   }

    List<Long> getTwoDimensionalCoOrdinateAsList () {
        [x,y] as Immutable
    }

    Tuple getTwoDimensionalTuple () {
        new Tuple2 (x,y)
    }

    List<Long> getThreeDimensionalCoOrdinateAsList () {
        [x, y, z] as Immutable
    }

    Tuple getThreeDimensionalTuple () {
        new Tuple3 (x,y,z)
    }

    String toString () {
        "CoOrdinate[$x,$y,$z]"
    }

    CoOrdinate asType (Class clazz) {
        assert clazz
        if (clazz instanceof List) {
            new CoOrdinate(x,y,z)
        } else null
    }
}

