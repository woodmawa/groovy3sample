package com.softwood.util.async

import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.function.Function
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

class PromiseImpl<T>  implements Promise<T> {

    @Delegate
    CompletableFuture promise

    PromiseImpl (Supplier callable) {
        promise = CompletableFuture.supplyAsync(callable)
    }

    PromiseImpl (CompletableFuture future) {
        promise = future
    }

    static from (Supplier callable) {
        def p = PromiseImpl::new (callable)
    }

    Promise<T> rightShift (PromiseImpl composable) {
        CompletableFuture future = composable.promise
        CompletableFuture combinedFuture = this.thenCombineAsync(future, (first,second) -> first + second)
        //CompletableFuture fut = promise.thenComposeAsync((res) -> res + future.get())
        new PromiseImpl (combinedFuture)
    }

    Promise<T> apply (Promise<T> transform) {
        promise.thenApplyAsync(transform)
    }

 /*
    @Override
    T get() {
        return promise.get()
    }

    @Override
    T get(long timeout, TimeUnit unit) {
        return promise.get(timeout, unit)
    }
*/
    @Override
    Promise<T> onComplete(Function callable) {
        return promise.completeAsync(callable)
    }

    @Override
    Promise<T> onError(Function callable) {
        return promise.completeAsync(callable)
    }

    @Override
    Promise<T> then(Function callable) {
        return null
    }

}
