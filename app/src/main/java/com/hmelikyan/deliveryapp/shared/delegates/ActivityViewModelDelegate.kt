package com.hmelikyan.deliveryapp.shared.delegates

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProviders
import com.hmelikyan.deliveryapp.view.root.BaseActivity

class ActivityViewModelDelegate<T : AndroidViewModel>(
    private val viewModelClass: Class<T>,
    private val activity: BaseActivity
) : Lazy<T> {
    private var viewModel: T? = null

    override val value: T
        get():T {
            if (viewModel == null) {
                viewModel = ViewModelProviders.of(activity).get(viewModelClass)
            }
            return viewModel!!
        }

    override fun isInitialized(): Boolean = viewModel != null

}

inline fun <reified T : AndroidViewModel> BaseActivity.viewModelProvider():
        Lazy<T> = ActivityViewModelDelegate(T::class.java, this)