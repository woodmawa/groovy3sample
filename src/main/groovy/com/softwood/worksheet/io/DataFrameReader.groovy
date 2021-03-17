package com.softwood.worksheet.io

import com.softwood.worksheet.Table
import com.softwood.worksheet.TableHashMapImpl

class DataFrameReader {

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
            //file (dataFile.get as InputStreamReader)
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
