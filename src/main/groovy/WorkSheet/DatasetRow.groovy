package WorkSheet

import java.util.concurrent.ConcurrentHashMap

interface DatasetRow {
    void setName (final String name)
    String getName ()
    void putCell (final Cell cell)
    Collection getCellsCollection ()
    List getCellsAsList()

}