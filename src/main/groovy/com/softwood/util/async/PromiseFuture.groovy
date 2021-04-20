package com.softwood.util.async

import java.util.concurrent.Future
import java.util.function.BiFunction
import java.util.function.Function
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

class PromiseFuture<T>  implements Promise<T>  {

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

    //groovy as type convertor Future is an interface
    CompletableFuture asType (Class clazz) {
        if (clazz == CompletableFuture || clazz == Future)
            promise
        else
            null
    }

    /**
     * with another just another promise, we can't access the code for this so we have to use .thenCombineAsync
     * @param composable
     * @return
     */
    Promise<T> rightShift (Promise composable) {
        CompletableFuture future = composable.asFuture()
        //have to use combine here as cant access the code for the composable
        CompletableFuture combinedFuture = this.thenCombineAsync (future, {first, second ->
            if (first.respondsTo('plus')) {
                (first + second)
            } else {
                [first,second]
            }
        })
        new PromiseFuture (combinedFuture)
    }

    /**
     * if passed BiFunction use thenCombineAsync
     * @param composable
     * @param composeLogic
     * @return
     */
    Promise<T> rightShift (Promise composable, BiFunction composeLogic) {
        assert composeLogic
        CompletableFuture future = composable.asFuture()
        CompletableFuture combinedFuture = this.thenCombineAsync(future, composeLogic)
        new PromiseFuture (combinedFuture)
    }

    /**
     * with a function, we can use thenApplyAsync which takes the result of this and passes it to the funcNext-
     * but code assumes the funcNext does not return another CompletableFuture else we get a nested result
     * @param funcNext
     * @return
     */
    Promise<T> rightShift (Function funcNext) {
        //takes the output of this when complete and invokes FuncNext with it
        //assumes func isn't returning another completableFuture
        CompletableFuture composedFuture = this.thenApplyAsync(funcNext)
        PromiseFuture promise = new PromiseFuture (composedFuture)
        promise
    }


    /**
     * uses the combine method to pass both results to the compose logic
     * takes the result of this, and the composable and passes both to the BiFunction
     * @param composable
     * @param composeLogic
     * @return
     */
    Promise<T> rightShiftUnsigned (Promise composable, BiFunction composeLogic=null) {
        CompletableFuture future = composable.asFuture()
        CompletableFuture combinedFuture
        if (composeLogic)
            combinedFuture = this.thenCombineAsync(future, composeLogic)
        else
            combinedFuture = this.thenCombineAsync(future, {first, second ->
                if (first.respondsTo('plus')) {
                    (first + second)
                } else {
                    [first,second]
                }
            } )

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

    @Override
    Promise<T> onComplete(Function callable) {
        return promise.whenCompleteAsync(callable)
    }

    @Override
    Promise<T> onError(BiFunction handler) {
        //handler should present as (res, excep) -> {}
        return new PromiseFuture (promise.handle(handler ) )
    }

    @Override
    Promise<T> then(Function callable) {
        return rightShift (callable)
    }

}
