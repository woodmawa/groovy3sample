package com.softwood.util.async

import groovy.transform.EqualsAndHashCode

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.function.BiConsumer
import java.util.function.BiFunction
import java.util.function.Consumer
import java.util.function.Function
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier


// cludge: just using closure doesnt seem to return the result when scheduled to run in future -
// so have to wrap the closure in class that implements Callable!
class ClosureCallable<T>  implements Callable {
    Closure<T> work

    ClosureCallable (Closure<T> clos) {
        work = clos.clone()
    }

    T call() throws Exception {
        return work.call()
    }
}

@EqualsAndHashCode (includeFields = true)
class PromiseFuture<T>  implements Promise<T>  {

    static ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(10)

    @Delegate
    CompletableFuture promise

    PromiseFuture() {this}

    PromiseFuture(Supplier callable) {
        promise = CompletableFuture.supplyAsync(callable)
        this
    }

    PromiseFuture(CompletableFuture future) {
        promise = future
        this
    }

    PromiseFuture(Future future) {
        promise = future as CompletableFuture
        this
    }

    static from (Supplier callable) {
        assert callable
        PromiseFuture::new (callable)
    }

    static from (Callable callable) {
        assert callable
        PromiseFuture::new (callable)
    }

    static task (arg, Closure function) {
        assert function
        assert function.maximumNumberOfParameters == 1

        Closure functionWithParam = {function.call(arg)}

        //wrap function as Supplier<T> form for the Promise
        def promise = PromiseFuture::new (functionWithParam)
        promise
    }

    /**
     * for form uses varargs to get the args so it must be and the end, after the closure
     * @param function
     * @param args
     */
    static task (Closure function, Object...args) {
        assert function

        List argList = args
        def closureListSize = function.maximumNumberOfParameters
        def argListSize = args.size()
        Closure functionWithParam

        if (closureListSize == 0) {
            functionWithParam = function
        } else {
            if (argListSize < closureListSize) {
                //pad arglist to exact size with nulls
                for (i in argListSize..<closureListSize) {
                    argList.add(null)
                }
            } else if (argListSize > closureListSize) {
                argList = argList.subList(0, argListSize - 1)
            }

            //wrap function as Supplier<T> form for the Promise
            functionWithParam = { function.call(*argList) }
        }

        def result = functionWithParam()

        def promise = PromiseFuture::new (functionWithParam)
        promise

    }

    /**
     * this form takes a explicit single parameter that must be a list
     *
     * @param argList
     * @param function
     * @return
     */
    static task (List argList, Closure function) {
        assert function

        //spread args and use var args method
        task (function, *argList)

    }

    static withScheduler (long delay, TimeUnit unit, Closure work) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1)
        scheduler.schedule({work(scheduler)}, delay, unit)
        scheduler.shutdown()
    }

    /**
     * setup a task to run in the future and return scheduleFuture whos get() will return a PromiseFuture
     * @param delay
     * @param unit
     * @param callable
     * @return ScheduledFuture
     */
    static ScheduledFuture deferredTask (long delay, TimeUnit unit, Callable callable) {
        //set up the work to be called at future time
        Callable deferredPromiseFuture = new ClosureCallable ({ PromiseFuture.from (callable) })
        ScheduledFuture deferred = scheduler.schedule (deferredPromiseFuture, delay, unit)
        deferred
    }

    static ScheduledFuture deferredTask (long delay, TimeUnit unit, Closure function, arg=null) {
        //set up the work to be called at future time
        Callable deferredPromiseFuture = new ClosureCallable({ PromiseFuture.task (function, arg)} )
        ScheduledFuture deferred = scheduler.schedule (deferredPromiseFuture, delay, unit)
        deferred
    }

    /**
     * cancel a scheduled deferredTask
     * @param scheduledTask
     * @return
     */
    static ScheduledFuture cancelDeferredTask (ScheduledFuture scheduledTask) {
        assert scheduledTask
        scheduledTask.cancel(true)
        scheduledTask
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
        CompletableFuture combinedFuture = promise.thenCombineAsync(future, composeLogic)
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
        CompletableFuture composedFuture = promise.thenApplyAsync(funcNext)
        PromiseFuture promise = new PromiseFuture (composedFuture)
        promise
    }

    Promise<T> rightShift (Closure clos) {
        //takes the output of this when complete and invokes FuncNext with it
        //assumes func isn't returning another completableFuture
        CompletableFuture composedFuture = promise.thenApplyAsync(clos)
        PromiseFuture promise = new PromiseFuture (composedFuture)
        promise
    }

     Promise<Void> rightShift (Consumer finishWithFunction) {
        //takes the output of this when complete and invokes FuncNext with it
        //assumes func isn't returning another completableFuture
        CompletableFuture composedFuture = promise.thenAcceptAsync(finishWithFunction)
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
            combinedFuture = promise.thenCombineAsync(future, composeLogic)
        else
            combinedFuture = promise.thenCombineAsync(future, {first, second ->
                if (first.respondsTo('plus')) {
                    (first + second)
                } else {
                    [first,second]
                }
            } )

        new PromiseFuture (combinedFuture)
    }

    Promise<T> leftShift (Supplier<T> supplier) {
        assert supplier
        promise = CompletableFuture.supplyAsync(supplier)
        this
    }

    @Override
    /**
     * expects a BiConsumer<?super T>, ? super Throwable
     */
    Promise<T> onComplete(BiConsumer action) {
        return new PromiseFuture (promise.whenCompleteAsync(action ))
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

    static Promise select (Promise... promises) {
        def first = CompletableFuture.anyOf (*promises.promise)

        //should i cancel the others?
        new PromiseFuture (first)
    }

    static Promise selectAndCancelRest (Promise... promises) {
        def first = CompletableFuture.anyOf (*promises.promise)

        //should i cancel the others?
        promises.each {it.promise.cancel(true)}
        new PromiseFuture (first)
    }

    static Promise whenAll (Promise... promises) {
        def done = CompletableFuture.allOf(*promises.promise)

        new PromiseFuture (done)
    }
}
