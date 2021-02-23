package com.softwood.worksheet

import groovy.transform.EqualsAndHashCode
import groovy.transform.MapConstructor

/**
 * cells can be optionally named
 * CoOrdinate:value pairing
 *
 */
@MapConstructor
@EqualsAndHashCode (includeFields = true)
class Cell {
    private Optional<String> name = Optional.empty()
    private CoOrdinate cellReference
    def value

    Cell (CoOrdinate cellReference, def value){
        this.cellReference = cellReference
        this.value = value
    }

    Cell (final List array_ref, def value){
        CoOrdinate cellRef = new CoOrdinate(array_ref)
        this.cellReference = cellRef
        this.value = value
    }

    Cell (final long x_col_index, final long y_row_index, final long z_index = 0, def value){
        CoOrdinate cellRef = new CoOrdinate(x_col_index, y_row_index )
        this.cellReference = cellRef
        this.value = value
    }

    void setName (final String name) {
        this.name = Optional<String>.of(name)
    }

    String getName () {
        name.orElse("--Unnamed Cell--")
    }

    CoOrdinate getCoOrdinate () {
        cellReference
    }

    List<Long> getCoOrdinateAsList () {
        //cellReference.
    }

    void updateValue (final update) {
        value = update
    }

    void setValue (final value) {
        this.value = value
    }

    def getValue (final value) {
        value
    }

    String getValueAsText () {
        String BLANK_STRING = ""
        if (value) {
            if (value instanceof String)
                value
            else if (value.getClass() == Optional) {
                value.orElse "BLANK_STRING"
            } else
                value.toString()
        } else {
            BLANK_STRING
        }
    }

    String toString () {
        "cell @{$cellReference : $value}"
    }

}