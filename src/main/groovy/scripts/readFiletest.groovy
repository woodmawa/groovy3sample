package scripts

import com.softwood.worksheet.io.DataFrameReader
import com.softwood.worksheet.io.ReaderRegistry
import com.softwood.worksheet.io.csv.CsvReadOptions
import com.softwood.worksheet.io.csv.CsvReader


File inp = new File ("testDataFile.csv")

CsvReader csv = new CsvReader()



DataFrameReader reader = new DataFrameReader ()

reader.csv ("testDataFile.csv")

reader.file ("testDataFile.csv")

System.exit(0)

////////////////
def path = inp.getCanonicalPath()
def uri = inp.toURI()
def name = inp.getName()

InputStream source = this.getClass().getClassLoader().getResourceAsStream(name)

source.eachLine {
    println it
    String[] items = it.tokenize("\t,")
    items = items.collect { it.replace("\"", "").trim() }.each { println it }
    items.each {

        if (it.isLong())
            println "$it : Long"
        else if (it.isNumber())
            println "$it : Number - parsed as " + new BigDecimal(it)
        else if (it.matches (/(\d*)[-.\/]([a-z]*[A-Z]*\d*)[-.\/](\d*)/))
            println "$it : date probably "
        else if (it.matches (/([\D]*\d*)*/))
            println "$it : is String "
        else
            println "cant guess what  type $it is "

    }
}


println "--done--"
