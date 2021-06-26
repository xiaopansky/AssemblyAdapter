package com.github.panpf.assemblyadapter.sample.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.panpf.assemblyadapter.list.expandable.BindingExpandablePlaceholderItemFactory
import com.github.panpf.assemblyadapter.sample.databinding.ItemAppGroupPlaceholderBinding

class AppGroupPlaceholderItemFactory :
    BindingExpandablePlaceholderItemFactory<ItemAppGroupPlaceholderBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemAppGroupPlaceholderBinding {
        return ItemAppGroupPlaceholderBinding.inflate(inflater, parent, false)
    }
}