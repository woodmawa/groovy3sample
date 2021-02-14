package scripts

import org.codehaus.groovy.runtime.MethodClosure

import static java.util.stream.Collectors.toList
import java.util.stream.Stream
// also useful for your own classes
import groovy.transform.Canonical
import java.util.stream.Collectors
import groovy.transform.EqualsAndHashCode



class Demo {

    static def doStuff () {
        println "hello from doStuff "
    }
}

def demo = new Demo()

def fnc = Demo::doStuff  //needs to be static reference if only the class

fnc()

count = 3
do {
    println count--
} while (count)

println "forEach "
(1..10).forEach((it) -> { println it })


println "assert stream  "
assert (1..10).stream()
        .filter((it) -> it % 2 == 0)
        .map((it) -> it * 2)
        .collect(toList()) == [4, 8, 12, 16, 20]

// lambda general form
def add = (int x, int y) -> { def z = y; return x + z }
assert add(3, 4) == 7

// curly braces are optional for a single expression
def sub = (int x, int y) -> x - y
assert sub(4, 3) == 1

// parameter types are optional
def mult = (x, y) -> x * y
assert mult(3, 4) == 12

// no parentheses required for a single parameter with no type
def isEven = n -> n % 2 == 0
assert isEven(6)
assert !isEven(7)

// no arguments case
def theAnswer = () -> 42
assert theAnswer() == 42

// any statement requires braces
def checkMath = () -> { assert 1 + 1 == 2 }
checkMath()

// example showing default parameter values (no Java equivalent)
def addWithDefault = (int x, int y = 100) -> x + y
assert addWithDefault(1, 200) == 201
assert addWithDefault(1) == 101

// class::staticMethod
assert ['1', '2', '3'] ==
        Stream.of(1, 2, 3)
                .map(String::valueOf)
                .collect(toList())

// class::instanceMethod
assert ['A', 'B', 'C'] ==
        ['a', 'b', 'c'].stream()
                .map(String::toUpperCase)
                .collect(toList())

// normal constructor
def r = Random::new
assert r().nextInt(10) in 0..9

// array constructor refs are handy when working with various Java libraries, e.g. streams
assert [1, 2, 3].stream().toArray().class.name == '[Ljava.lang.Object;'
assert [1, 2, 3].stream().toArray(Integer[]::new).class.name == '[Ljava.lang.Integer;'

// works with multi-dimensional arrays too
def make2d = String[][]::new
def tictac = make2d(3, 3)
tictac[0] = ['X', 'O', 'X']
tictac[1] = ['X', 'X', 'O']
tictac[2] = ['O', 'X', 'O']
assert tictac*.join().join('\n') == '''
XOX
XXO
OXO
'''.trim()


/* assert !(45 instanceof Date) // old form */
assert 45 !instanceof Date

assert 4 !in [1, 3, 5, 7]


@Canonical
class Animal {
    String kind
}

def a = Animal::new
assert a('lion').kind == 'lion'

def c = Animal
assert c::new('cat').kind == 'cat'

MethodClosure m = c::toString
println "default toString() : " + c

m.doCall()

def pets = ['cat', 'dog'].stream().map(Animal::new)
def names = pets.map(Animal::toString).collect(Collectors.joining( "," ))
println names
assert names == 'scripts.Animal(cat),scripts.Animal(dog)'


println "equals and hash code "
@EqualsAndHashCode
class Creature { String type }

def cat = new Creature(type: 'cat')
def copyCat = cat
def lion = new Creature(type: 'cat')

assert cat.equals(lion) // Java logical equality
assert cat == lion      // Groovy shorthand operator

assert cat.is(copyCat)  // Groovy identity
assert cat === copyCat  // operator shorthand
assert cat !== lion     // negated operator shorthand

println "safe indexing "

String[] array = ['a', 'b']
assert 'b' == array?[1]      // get using normal array index
array?[1] = 'c'              // set using normal array index
assert 'c' == array?[1]

array = null
assert null == array?[1]     // return null for all index values
array?[1] = 'c'              // quietly ignore attempt to set value
assert null == array?[1]

def personInfo = [name: 'Daniel.Sun', location: 'Shanghai']
assert 'Daniel.Sun' == personInfo?['name']      // get using normal map index
personInfo?['name'] = 'sunlan'                  // set using normal map index
assert 'sunlan' == personInfo?['name']

personInfo = null
assert null == personInfo?['name']              // return null for all map values
personInfo?['name'] = 'sunlan'                  // quietly ignore attempt to set value
assert null == personInfo?['name']

println "interface default methods "
interface Greetable {
    String target()

    default String salutation() {
        'Greetings'
    }

    default String greet() {
        "${salutation()}, ${target()}"
    }
}

class Greetee implements Greetable {
    String name
    @Override
    String target() { name }
}

def daniel = new Greetee(name: 'Daniel')
assert 'Greetings, Daniel' == "${daniel.salutation()}, ${daniel.target()}"
assert 'Greetings, Daniel' == daniel.greet()

println "non static inner class "
public class Computer {
    public class Cpu {
        int coreNumber

        public Cpu(int coreNumber) {
            this.coreNumber = coreNumber
        }
    }
}

//
//assert 4 == new Computer().new Cpu(4).coreNumber