package com.softwood.worksheet.io

class ReadOptions {

    protected Source source
    protected String tableName
    protected boolean headers
    protected Locale locale

    protected ReadOptions(ReadOptions.Builder builder) {
        source = builder.source
        tableName = builder.tableName
        headers = builder.headers
        locale = builder.locale
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

        protected Builder (Source source) {
            this.source = source
        }

        protected Builder(File file) {
            this.source = new Source(file)
            this.tableName = file.getName()
        }

        protected Builder(URL url) {
            this.source = new Source(url.openStream())
            this.tableName = url.toString()
        }

        protected Builder(InputStream stream) {
            this.source = new Source(stream)
        }

        protected Builder(InputStreamReader reader) {
            this.source = new Source(reader)
        }

        protected Builder(Reader reader) {
            this.source = new Source(reader)
        }

        public Builder tableName(String tableName) {
            this.tableName = tableName
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
