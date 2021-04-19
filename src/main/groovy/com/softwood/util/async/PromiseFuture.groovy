package com.softwood.util.async


import java.util.function.BiFunction
import java.util.function.Function
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

class PromiseFuture<T>  implements Promise<T> {

    @Delegate
    CompletableFuture promise

    PromiseFuture() {}

    PromiseFuture(Supplier callable) {
        promise = CompletableFuture.supplyAsync(callable)
    }

    PromiseFuture(CompletableFuture future) {
        promise = future
    }

    static from (Supplier callable) {
        assert callable
        def p = PromiseFuture::new (callable)
    }

    CompletableFuture asFuture () {
        promise
    }

    Promise<T> rightShift (Promise composable) {
        CompletableFuture future = composable.asFuture()
        CompletableFuture combinedFuture = this.thenCombineAsync(future, (first,second) -> first + second)
        new PromiseFuture (combinedFuture)
    }

    Promise<T> rightShift (Promise composable, BiFunction composeLogic) {
        assert composeLogic
        CompletableFuture future = composable.asFuture()
        CompletableFuture combinedFuture = this.thenCombineAsync(future, composeLogic)
        new PromiseFuture (combinedFuture)
    }


    Promise<T> leftShift (Supplier<T> callable) {
        assert callable
        promise = CompletableFuture.supplyAsync(callable)
        this
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
