package com.softwood.worksheet.io

import java.nio.charset.Charset

/**
 * represents source of data that can be read
 *
 */
class Source {

    protected final Optional<File> file
    protected String defaultFileSuffix = "dat"
    protected final Optional<Reader> reader
    protected final Optional<InputStream> inputStream
    protected final Charset charset

    Source (File file, Charset charset=Charset.defaultCharset()) {

        this.file = Optional.ofNullable(file)
        this.charset = charset
        this.reader = null
        this.inputStream = null
    }

    Source (InputStreamReader reader) {
        this.file = Optional.empty()
        this.reader = Optional.ofNullable(reader)
        this.inputStream = Optional.empty()
        this.charset = Charset.forName (reader.getEncoding())
    }

    Source (InputStream inputStream, Charset charset=Charset.defaultCharset()) {
        this.file = Optional.empty()
        this.reader = Optional.empty()
        this.inputStream = Optional.ofNullable(inputStream)
        this.charset = charset
    }

    static Source fromString (String s) {
        new Source (new StringReader(s))
    }

    File file() {
        file.orElse()
    }

    Reader reader () {
        reader.orElse()
    }

    InputStream inputStream() {
        inputStream.orElse()
    }

    Charset getCharset () {
        charset
    }

    /**
     * createReader looks at the Source instance and returns the best fit
     * starting with any cachedBytes if present
     *
     * @param cachedBytes
     * @return
     */
    Reader createReader (byte[] cachedBytes = null) {
        InputStreamReader isr
        if (cachedBytes) {
            isr = new InputStreamReader (new ByteArrayInputStream (cachedBytes), charset)
        } else if (inputStream.isPresent()) {
            isr = new InputStreamReader (inputStream.get())
        } else if (reader.isPresent()) {
            isr = reader.get()
        } else if (file.isPresent()){
            isr = new InputStreamReader(new FileInputStream (file.get()), charset)
        }
        //todo what to do is ISR should be null
        return isr
    }

}
