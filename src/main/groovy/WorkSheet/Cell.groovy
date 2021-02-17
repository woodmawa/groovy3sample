package WorkSheet

/**
 * cells can be optionally named
 * CoOrdinate:value pairing
 *
 */
class Cell {
    Optional<String> name = Optional.ofNullable(null)
    CoOrdinate cellReference
    def value

    void setName (final String name) {
        this.name = Optional<String>.of(name)
    }

    void updateValue (final update) {
        value = update
    }

    String getValueAsText () {
        String BLANK_STRING = ""
        if (value) {
            value.toString()
        } else {
            BLANK_STRING
        }
    }

    String toString () {
        "cell @{$cellReference : $value}"
    }

}