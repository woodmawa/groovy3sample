package com.softwood.util.async

import java.util.function.Function
import java.util.function.Supplier

interface Promise<T> {

    T get()
    T get (long timeout, java.util.concurrent.TimeUnit units)
    Promise<T> onComplete (Function callable)
    Promise<T> onError (Function callable)
    Promise<T> then (Function callable)

}