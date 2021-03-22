package com.softwood.worksheet.io

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.regex.Pattern

enum DataValueType {
    NUMBER,     //whole numbers integers or longs, BigInteger etc
    DECIMAL,    //fractional numbers, floats, doubles, BigDecimal
    DATE,       // held as LocalDate
    TIME,       //held as LocalTime
    DATETIME,   //held as LocalDateTime
    TEXT,       //CharBuffer, String, Gstring, etc
    UNKNOWN,    //cant figure out what this is
    UNDEFINED   //starting condition - type is not yet defined
}

/**
 * parses a line of text and tries to read typed values from tokenised string
 *
 *
 */
class LineParser {

    final String delimiters = "\t,|"
    final static Pattern isoDateTimePattern = Pattern.compile (/ (((2000|2400|2800|((19|2[0-9])(0[48]|[2468][048]|[13579][26])))-02-29)|(((19|2[0-9])[0-9]{2})-02-(0[1-9]|1[0-9]|2[0-8]))|(((19|2[0-9])[0-9]{2})-(0[13578]|10|12)-(0[1-9]|[12][0-9]|3[01]))|(((19|2[0-9])[0-9]{2})-(0[469]|11)-(0[1-9]|[12][0-9]|30)))T([01][0-9]|[2][0-3]):[0-5][0-9]:[0-5][0-9]\.[0-9]{3}Z /)
    final static Pattern datePattern = Pattern.compile (/(\d*)[-.\/]([a-z]*[A-Z]*\d*)[-.\/](\d*)/)
    final static Pattern timePattern = Pattern.compile (/(\d{1,2}):(\d{1,2}):(\d{1,2}).(\d*)/)
    final static Pattern textPattern = Pattern.compile (/([\D]*\d*)*/)

    LineParser () {
        this
    }

    LineParser (String delimiters) {
        this.delimiters = delimiters
        this
    }

    class ColumnItem {
        DataValueType type
        Long column
        def value

        String toString() {
            "ColumnItem (type: $type, value: $value)"
        }
    }

    HashMap parse (String line ) {
        def row = new LinkedHashMap()

        String[] items = line.tokenize(delimiters).collect{it.replace("\"", "").trim()}

        def item
        for (column in 0..items.size() - 1) {
            item = items[column]
            if (item.isLong())
                row << [(column) : new ColumnItem (type: DataValueType.NUMBER, value: Long.valueOf(item), column: column)]
            else if (item.isNumber())
                row << [(column) : new ColumnItem (type:DataValueType.DECIMAL, value: new BigDecimal(item), column: column)]
            else if (datePattern.matcher (item).matches() )
                row << [(column) : new ColumnItem (type:DataValueType.DATE, value: buildDate (item), column: column)]
            else if (timePattern.matcher (item).matches())
                row << [(column) : new ColumnItem (type:DataValueType.TIME, value: buildTime (item), column: column)]
            else if (isoDateTimePattern.matcher(item).matches())
                row << [(column) : new ColumnItem (type:DataValueType.DATETIME, value: buildTime (item), column: column)]
            else if (textPattern.matcher(item).matches())
                row << [(column) : new ColumnItem (type:DataValueType.TEXT, value: item, column: column)]
            else
                row << [(column) : new ColumnItem (type:DataValueType.UNKNOWN, value: item, column: column)]

        }
        row
    }

    /**
     * makes assumptions about date string and tries to build a LocalDate for the entry
     * @param dateString
     * @return
     */
    private LocalDate buildDate (String dateString ) {

        String[] parts = dateString.tokenize("-/.").collect{it.trim().toLowerCase()}

        final int defaultCentury = 2000

        final Map monthLookup = [january:1, february:2, march:3,
                april:4,  may:5, june:6,  july:7, august:8,
                september:9, october:10, november:11, december:12]

        //need to make some assumptions now
        int day, year, month
        if ( parts.size() == 3) {   //address short form later
            if (parts[0].size() < parts[2].size() ) {
                day = Integer.valueOf(parts[0])
                year = Integer.valueOf(parts[2])
            } else if (parts[2].size() < parts[0].size()  ){
                day = Integer.valueOf(parts[2])
                year = Integer.valueOf(parts[0])
            } else {
                //assume day is first, year is last
                day = Integer.valueOf(parts[0])
                year = defaultCentury + Integer.valueOf(parts[2])
                assert year in 2000..2999
            }
            if (parts[1][0].isNumber() )
                month = Integer.valueOf(parts[1])
            else {
                def monStr = monthLookup.keySet().find {
                    it.contains(parts[1])}
                month = monthLookup[monStr]
           }
         }
        else (parts.size() == 2) {  //assume day and moth only

        }

        LocalDate date = new LocalDate (year, month, day)
    }

    private LocalTime buildTime (String timeString) {
        LocalTime.parse (timeString)
    }

    private LocalDateTime buildDateTime (String timeString) {
        LocalDateTime.of (timeString)
    }
}
