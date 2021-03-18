package com.softwood.worksheet.io

import com.softwood.worksheet.Table
import com.softwood.worksheet.TableHashMapImpl
import com.softwood.worksheet.io.csv.CsvReadOptions
import com.softwood.worksheet.io.csv.CsvReader

class DataFrameReader {

    //register of possible readers
    private final ReaderRegistry registry

    //constructor
    public DataFrameReader(ReaderRegistry registry = null) {
        this.registry = registry ?: new ReaderRegistry()
    }

    Table usingOptions(ReadOptions.Builder builder) {
        return usingOptions(builder.build())
    }

    public <T extends ReadOptions> Table usingOptions(T options) throws IOException {
        DataReader<T> reader = registry.getReaderForOptions(options)
        return reader.read(options)
    }

    ///csv reading options
    Table csv (String dataFileName) {
        return csv(CsvReadOptions.builder(dataFileName))
    }

    Table csv(InputStream stream) {
        return csv(CsvReadOptions.builder(stream))
    }

    Table csv(Reader reader) {
        return csv(CsvReadOptions.builder(reader))
    }

    public Table csv(CsvReadOptions.Builder options) {
        return csv(options.build())
    }

    public Table csv(CsvReadOptions options)  {
        return new CsvReader().read(options)
    }

    ///todo general file reading options
    static Table file (String dataFileName) {
        assert dataFileName
        InputStream inp = DataFrameReader.class.getClassLoader().getResourceAsStream(dataFileName)
        file (new InputStreamReader(inp))
    }

    static Table file (File dataFile) {
        assert dataFile
        assert dataFile.canRead()
        if (dataFile.exists()) {
            def path = dataFile.getCanonicalPath()
            InputStream inp = DataFrameReader.getClass().getClassLoader().getResourceAsStream(path)

            //file(dataFile.toURI())
            BufferedInputStream source = new BufferedInputStream(inp)
            Scanner scanner = new Scanner(source)

            while (scanner.hasNext()) {
                String line = scanner.nextLine()
                println ">> $line"
            }
        }
        else
            throw new FileNotFoundException ("File $dataFile references a non existant file ")
    }

    static Table file (URI dataFileUri) {
        assert dataFileUri

        //dataFileUri.
    }

    static Table file (InputStreamReader dataFileReader) {
        assert InputStreamReader

        String[] colEntries

        LineParser lineParser = new LineParser ()

        ArrayList parsedLines = []
        def lineNumber = 0

        dataFileReader.eachLine() {String line ->
            def pline =  lineParser.parse(line)
            parsedLines << pline

        }
        println ">> $parsedLines"

        new TableHashMapImpl()
    }
}
