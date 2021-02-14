package scripts

def m = System::println

class A {
    static String shout() {
        "class A, static method: shout out "
    }
    def a = 10
    def aString = "initial string value "

    String toString () {
        "value of A instance is a = $a"
    }
}

def a = new A()
def m2 = a::toString
println a.toString()
def m3 = a.&toString
def m4 = A::shout
def m5 = a::toString

def len= a.aString::length

m.call ("hello")
println "m2 : " + m2()
println "m3 : " + m3()
println "m4 : " + m4()
println "m5 : " + m5()
println "len : " + len()
println "a : " + (a::toString)()