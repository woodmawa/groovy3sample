package scripts

//def a = [5,5,3,1,2,3,3].stream().distinct().sorted().toArray()
def a = [5,5,3,1,2,3,3].stream().distinct().filter (i -> i>2).sorted().forEachOrdered(i -> println "got $i")

//println a.each{ println "val = $it"}