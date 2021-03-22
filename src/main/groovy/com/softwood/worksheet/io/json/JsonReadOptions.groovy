package com.softwood.worksheet.io.json

import com.softwood.worksheet.io.ReadOptions
import com.softwood.worksheet.io.Source

class JsonReadOptions extends ReadOptions {

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
    private JsonReadOptions(JsonReadOptions.Builder builder) {
        super(builder)
        //columnTypes = builder.columnTypes
        quoteChar = builder.quoteChar
        escapeChar = builder.escapeChar
        //maxNumberOfColumns = builder.maxNumberOfColumns
        commentPrefixList = builder.commentPrefixList
        lineSeparatorDetectionEnabled = builder.lineSeparatorDetectionEnabled
        //sampleSize = builder.sampleSize
    }

    //static methods to provide an initial builder object to work with
    static Builder builder() {
        return new JsonReadOptions.Builder()
    }

    static Builder builder(Source source) {
        return new JsonReadOptions.Builder(source)
    }

    static Builder builder(InputStream stream) {
        return new JsonReadOptions.Builder(stream)
    }

    public static Builder builder(Reader reader) {
        return new JsonReadOptions.Builder(reader)
    }

    public static Builder builder(InputStreamReader reader) {
        return new JsonReadOptions.Builder(reader)
    }

    static Builder builder(File file) {
        return new JsonReadOptions.Builder(file)
    }

    static Builder builder(String fileName) {
        File file = new File(fileName)
        builder(file)  //call above to handle
    }

    static Builder builder(URL url)  {
        return new JsonReadOptions.Builder(url)
    }

    static Builder builderFromFile(String fileName) {
        return new JsonReadOptions.Builder(new File(fileName))
    }

    static Builder builderFromString(String contents) {
        return new JsonReadOptions.Builder(new StringReader(contents))
    }

    static Builder builderFromUrl(String url) {
        return new JsonReadOptions.Builder(new URL(url))
    }



    //@InheritConstructors
    public static class Builder extends ReadOptions.Builder {

        private Character quoteChar
        private Character escapeChar
        private String[] commentPrefixList
        private boolean lineSeparatorDetectionEnabled = true
        //private int sampleSize = -1

        //default constructor - sets the default file extension
        //when called invokes the parent ReadOption constructor and the finalises initialisation for this
        Builder (arg) {
            super(arg)
            defaultFileExtension = "json"
            quoteChar = Character.valueOf ('"' as char)
            escapeChar = Character.valueOf('\\' as char)
            commentPrefixList = ['!', '#', '//']
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

         //last function in chain to return the validated JsonReadOptions
        JsonReadOptions build() {
            return new JsonReadOptions(this)
        }

    }
}
