package scripts

import com.softwood.util.async.PromiseImpl

import java.util.concurrent.CompletableFuture

println 'hello'

PromiseImpl<String> p = new PromiseImpl (() -> "new promise") >> new PromiseImpl (() -> " and then another promise")
println p.get()


CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "future hello")
CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> " there")
//def future3 = future.thenAcceptBoth(future2, (s1,s2) -> println s1 + s2 )



println future.get()

println (future >> future2).get()
