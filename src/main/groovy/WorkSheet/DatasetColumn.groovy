package WorkSheet

import java.util.concurrent.ConcurrentHashMap

interface DatasetColumn {
   void setName (final String name)
    void putCell (final Cell cell)
}