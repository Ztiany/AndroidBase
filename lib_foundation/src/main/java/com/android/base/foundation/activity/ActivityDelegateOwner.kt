package com.android.base.foundation.activity

import androidx.annotation.UiThread
import com.github.dmstocking.optional.java.util.function.Predicate

@UiThread
interface ActivityDelegateOwner {

    fun addDelegate(activityDelegate: ActivityDelegate<*>)

    fun removeDelegate(activityDelegate: ActivityDelegate<*>): Boolean

    fun findDelegate(predicate: (ActivityDelegate<*>) -> Boolean): ActivityDelegate<*>?

    fun getStatus(): ActivityState

}