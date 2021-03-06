package com.android.base.app.aac

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 *@author Ztiany
 *      Email: ztiany3@gmail.com
 *      Date : 2020-09-21 20:16
 */
abstract class LiveBus {

    private val eventMap = HashMap<String, SingleLiveData<*>>()

    @Synchronized
    fun <T> getSender(eventName: String): MutableLiveData<T> {
        var liveData = eventMap[eventName]
        if (liveData == null) {
            liveData = SingleLiveData<T>()
            eventMap[eventName] = liveData
        }
        return liveData as MutableLiveData<T>
    }

    @Synchronized
    fun <T> getReceiver(eventName: String): LiveData<T> {
        var liveData = eventMap[eventName]
        if (liveData == null) {
            liveData = SingleLiveData<T>()
            eventMap[eventName] = liveData
        }
        return liveData as LiveData<T>
    }

    @Synchronized
    fun <T> destroy(eventName: String): Boolean {
        return eventMap.remove(eventName) != null
    }

}

inline fun <reified T> LiveBus.getClassReceiver(): LiveData<T> {
    return getReceiver(T::class.java.name)
}

inline fun <reified T> LiveBus.getClassSender(): MutableLiveData<T> {
    return getSender(T::class.java.name)
}