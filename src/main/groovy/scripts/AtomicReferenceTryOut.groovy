package scripts

import java.util.concurrent.atomic.AtomicReference
import java.util.function.UnaryOperator

class MyClass implements UnaryOperator {
    String name = ""

    @Override
    Object apply(Object o) {
        return null
    }
}

MyClass me = new MyClass (name:"william")

AtomicReference ref = new AtomicReference(me)

println ref.get().name

ref.updateAndGet({" woodman"})

println ref.get().name