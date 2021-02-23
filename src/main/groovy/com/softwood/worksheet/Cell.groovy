package com.softwood.worksheet

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode
import groovy.transform.MapConstructor

import java.beans.PropertyChangeListener

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
    static String BLANK_STRING = ""

    //sends property change events to subscribers on cell values
    @Bindable def value
    Closure function = {args -> }

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

    /**
     * subscribe and unsubscribe for changes to value property
     * @param changeListener
     */
    void addCellValueListener (PropertyChangeListener changeListener) {
        this.addPropertyChangeListener("value", changeListener)
    }

    void removeCellValueListener (PropertyChangeListener changeListener) {
        this.removePropertyChangeListener("value", changeListener)
    }

    List<PropertyChangeListener> getCellValueListenersList ( ) {
        this.getPropertyChangeListeners("value")
    }

    /**
     * updates the cell value and fires any listeners with PropertyChangeEvent
     * @param update value to save on cell
     */
    void updateValue (final update) {
        this.firePropertyChange("value", this.value, update)
        value = update
    }

    void setValue (final value) {
        this.value = value
    }

    def getValue (final value) {
        value
    }

    def calculate (List args) {
        function.call (args)
    }

    String getValueAsText () {
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