package com.softwood.worksheet.io.csv

import com.softwood.worksheet.Cell
import com.softwood.worksheet.DatasetColumn
import com.softwood.worksheet.DatasetColumnHashMapImpl
import com.softwood.worksheet.Table
import com.softwood.worksheet.TableHashMapImpl
import com.softwood.worksheet.io.DataReader
import com.softwood.worksheet.io.DataValueType
import com.softwood.worksheet.io.FileReaderBase
import com.softwood.worksheet.io.LineParser
import com.softwood.worksheet.io.ReaderRegistry
import com.softwood.worksheet.io.Source

class CsvReader implements FileReaderBase,  DataReader<CsvReadOptions> {

    //call constructor and store singleton instance
    private static final CsvReader instance = new CsvReader()

    CsvReader() {
        super()
    }

    static {
        def registry = TableHashMapImpl.getDefaultReaderRegistry()
        register(registry)
    }

    public static void register(ReaderRegistry registry) {
        registry.registerExtension("csv", instance)
        registry.registerMimeType("text/csv", instance)
        registry.registerOptions(CsvReadOptions.class, instance)
    }

    Table read(CsvReadOptions options) {
        boolean headerOnly = false
        return read(options, headerOnly)
    }

    //not sure i really want this
    Table read (CsvReadOptions options, boolean headerOnly) {

        String[] colEntries
        Table table = new TableHashMapImpl()

        LineParser lineParser = new LineParser (options.delimiters)

         def lineNumber = 0

       InputStreamReader stream = options.source.createReader ()

        //now process the data lines themselves, first readLine seems to clear row[0]

        int rowNumber
        def isReady = stream?.ready()
        def enc = stream?.getEncoding()

        String line
        String[] commentPrefix = options.getCommentPrefixList()

        while (stream?.ready()) {
            line = stream.readLine()
            if (line == "") {
               continue  //skip this line
            }
            for (i in 0..<commentPrefix.size()) {
                if (line.startsWith (commentPrefix[i])) {
                    continue  //skip this line
                }
            }

            if (options.headers && rowNumber == 0 ) {
                //process the first row for names of the columns
                Map rowOfColumns = lineParser.parse (line)
                table.setHeaders (true)
                for (i in 0..<rowOfColumns.size()) {
                    DatasetColumn col = new DatasetColumnHashMapImpl()
                    col.columnNumber = i
                    col.name = (rowOfColumns[i] as LineParser.ColumnItem).value
                    table.insertColumn(i, col)
                }
                rowNumber++
                continue
            }

            def row =  lineParser.parse(line)
            for (col in 0..<row.size()) {
                Cell cell
                //if column is undefined then set the column type first time
                DataValueType type = (row[col] as LineParser.ColumnItem).type
                if (table.getColumn(0).type == DataValueType.UNDEFINED) {
                    table.getColumn(0).type = type
                }
                cell = new Cell([col, rowNumber], (row[col] as LineParser.ColumnItem).value )
                cell.valueType = type
                table.setCell(cell)
            }

            rowNumber++

        }
        stream.close()

        table
    }


    Table read(Source source) throws IOException {
        return read(CsvReadOptions.builder(source).build())
    }
}
