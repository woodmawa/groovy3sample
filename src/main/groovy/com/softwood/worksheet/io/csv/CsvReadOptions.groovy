package com.softwood.worksheet.io.csv

import com.softwood.worksheet.io.ReadOptions
import groovy.transform.InheritConstructors

class CsvReadOptions extends ReadOptions {

    //private final ColumnType[] columnTypes;
    private final Character separator
    private final Character quoteChar
    private final Character escapeChar
    private final String lineEnding
    private final Integer maxNumberOfColumns
    private final Character commentPrefix
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
        commentPrefix = builder.commentPrefix
        lineSeparatorDetectionEnabled = builder.lineSeparatorDetectionEnabled
        //sampleSize = builder.sampleSize
    }

    @InheritConstructors
    public static class Builder extends ReadOptions.Builder {

        private Character separator
        private Character quoteChar
        private Character escapeChar
        private String lineEnding
        //private ColumnType[] columnTypes
        //private Integer maxNumberOfColumns = 10_000
        private Character commentPrefix
        private boolean lineSeparatorDetectionEnabled = true
        //private int sampleSize = -1

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

        Builder commentPrefix(Character commentPrefix) {
            this.commentPrefix = commentPrefix
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
