package scripts

import java.util.function.Function

class Test {
    def rightShift (Function func) {
        func.apply("hello ")
    }
}

test = new Test()

test.rightShift {var -> println ".rightShift got $var"}
test >>  {var -> println " >> got $var"}
