package com.softwood.util.async

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.TimeUnit
import java.util.function.BiConsumer
import java.util.function.BiFunction
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

interface Promise<T> extends CompletionStage {

    T get()
    T get (long timeout, TimeUnit units)
    Promise<T> onComplete (BiConsumer action)
    Promise<T> onError (BiFunction error)
    Promise<T> then (Function callable)
    Promise<T> rightShift (Function nextFunctionWithResult)
    Promise<Void> rightShift (Consumer finishWithConsumer)
    //Promise<Void> rightShift (Runnable lastAction)
    Promise<T> rightShift (Promise<T> followedByPromise)
    Promise<T> rightShift (Promise<T> combinePromise, BiFunction composeFunction)
    Promise<T> rightShiftUnsigned (Promise<T> combinePromise)
    Promise<T> rightShiftUnsigned (Promise<T> combinePromise, BiFunction composeFunction)

    Promise<T> leftShift (Supplier<T> calculation)
    CompletableFuture asFuture ()

    /* doesnt work in groovy 3.0.7 and neither does static methods
    default Promise<T> fromTask (Function function, arg) {
        PromiseFuture.task(function, arg)
    }*/
}