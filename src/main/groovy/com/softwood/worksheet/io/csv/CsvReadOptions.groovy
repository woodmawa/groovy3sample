package com.softwood.worksheet.io.csv

import com.softwood.worksheet.io.ReadOptions
import com.softwood.worksheet.io.Source

class CsvReadOptions extends ReadOptions {

    //private final DataValueType[] columnTypes;
    private final String[] delimiters
    private final Character quoteChar
    private final Character escapeChar
    private final String lineEnding
    private final Integer maxNumberOfColumns
    private final String[] commentPrefixList
    private final boolean lineSeparatorDetectionEnabled

    String getDelimiters () {delimiters}
    Character getQuoteChar () {quoteChar}
    Character getEscapeChar () {escapeChar}
    String[] getCommentPrefixList () {commentPrefixList }

    //private constructor
    private CsvReadOptions(CsvReadOptions.Builder builder) {
        super(builder)
        //columnTypes = builder.columnTypes
        delimiters = builder.delimiters
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
        return new CsvReadOptions.Builder()
    }

    static Builder builder(Source source) {
        return new CsvReadOptions.Builder(source)
    }

    static Builder builder(InputStream stream) {
        return new CsvReadOptions.Builder(stream)
    }

    public static Builder builder(Reader reader) {
        return new CsvReadOptions.Builder(reader)
    }

    public static Builder builder(InputStreamReader reader) {
        return new CsvReadOptions.Builder(reader)
    }

    static Builder builder(File file) {
        return new CsvReadOptions.Builder(file)
    }

    static Builder builder(String fileName) {
        File file = new File(fileName)
        builder(file)  //call above to handle
    }

    static Builder builder(URL url)  {
        return new CsvReadOptions.Builder(url)
    }

    static Builder builderFromFile(String fileName) {
        return new CsvReadOptions.Builder(new File(fileName))
    }

    static Builder builderFromString(String contents) {
        return new CsvReadOptions.Builder(new StringReader(contents))
    }

    static Builder builderFromUrl(String url) {
        return new CsvReadOptions.Builder(new URL(url))
    }



    //@InheritConstructors
    public static class Builder extends ReadOptions.Builder {

        private String[] delimiters
        private Character quoteChar
        private Character escapeChar
        private String lineEnding
        //private DataValueType[] columnTypes
        //private Integer maxNumberOfColumns = 10_000
        private String[] commentPrefixList
        private boolean lineSeparatorDetectionEnabled = true
        //private int sampleSize = -1

        //default constructor - sets the default file extension
        //when called invokes the parent ReadOption constructor and the finalises initialisation for this
        Builder (arg) {
            super(arg)
            defaultFileExtension = "csv"
            delimiters = ['\t', '|', ',']  //default separators
            quoteChar = Character.valueOf ('"' as char)
            escapeChar = Character.valueOf('\\' as char)
            lineEnding = "\n"
            commentPrefixList = ['!', '#', '//']
        }


        Builder separator(Character separator) {
            this.delimiters = separator
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
