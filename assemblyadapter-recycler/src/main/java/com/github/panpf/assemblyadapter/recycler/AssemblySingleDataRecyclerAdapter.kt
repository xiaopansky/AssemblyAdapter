/*
 * Copyright (C) 2017 Peng fei Pan <sky@panpf.me>
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
package com.github.panpf.assemblyadapter.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.panpf.assemblyadapter.AssemblyAdapter
import com.github.panpf.assemblyadapter.ItemFactory
import com.github.panpf.assemblyadapter.recycler.internal.AssemblyRecyclerItem
import com.github.panpf.assemblyadapter.recycler.internal.FullSpanStaggeredGridLayoutManager

open class AssemblySingleDataRecyclerAdapter<DATA> @JvmOverloads constructor(
    private val itemFactory: ItemFactory<DATA>,
    initData: DATA? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), AssemblyAdapter {

    var data: DATA? = initData
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    val dataCount: Int
        get() = if (data != null) 1 else 0

    override fun getItemCount(): Int = if (data != null) 1 else 0

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val item = itemFactory.dispatchCreateItem(parent)
        return AssemblyRecyclerItem(item).apply {
            val layoutManager =
                (parent.takeIf { it is RecyclerView } as RecyclerView?)?.layoutManager
            if (layoutManager is FullSpanStaggeredGridLayoutManager) {
                layoutManager.setFullSpan(itemView, itemFactory)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AssemblyRecyclerItem<*>) {
            @Suppress("UNCHECKED_CAST")
            (holder as AssemblyRecyclerItem<Any?>).dispatchBindData(position, data)
        }
    }


    override fun getItemFactoryByPosition(position: Int): ItemFactory<*> {
        return itemFactory
    }
}