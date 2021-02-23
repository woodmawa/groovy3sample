package com.softwood.worksheet

import spock.lang.Specification

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

class TestCellSpecification extends Specification {

    def "test property changes on cell values"() {
        given:
        Cell cell = new Cell ([0,0], "orig text")

        when :
        PropertyChangeEvent pce
        PropertyChangeListener l = { event -> pce = event}
        cell.addCellValueListener(  l )
        List<PropertyChangeListener> listeners = cell.getCellValueListenersList()
        cell.updateValue(10)

        then:
        cell.getPropertyChangeListeners().size() == 1
        listeners[0] == l
        pce.oldValue == "orig text"
        pce.newValue == 10
        cell.value == 10

        and:
        cell.setValue(100)
        
        then:
        pce.oldValue == 10
        pce.newValue == 100
        cell.value == 100


    }
}
