package com.softwood.worksheet.io.json

import com.softwood.worksheet.*
import com.softwood.worksheet.io.*
import com.softwood.worksheet.io.csv.CsvReadOptions

class JsonReader implements FileReaderBase,  DataReader<JsonReadOptions> {

    //call constructor and store singleton instance
    private static final JsonReader instance = new JsonReader()

    JsonReader() {
        super()
    }

    static {
        def registry = TableHashMapImpl.getDefaultReaderRegistry()
        register(registry)
    }

    //register this singleton in the reader registry
    public static void register(ReaderRegistry registry) {
        registry.registerExtension("json", instance)
        registry.registerMimeType("text/json", instance)
        registry.registerOptions(JsonReadOptions.class, instance)
    }

    Table read(JsonReadOptions options) {
        boolean headerOnly = false
        return read(options, headerOnly)
    }

    //not sure i really want this, surely i'll always read all the entries ?
    //todo - lots need to look at groovy json parsing instead...
    Table read (JsonReadOptions options, boolean headerOnly) {

        String[] colEntries
        Table table = new TableHashMapImpl()

        LineParser lineParser = new LineParser (options.delimiters)

         def lineNumber = 0

       InputStreamReader stream = options.source.createReader ()

        //now process the data lines themselves, first readLine seems to clear row[0]

        int rowNumber
        int dataRowNumber
        String line
        String[] commentPrefix = options.getCommentPrefixList()

        while (stream?.ready()) {
            line = stream.readLine()
            if (line == "") {
               continue  //for some reason the stream reads an entype line for each \n, so skip this line
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
                cell = new Cell([col, dataRowNumber ], (row[col] as LineParser.ColumnItem).value )
                cell.valueType = type
                table.setCell(cell)
            }

            dataRowNumber++
            rowNumber++

        }
        stream.close()

        table
    }


    Table read(Source source) throws IOException {
        return read(JsonReadOptions.builder(source).build())
    }
}
