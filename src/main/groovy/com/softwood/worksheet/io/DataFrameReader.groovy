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

    ///csv reading options - fall through cascade into csv(CsvReadOptions option)
    Table csv (Source source) {
        def builder = CsvReadOptions.builder(source)
        return csv(builder)
    }

    Table csv (String dataFileName) {
        def builder = CsvReadOptions.builder(dataFileName)
        return csv(builder)
    }

    Table csv(InputStream stream) {
        def builder = CsvReadOptions.builder(stream)
        return csv(builder)
    }

    Table csv(Reader reader) {
        def builder = CsvReadOptions.builder(reader)
        return csv(builder)
    }

    Table csv(CsvReadOptions.Builder builder) {
        CsvReadOptions options = builder.build()
        return csv(options) // generate the option instance
    }

    //last in chain - does the hard work
    Table csv(CsvReadOptions options)  {
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

    static Table file (InputStreamReader dataStreamReader) {
        assert InputStreamReader

        String[] colEntries

        LineParser lineParser = new LineParser ()

        ArrayList parsedLines = []
        def lineNumber = 0

        dataStreamReader.eachLine() {String line ->
            def pline =  lineParser.parse(line)
            parsedLines << pline

        }
        println ">> $parsedLines"

        new TableHashMapImpl()
    }
}
