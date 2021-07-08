/*
 * Copyright (C) 2021 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.assemblyadapter.list.expandable

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlin.reflect.KClass

abstract class SimpleExpandableItemFactory<DATA : Any>(
    dataClass: KClass<DATA>
) : ExpandableItemFactory<DATA>(dataClass) {

    override fun createItem(parent: ViewGroup): ExpandableItem<DATA> {
        val context = parent.context
        val itemView = createItemView(context, LayoutInflater.from(context), parent)
        return SimpleExpandableItem(this, itemView).apply {
            initItem(parent.context, itemView, this)
        }
    }

    protected abstract fun createItemView(
        context: Context, inflater: LayoutInflater, parent: ViewGroup
    ): View

    protected open fun initItem(context: Context, itemView: View, item: ExpandableItem<DATA>) {
    }

    protected abstract fun bindItemData(
        context: Context,
        itemView: View,
        item: ExpandableItem<DATA>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: DATA
    )

    private class SimpleExpandableItem<DATA : Any>(
        private val factory: SimpleExpandableItemFactory<DATA>, itemView: View
    ) : ExpandableItem<DATA>(itemView) {

        override fun bindData(
            bindingAdapterPosition: Int, absoluteAdapterPosition: Int, data: DATA
        ) {
            factory.bindItemData(
                context, itemView, this, bindingAdapterPosition, absoluteAdapterPosition, data
            )
        }
    }
}