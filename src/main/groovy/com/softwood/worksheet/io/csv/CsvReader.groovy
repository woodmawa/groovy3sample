package com.softwood.worksheet.io.csv

import com.softwood.worksheet.Table
import com.softwood.worksheet.TableHashMapImpl
import com.softwood.worksheet.io.DataReader
import com.softwood.worksheet.io.FileReaderBase
import com.softwood.worksheet.io.ReaderRegistry
import com.softwood.worksheet.io.Source

class CsvReader implements FileReaderBase,  DataReader<CsvReadOptions> {

    //call construictor and store singleton instance
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
        return read(options, false)
    }

    Table read(Source source) throws IOException {
        return read(CsvReadOptions.builder(source).build())
    }
}
