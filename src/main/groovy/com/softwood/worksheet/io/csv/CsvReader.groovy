package com.softwood.worksheet.io.csv

import com.softwood.worksheet.Cell
import com.softwood.worksheet.DatasetColumn
import com.softwood.worksheet.DatasetColumnHashMapImpl
import com.softwood.worksheet.Table
import com.softwood.worksheet.TableHashMapImpl
import com.softwood.worksheet.io.DataReader
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

        ArrayList tempParsedLines = []
        def lineNumber = 0

       InputStreamReader stream = options.source.createReader ()

        if (options.headers) {
            //process the first row for names of the columns
            String headerLine = stream.readLine()
            Map rowOfColumns = lineParser.parse (headerLine)
            for (i in 0..<rowOfColumns.size()) {
                DatasetColumn col = new DatasetColumnHashMapImpl()
                col.columnNumber = i
                col.name = (rowOfColumns[i] as com.softwood.worksheet.io.LineParser.ColumnItem).value
                table.addColumnToTable(col)
            }
        }

        //now process the data lines themselves, first readLine seems to clear row[0]
        int rowNumber
        stream?.eachLine() {String line ->
            if (line == "")
                return
            String[] commentPrefix = options.getCommentPrefixList()
            for (i in 0..<commentPrefix.size()) {
                if (line.startsWith (commentPrefix[i]))
                    return  //skip this line
            }

            def row =  lineParser.parse(line)
            for (col in 0..<row.size()) {
                if (table.getColumn(0).setType (row[col] as LineParser.ColumnItem).getType())
                def cell = new Cell([col, rowNumber], (row[col] as LineParser.ColumnItem).value )
                table.setCell(cell)
            }
            tempParsedLines << row
            rowNumber++
        }

        //println ">> $tempParsedLines"
        table
    }


    Table read(Source source) throws IOException {
        return read(CsvReadOptions.builder(source).build())
    }
}
