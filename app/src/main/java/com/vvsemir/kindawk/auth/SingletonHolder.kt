package com.vvsemir.kindawk.auth

open class SingletonHolder<out T, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null

    fun getInstance(): T? {
        val instanceRef = instance
        return instanceRef
    }

    fun getInstance(arg: A): T {
        val instanceRef = instance
        if (instanceRef != null) {
            return instanceRef
        }

        return synchronized(this) {
            val instanceRef2 = instance
            if (instanceRef2 != null) {
                instanceRef2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}
