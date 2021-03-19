package com.softwood.worksheet.io

class ReadOptions {

    protected Source source
    protected String tableName
    protected boolean headers
    protected Locale locale
    protected String defaultFileExtension

    protected ReadOptions(ReadOptions.Builder builder) {
        source = builder.source
        tableName = builder.tableName
        headers = builder.headers
        locale = builder.locale
        defaultFileExtension = builder.defaultFileExtension
        //allowDuplicateColumnNames = builder.allowDuplicateColumnNames

    }

    /**
     * builder class to build a valid ReadOptions instance
     */
    protected static class Builder {
        protected final Source source       //representation of data to read from
        protected String tableName
        protected boolean headers = true
        protected Locale locale = Locale.default
        protected String defaultFileExtension= ""

        Builder () {
            this
        }

        protected Builder (Source source) {
            super()
            this.source = source
        }

        protected Builder(File file) {
            super()
            this.source = new Source(file)
            String fileName = file.getName()
            String[] parts = fileName.tokenize('.')
            this.tableName = parts [0]
        }

        protected Builder(URL url) {
            super()
            this.source = new Source(url.openStream())
            this.tableName = url.toString()
        }

        protected Builder(InputStream stream) {
            super()
            this.source = new Source(stream)
        }

        protected Builder(InputStreamReader reader) {
            super()
            this.source = new Source(reader)
        }

        protected Builder(Reader reader) {
            super()
            this.source = new Source(reader)
        }

        //configuration methods to be called on builder instance
        public Builder tableName(String tableName) {
            this.tableName = tableName
            return this
        }

        public Builder fileExtension(String extension) {
            this.defaultFileExtension = extension
            return this
        }

        public Builder header(boolean hasHeader) {
            this.headers = hasHeader
            return this
        }

        public Builder locale(Locale locale) {
            this.locale = locale
            return this
        }

        /**
         * complete the build process and return
         * @return
         */
        public  ReadOptions build() {
            return new ReadOptions(this)
        }

    }
}
