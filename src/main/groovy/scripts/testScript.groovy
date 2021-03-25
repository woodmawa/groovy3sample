package scripts

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors
import java.util.stream.Stream
import java.util.concurrent.TimeUnit

import static java.util.stream.Collectors.*

//replace one char sub seq with another char sequence
println "hello".tr ("e", "f")

def res = Stream.of("hello").map(s -> s::concat(" Will")).collect(Collectors.toList())
println res


List getPathList (String filename) {
    filename.replaceAll("\\/", File.separator)
    String projectRoot = System.getProperty("user.dir")
    def split = projectRoot.split("src")
    projectRoot = split?[0]
    def hasClassPath = filename.startsWith("classpath:")
    List paths = []
    if (hasClassPath) {
        String cleanedFile = (filename - "classpath:").trim()
        paths << projectRoot.concat("build${File.separator}resources${File.separator}main${File.separator}$cleanedFile")
        paths << projectRoot.concat("build${File.separator}classes${File.separator}groovy${File.separator}$cleanedFile")
        paths << projectRoot.concat("build${File.separator}classes${File.separator}java${File.separator}$cleanedFile")

    } else {
        String cleanedFile = filename.trim()
        paths << System.getProperty("user.dir")+File.separator+cleanedFile
    }
    paths
}

File getFileForResource (String filename){
    filename.replaceAll("\\/", File.separator)
    String projectRoot = System.getProperty("user.dir")
    def split = projectRoot.split("src")
    projectRoot = split?[0]
    def hasClassPath = filename.startsWith("resource:")
    if (hasClassPath) {
        String cleanedFile = (filename - "resource:").trim()
        String name = projectRoot.concat("build${File.separator}resources${File.separator}main${File.separator}${cleanedFile}")
        println "looking in resources for $name"
        new File(name)
    } else {
        String cleanedFile = filename.trim()
        new File(cleanedFile)
    }
}

def paths = getPathList ("classpath:testDataFile.csv")
def file = getFileForResource ("resource:testDataFile.csv")
def name = file.canonicalPath
def exists = file.exists()


def start = System.nanoTime()
new File("D:\\OneDrive\\wills-sync\\Tech M\\VF UK OpCo Systems and programmes\\TM for VF design council\\UK Data Lake and monitisation\\companies data\\BasicCompanyDataAsOneFile-2019-09-01.csv".toString()).withInputStream { inps ->
    inps.eachLine { line ->
        if (line.contains "BOOTS") println line
    }
}
def end = System.nanoTime()

long convert = TimeUnit.MILLISECONDS.convert(end-start, TimeUnit.NANOSECONDS)

println "\ngroovy withStream closure  count in ${convert/1000} seconds"

Stream<String> stream = Files.lines(Paths.get("D:\\OneDrive\\wills-sync\\Tech M\\VF UK OpCo Systems and programmes\\TM for VF design council\\UK Data Lake and monitisation\\companies data\\BasicCompanyDataAsOneFile-2019-09-01.csv".toString()))

start = System.nanoTime()
stream.parallel()
        .filter(line -> line.contains "UNILEVER")
        .collect(toList()).each{ println it}
 end = System.nanoTime()

convert = TimeUnit.MILLISECONDS.convert(end-start, TimeUnit.NANOSECONDS)

println "\npararlel count in ${convert/1000} seconds"

//reset the stream
Stream<String> stream2 = file = Files.lines(Paths.get("D:\\OneDrive\\wills-sync\\Tech M\\VF UK OpCo Systems and programmes\\TM for VF design council\\UK Data Lake and monitisation\\companies data\\BasicCompanyDataAsOneFile-2019-09-01.csv".toString()))

println "now sequentially \n"

start = System.nanoTime()
stream2.filter(line -> line.contains "UNILEVER")
        .collect(toList()).each{ println it}
end = System.nanoTime()

convert = TimeUnit.MILLISECONDS.convert(end-start, TimeUnit.NANOSECONDS)

println "\nsequential count in ${convert/1000} seconds"

/*file.withInputStream {def str -> println "str is type ${str.class}"

}*/

