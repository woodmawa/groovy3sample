package WorkSheet

import groovy.transform.MapConstructor

/**
 * cells can be optionally named
 * CoOrdinate:value pairing
 *
 */
@MapConstructor
class Cell {
    Optional<String> name = Optional.ofNullable(null)
    CoOrdinate cellReference
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

    Cell (final long x_col_index, final long y_row_index, def value){
        CoOrdinate cellRef = new CoOrdinate(x_col_index, y_row_index )
        this.cellReference = cellRef
        this.value = value
    }

    void setName (final String name) {
        this.name = Optional<String>.of(name)
    }

    void updateValue (final update) {
        value = update
    }

    String getValueAsText () {
        String BLANK_STRING = ""
        if (value) {
            value.toString()
        } else {
            BLANK_STRING
        }
    }

    String toString () {
        "cell @{$cellReference : $value}"
    }

}