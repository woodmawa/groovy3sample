package scripts

import com.softwood.util.async.Promise
import com.softwood.util.async.PromiseFuture

import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

Promise prom =  new PromiseFuture() << ()-> "hello"

println prom.get()

//this combines the result of both promises and calls BiFunction to process both results
PromiseFuture<String> p = new PromiseFuture (() -> "new promise") >>> new PromiseFuture (() -> " and then another promise")
println p.get()

//this should take output of first and pass as input this
p = new PromiseFuture (() -> "new promise").then {res -> "'$res with .then"} >> { res ->
    "'$res' right shifted "
}
println p.get()

CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "future hello")
CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> " there")
//def future3 = future.thenAcceptBoth(future2, (s1,s2) -> println s1 + s2 )



