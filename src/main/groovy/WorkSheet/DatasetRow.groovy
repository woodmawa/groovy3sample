package WorkSheet

import java.util.concurrent.ConcurrentHashMap

interface DatasetRow {
    void setName (final String name)
    void putCell (final Cell cell)

}