@file:JvmName("UIKit")

package com.android.base.app.ui

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.android.base.app.Sword
import com.android.base.data.State
import com.android.base.data.StateHandler
import com.android.base.utils.common.isEmpty

//----------------------------------------------Common->Loading->Dialog ----------------------------------------------
interface UIErrorHandler {
    /**处理异常*/
    fun handleError(throwable: Throwable)
}

fun <H, T> H.handleLiveState(
        /**core: data*/
        liveData: LiveData<State<T>>,
        //configuration
        loadingMessage: CharSequence = "",
        forceLoading: Boolean = true,
        onError: ((Throwable) -> Unit)? = null,
        onSuccess: (T?) -> Unit
) where H : UIErrorHandler, H : LoadingView, H : LifecycleOwner {

    liveData.observe(this, Observer { state ->
        when {
            state.isError -> {
                dismissLoadingDialog(Sword.minimumShowingDialogMills) {
                    if (onError != null) {
                        onError(state.error())
                    } else {
                        handleError(state.error())
                    }
                }
            }
            state.isLoading -> {
                showLoadingDialog(loadingMessage, !forceLoading)
            }
            state.isSuccess -> {
                dismissLoadingDialog(Sword.minimumShowingDialogMills) {
                    onSuccess(state.get())
                }
            }//success end
        }
    })

}

fun <H, T> H.handleLiveState2(
        /**core: data*/
        liveData: LiveData<State<T>>,
        //configuration
        loadingMessage: CharSequence = "",
        forceLoading: Boolean = true,
        handler: StateHandler<T>.() -> Unit
) where H : UIErrorHandler, H : LoadingView, H : LifecycleOwner {

    val stateHandler = StateHandler<T>()

    handler(stateHandler)

    liveData.observe(this, Observer { state ->
        when {
            state.isError -> {
                dismissLoadingDialog(Sword.minimumShowingDialogMills) {
                    val onError = stateHandler.onError
                    if (onError != null) {
                        onError(state.error())
                    } else {
                        handleError(state.error())
                    }
                }
            }
            state.isLoading -> {
                showLoadingDialog(loadingMessage, !forceLoading)
            }
            state.isSuccess -> {
                dismissLoadingDialog(Sword.minimumShowingDialogMills) {
                    stateHandler.onSuccess?.invoke(state.get())
                    if (state.hasData()) {
                        stateHandler.onSuccessWithData?.invoke(state.data())
                    } else {
                        stateHandler.onEmpty?.invoke()
                    }
                }
            }//success end
        }
    })

}

//----------------------------------------------Loading In StateView----------------------------------------------
private fun <T> newDefaultChecker(): ((T) -> Boolean)? {
    return { t ->
        (t is CharSequence && (t.isEmpty() || t.isBlank())) || (t is Collection<*> && t.isEmpty()) || (t is Map<*, *> && t.isEmpty())
    }
}

fun <T> RefreshStateLayout.handleStateResult(state: State<T>, isEmpty: ((T) -> Boolean)? = newDefaultChecker(), onEmpty: (() -> Unit)? = null, onResult: ((T) -> Unit)) {
    when {
        state.isLoading -> {
            showLoadingLayout()
        }
        state.isError -> {
            handleResultError(state.error())
        }
        state.isSuccess -> {
            handleResult(state.get(), isEmpty, onEmpty, onResult)
        }
    }
}

fun <T> RefreshStateLayout.handleResult(t: T?, isEmpty: ((T) -> Boolean)? = newDefaultChecker(), onEmpty: (() -> Unit)? = null, onResult: ((T) -> Unit)) {
    if (isRefreshing) {
        refreshCompleted()
    }
    if (t == null || isEmpty?.invoke(t) == true) {
        if (onEmpty != null) {
            onEmpty()
        } else {
            showEmptyLayout()
        }
    } else {
        showContentLayout()
        onResult.invoke(t)
    }
}

fun RefreshStateLayout.handleResultError(throwable: Throwable) {
    if (isRefreshing) {
        refreshCompleted()
    }
    val errorTypeClassifier = Sword.errorClassifier
    if (errorTypeClassifier != null) {
        when {
            errorTypeClassifier.isNetworkError(throwable) -> {
                showNetErrorLayout()
            }
            errorTypeClassifier.isServerError(throwable) -> {
                showServerErrorLayout()
            }
            else -> showErrorLayout()
        }
    } else {
        showErrorLayout()
    }
}

//----------------------------------------------Loading In List----------------------------------------------
fun <T> RefreshListLayout<T>.handleListResult(list: List<T>?, onEmpty: (() -> Unit)? = null) {
    if (isLoadingMore) {
        if (!isEmpty(list)) {
            addData(list)
        }
    } else {
        replaceData(list)
        refreshCompleted()
    }

    if (pager != null) {
        loadMoreCompleted(list != null && pager.hasMore(list.size))
    }

    if (isEmpty) {
        if (onEmpty == null) {
            showEmptyLayout()
        } else {
            onEmpty()
        }
    } else {
        showContentLayout()
    }
}

fun RefreshListLayout<*>.handleListError(throwable: Throwable) {
    if (isRefreshing) {
        refreshCompleted()
    }
    if (isLoadingMore) {
        loadMoreFailed()
    }
    if (isEmpty) {
        val errorTypeClassifier = Sword.errorClassifier
        if (errorTypeClassifier != null) {
            when {
                errorTypeClassifier.isNetworkError(throwable) -> showNetErrorLayout()
                errorTypeClassifier.isServerError(throwable) -> showServerErrorLayout()
                else -> showErrorLayout()
            }
        } else {
            showErrorLayout()
        }
    } else {
        showContentLayout()
    }
}

fun <T> RefreshListLayout<T>.submitListResult(list: List<T>?, hasMore: Boolean, onEmpty: (() -> Unit)? = null) {
    if (isRefreshing) {
        refreshCompleted()
    }

    replaceData(list)
    loadMoreCompleted(hasMore)

    if (isEmpty) {
        if (onEmpty == null) {
            showEmptyLayout()
        } else {
            onEmpty()
        }
    } else {
        showContentLayout()
    }
}

fun <T> RefreshListLayout<T>.handleStateList(state: State<List<T>>, onEmpty: (() -> Unit)? = null) {
    when {
        state.isLoading -> {
            showLoadingIfEmpty()
        }
        state.isError -> {
            handleListError(state.error())
        }
        state.isSuccess -> {
            handleListResult(state.get(), onEmpty)
        }
    }
}

fun <T> RefreshListLayout<T>.handleStateFullList(state: State<List<T>>, hasMore: Boolean, onEmpty: (() -> Unit)? = null) {
    when {
        state.isLoading -> {
            showLoadingIfEmpty()
        }
        state.isError -> {
            handleListError(state.error())
        }
        state.isSuccess -> {
            submitListResult(state.get(), hasMore, onEmpty)
        }
    }
}

fun RefreshListLayout<*>.showLoadingIfEmpty() {
    if (isEmpty) {
        if (isRefreshing) {
            showBlank()
        } else {
            showLoadingLayout()
        }
    }
}

//----------------------------------------------Loading In List And Without State----------------------------------------------
fun <T> RefreshListLayout<T>.handleListResultWithoutState(list: List<T>?, onEmpty: (() -> Unit)? = null) {
    if (isLoadingMore) {
        if (!isEmpty(list)) {
            addData(list)
        }
    } else {
        replaceData(list)
        refreshCompleted()
    }

    if (pager != null) {
        loadMoreCompleted(list != null && pager.hasMore(list.size))
    }

    if (onEmpty != null && isEmpty) {
        onEmpty()
    }

}

fun RefreshListLayout<*>.handleListErrorWithoutState() {
    if (isRefreshing) {
        refreshCompleted()
    }
    if (isLoadingMore) {
        loadMoreFailed()
    }
}

fun <T> RefreshListLayout<T>.submitListResultWithoutState(list: List<T>?, hasMore: Boolean, onEmpty: (() -> Unit)? = null) {
    if (isRefreshing) {
        refreshCompleted()
    }

    replaceData(list)
    loadMoreCompleted(hasMore)

    if (onEmpty != null && isEmpty) {
        onEmpty()
    }
}
