package scripts

import com.softwood.util.async.Promise
import com.softwood.util.async.PromiseFuture

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.function.Supplier

//this works
/*CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "future hello")
CompletableFuture<String> chained = future2.thenApplyAsync( (res) -> "$res plus one chain ").thenApplyAsync((res) -> "$res and finally".toString())
println chained.get() */


Promise anyOf = PromiseFuture.selectAndCancelRest(new PromiseFuture ({sleep(1); "one"}),
        new PromiseFuture ({sleep(2); "two"}),
        new PromiseFuture ({sleep(3); "three"})
)
println "first to complete was : " + anyOf.get()


ScheduledFuture in1Secs = PromiseFuture.deferredTask(1, TimeUnit.SECONDS) {"deferred just called!"}
def result = in1Secs.get()
println "result : " + result.get()
def done = in1Secs.isDone()


ScheduledFuture in2Secs = PromiseFuture.deferredTask(2, TimeUnit.SECONDS,  {"deferred just called! with $it"}, "william")
def result2 = in2Secs.get()
println "result2 : " + result2.get()
def done2 = in2Secs.isDone()

PromiseFuture.withScheduler(2, TimeUnit.SECONDS, {it -> println "using withSchedular $it"})

Promise prom =  new PromiseFuture() << ()-> "hello"

println "left shift gives : "+ prom.join()

PromiseFuture promFut = new PromiseFuture() << ()-> sleep(1); "hello"
promFut.complete(10)
println "forced complete task result  : " + promFut.get()

//arglist as a single parameter List, of all arg values
prom = PromiseFuture.task (['hello', 1, "there"]) { one,two, three -> "argList async task : $one, $two, $three"}
println "future task result  : " + prom.get()

//args as varargs at end
prom = PromiseFuture.task ({ one,two, three -> "varargs async task : $one, $two, $three"}, 'hello', 1)
println "future task result  : " + prom.get()


//feature doesnt work on v3.0.7
//prom = Promise.fromTask ({ "async task : $it"}, 'hello')

//this combines the result of both promises and calls BiFunction to process both results
PromiseFuture<String> p = new PromiseFuture (() -> "new promise") >>> new PromiseFuture (() -> " and then another promise")
println "combine 2 futures gives : " +p.get()

//this should take output of first and pass as input this
p = new PromiseFuture (() -> "new promise") >> {res -> "'$res with >>(clos) ".toString()} >> (res)-> "'$res with >>(lambda) ".toString()
println "p result : " + p.get()

Promise p2 = p.then (  (res) -> "'$res' with .then " )


p.onComplete({res, ex -> println "\tonComplete: all done, got '$res'"}).get()


println "p2 result : "+ p2.get()

PromiseFuture.scheduler.shutdown()


