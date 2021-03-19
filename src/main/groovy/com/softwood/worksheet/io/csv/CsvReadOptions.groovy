package com.softwood.worksheet.io.csv

import com.softwood.worksheet.io.ReadOptions
import com.softwood.worksheet.io.Source
import groovy.transform.InheritConstructors

class CsvReadOptions extends ReadOptions {

    //private final ColumnType[] columnTypes;
    private final String[] separator
    private final Character quoteChar
    private final Character escapeChar
    private final String lineEnding
    private final Integer maxNumberOfColumns
    private final String[] commentPrefixList
    private final boolean lineSeparatorDetectionEnabled

    //private constructor
    private CsvReadOptions(CsvReadOptions.Builder builder) {
        super(builder)
        //columnTypes = builder.columnTypes
        separator = builder.separator
        quoteChar = builder.quoteChar
        escapeChar = builder.escapeChar
        lineEnding = builder.lineEnding
        //maxNumberOfColumns = builder.maxNumberOfColumns
        commentPrefixList = builder.commentPrefixList
        lineSeparatorDetectionEnabled = builder.lineSeparatorDetectionEnabled
        //sampleSize = builder.sampleSize
    }

    //static methods to provide an initial builder object to work with
    static Builder builder() {
        return new Builder()
    }

    static Builder builder(Source source) {
        return new Builder(source)
    }

    static Builder builder(InputStream stream) {
        return new Builder(stream)
    }

    public static Builder builder(Reader reader) {
        return new Builder(reader)
    }

    public static Builder builder(InputStreamReader reader) {
        return new Builder(reader)
    }

    static Builder builder(File file) {
        String fileName = file.getName()
        //remove file extension if present
        String[] parts = fileName.tokenize('.')
        return new Builder(file).tableName(parts[0])
    }

    static Builder builder(String fileName) {
        return new Builder(new File(fileName))
    }

    static Builder builder(URL url)  {
        return new Builder(url)
    }

    static Builder builderFromFile(String fileName) {
        return new Builder(new File(fileName))
    }

    static Builder builderFromString(String contents) {
        return new Builder(new StringReader(contents))
    }

    static Builder builderFromUrl(String url) {
        return new Builder(new URL(url))
    }



    @InheritConstructors
    public static class Builder extends ReadOptions.Builder {

        private String[] separator
        private Character quoteChar
        private Character escapeChar
        private String lineEnding
        //private ColumnType[] columnTypes
        //private Integer maxNumberOfColumns = 10_000
        private String[] commentPrefixList
        private boolean lineSeparatorDetectionEnabled = true
        //private int sampleSize = -1

        //default constructor - sets the default file extension
        Builder () {
            defaultFileExtension = "csv"
            separator = ['\t', '|', ',']  //default separators
            quoteChar = Character.valueOf ('"' as char)
            escapeChar = Character.valueOf('\\' as char)
            lineEnding = "\n"
            commentPrefixList = ['!', '#', '//']
        }


        Builder separator(Character separator) {
            this.separator = separator
            return this
        }

        Builder quoteChar(Character quoteChar) {
            this.quoteChar = quoteChar
            return this
        }

        Builder escapeChar(Character escapeChar) {
            this.escapeChar = escapeChar
            return this
        }

        Builder commentPrefix(String[] commentPrefix) {
            this.commentPrefixList = commentPrefix
            return this
        }

        public Builder lineEnding(String lineEnding) {
            this.lineEnding = lineEnding
            this.lineSeparatorDetectionEnabled = false
            return this
        }

        //last function in chain to return the validated CsvReadOptions
        CsvReadOptions build() {
            return new CsvReadOptions(this)
        }

    }
}
