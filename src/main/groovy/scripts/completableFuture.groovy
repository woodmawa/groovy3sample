package scripts

import com.softwood.util.async.Promise
import com.softwood.util.async.PromiseFuture

import java.util.concurrent.CompletableFuture

Promise prom =  new PromiseFuture() << () -> "hello"

println prom.get()

PromiseFuture<String> p = new PromiseFuture (() -> "new promise") >> new PromiseFuture (() -> " and then another promise")
println p.get()


CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "future hello")
CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> " there")
//def future3 = future.thenAcceptBoth(future2, (s1,s2) -> println s1 + s2 )



