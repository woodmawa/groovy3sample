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
    protected final Optional<InputStreamReader> inputStreamReader
    protected final Charset charset

    Source (File file, Charset charset=Charset.defaultCharset()) {

        this.file = Optional.ofNullable(file)
        this.charset = charset
        this.reader = Optional.empty()
        this.inputStreamReader = Optional.empty()
    }

    Source (Reader reader) {
        this.file = Optional.empty()
        this.reader = Optional.ofNullable(reader)
        this.inputStreamReader = Optional.empty()
        this.charset = Charset.forName (reader.getEncoding())
    }

    Source (InputStream inputStream, Charset charset=Charset.defaultCharset()) {
        this.file = Optional.empty()
        this.reader = Optional.empty()
        this.inputStreamReader = Optional.ofNullable(new InputStreamReader (inputStream))
        this.charset = charset
    }

    Source (InputStreamReader inputStreamReader, Charset charset=Charset.defaultCharset()) {
        this.file = Optional.empty()
        this.reader = Optional.empty()
        this.inputStreamReader = Optional.ofNullable(inputStreamReader)
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
        inputStreamReader.orElse()
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
        } else if (file.isPresent()) {
            File fileRef = file.get()
            String path = fileRef.name
            InputStream is = Source.class.getClassLoader().getResourceAsStream(path)
            isr = new InputStreamReader(is, charset)
        } else if (inputStreamReader.isPresent()) {
            isr = inputStreamReader.get()
        } else if (reader.isPresent()) {
            isr = reader.get() as InputStreamReader //try a caste
        }
        //todo what to do is ISR should be null
        return isr
    }

}
