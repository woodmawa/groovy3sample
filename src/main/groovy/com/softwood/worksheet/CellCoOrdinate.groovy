package com.softwood.worksheet

import groovy.transform.EqualsAndHashCode
import groovy.transform.Immutable
import groovy.transform.MapConstructor

@MapConstructor
@EqualsAndHashCode (includeFields=true)  //need this to ensure unique access via the cells map
class CellCoOrdinate implements Comparable {
    private long x
    private long y
    private long z

    CellCoOrdinate(final List<Long> coOrds) {
        x = coOrds?[0] ?: 0
        y = coOrds?[1] ?: 0
        z = coOrds?[2] ?: 0
    }

    CellCoOrdinate(final long x_col_index, final long y_row_index, final long z_index = 0) {
        this.x = x_col_index as long
        this.y = y_row_index as long
        this.z = z_index as long
        this
    }

    long getX () { x }
    long getY () { y }
    long getZ () { z }

    /**
     * moves the cell co-ordinates relative to existing location
     * returns same instance but relocated co-ordinates
     * @param dx
     * @param dy
     * @param dz
     */
    CellCoOrdinate relocate (final long dx, final long dy, final long dz=0L) {
        x = x + dx
        y = y + dy
        z = z + dz
        this
    }

    /**
     * moves the cell co-ordinates returns a new CellCoOrdinate instance
     * @param dx
     * @param dy
     * @param dz
     */
    CellCoOrdinate translate (final long dx, final long dy, final long dz=0L) {
        new CellCoOrdinate (x + dx, y + dy, z + dz )
    }

    /**
     * add this location to another and return a new location
     * @param another
     * @return new location
     */
    CellCoOrdinate plus (CellCoOrdinate another) {
        assert another
        new CellCoOrdinate(x + another.x, y + another.y, z + another.z)
    }

    /**
     * subtract one location from this one and return a new location
     * @param another
     * @return new location
     */
    CellCoOrdinate minus (CellCoOrdinate another) {
        assert another
        new CellCoOrdinate(x - another.x, y - another.y, z - another.z)
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
        "CellCoOrdinate[$x,$y,$z]"
    }

    def asType (Class clazz) {
        assert clazz
        if (clazz == List) {
            //new CellCoOrdinate(x,y,z)
            getThreeDimensionalCoOrdinateAsList ()
        } else if (clazz == Tuple2) {
            getTwoDimensionalTuple()
        } else if (clazz == Tuple3) {
            getThreeDimensionalTuple()
        } else if (clazz == Tuple) {
            new Tuple (x,y,z)
        }else
            null
    }

    @Override
    int compareTo(Object obj) {
        assert obj
        int result = 0
        CellCoOrdinate other = obj

        if (this.is (other))
            result = 0
        else if (this.length() < ((CellCoOrdinate) other).length()) {
            result = -1
        } else if (this.length() > ((CellCoOrdinate) other).length()) {
            result = + 1
        } else {
            //vectors have the same length so we need to refine
            result = 0
        }
        result
    }

    /**
     * get the length of the vector relative to Origin (0,0,0)
     * @return
     */
    double length () {
        Math.sqrt(x^2 + y^2 + z^2)
    }
}

