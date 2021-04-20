package com.softwood.util.async

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.function.BiFunction
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

interface Promise<T> extends CompletionStage {

    T get()
    T get (long timeout, java.util.concurrent.TimeUnit units)
    Promise<T> onComplete (Function callable)
    Promise<T> onError (BiFunction callable)
    Promise<T> then (Function callable)
    Promise<T> rightShift (Function nextFunctionWithResult)
    Promise<T> rightShift (Consumer finishWithFunction)
    Promise<Void> rightShift (Runnable lastAction)
    Promise<T> rightShift (Promise<T> combinePromise)
    Promise<T> rightShift (Promise<T> combinePromise, BiFunction composeFunction)
    Promise<T> leftShift (Supplier<T> calculation)
    CompletableFuture asFuture ()
}