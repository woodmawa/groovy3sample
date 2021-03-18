package com.softwood.worksheet.io

/**
 * registry of Readers for various, ReadOption types,  file and mime types
 *
 *
 */
class ReaderRegistry {

    //create three areas one for Options, one for extensions, and one for mime types
    private final Map<String, DataReader<?>> optionTypesRegistry = new HashMap<>()

    private final Map<String, DataReader<?>> extensionsRegistry = new HashMap<>()

    private final Map<String, DataReader<?>> mimeTypesRegistry = new HashMap<>()

    //three registration methods
    void registerOptions(Class<? extends ReadOptions> optionsType, DataReader<?> reader) {
        optionTypesRegistry.put(optionsType.getCanonicalName(), reader)  //use the class name as the key
    }

    void registerExtension(String extension, DataReader<?> reader) {
        extensionsRegistry.put(extension, reader)
    }

    void registerMimeType(String mimeType, DataReader<?> reader) {
        mimeTypesRegistry.put(mimeType, reader)
    }

    @SuppressWarnings("unchecked")
    public <T extends ReadOptions> DataReader<T> getReaderForOptions(T options) {
        String clazz = options.getClass().getCanonicalName()
        DataReader<T> reader = (DataReader<T>) optionTypesRegistry.get(clazz)
        if (reader == null) {
            throw new IllegalArgumentException("No reader registered for class " + clazz)
        }
        return reader
    }

    Optional<DataReader<?>> getReaderForExtension(String extension) {
        return Optional.ofNullable(extensionsRegistry.get(extension))
    }

    Optional<DataReader<?>> getReaderForMimeType(String mimeType) {
        return Optional.ofNullable(mimeTypesRegistry.get(mimeType))
    }
}
