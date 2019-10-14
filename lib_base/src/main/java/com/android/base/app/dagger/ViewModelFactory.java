package com.android.base.app.dagger;


import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * {@link androidx.lifecycle.AbstractSavedStateViewModelFactory} is not supported.
 *
 * @author Ztiany
 * Email: ztiany3@gmail.com
 * Date : 2018-11-05 14:31
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

    private final Map<Class<? extends ViewModel>, Provider<ViewModel>> mCreatorMap;

    @Inject
    ViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> classProviderMap) {
        mCreatorMap = classProviderMap;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        Provider<ViewModel> viewModelProvider = mCreatorMap.get(modelClass);
        if (viewModelProvider == null) {
            for (Map.Entry<Class<? extends ViewModel>, Provider<ViewModel>> entry : mCreatorMap.entrySet()) {
                if (modelClass.isAssignableFrom(entry.getKey())) {
                    viewModelProvider = entry.getValue();
                    break;
                }
            }
        }
        if (viewModelProvider != null) {
            return (T) viewModelProvider.get();
        }
        throw new NullPointerException("can not find provider for " + modelClass);
    }

}
