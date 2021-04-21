package scripts

import com.softwood.util.async.Promise
import com.softwood.util.async.PromiseFuture

import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

//this works
/*CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "future hello")
CompletableFuture<String> chained = future2.thenApplyAsync( (res) -> "$res plus one chain ").thenApplyAsync((res) -> "$res and finally".toString())
println chained.get() */

Promise prom =  new PromiseFuture() << ()-> "hello"

println "left shift gives : "+ prom.get()

prom = PromiseFuture.task ({ "async task : $it"}, 'hello')
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


