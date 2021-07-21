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
package com.github.panpf.assemblyadapter3.compat

import android.database.DataSetObserver
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.github.panpf.assemblyadapter.list.AssemblyListAdapter
import com.github.panpf.assemblyadapter.list.AssemblySingleDataListAdapter
import com.github.panpf.assemblyadapter.list.ConcatListAdapter
import com.github.panpf.assemblyadapter3.compat.internal.CompatDataManager
import com.github.panpf.assemblyadapter3.compat.internal.CompatItemManager
import java.util.*

/**
 * Combined [BaseAdapter], supports combination of multiple items, supports head, tail and more
 */
class CompatAssemblyListAdapter : BaseAdapter, CompatAssemblyAdapter {

    private val dataManager: CompatDataManager
    private val itemManager: CompatItemManager
    private var concatAdapter: ConcatListAdapter? = null

    constructor() {
        dataManager = CompatDataManager()
        itemManager = CompatItemManager(dataManager)
    }

    constructor(dataList: List<Any?>?) {
        dataManager = CompatDataManager(dataList)
        itemManager = CompatItemManager(dataManager)
    }

    constructor(dataArray: Array<Any?>?) {
        dataManager = CompatDataManager(dataArray)
        itemManager = CompatItemManager(dataManager)
    }


    private fun createConcatAdapter(): ConcatListAdapter {
        val headerAdapters = itemManager.headerItemManager.itemList.map { fixedItem ->
            val itemFactoryCompat = CompatItemFactory(fixedItem.itemFactory)
            AssemblySingleDataListAdapter(itemFactoryCompat).apply {
                data = if (fixedItem.isEnabled) fixedItem.data else null
                fixedItem.callback = CompatFixedItem.Callback {
                    data = if (fixedItem.isEnabled) fixedItem.data else null
                }
            }
        }

        val footerAdapters = itemManager.footerItemManager.itemList.map { fixedItem ->
            val itemFactoryCompat = CompatItemFactory(fixedItem.itemFactory)
            AssemblySingleDataListAdapter(itemFactoryCompat).apply {
                data = if (fixedItem.isEnabled) fixedItem.data else null
                fixedItem.callback = CompatFixedItem.Callback {
                    data = if (fixedItem.isEnabled) fixedItem.data else null
                }
            }
        }

        val bodyItemFactoryList = itemManager.itemFactoryList.map { itemFactory ->
            CompatItemFactory(itemFactory)
        }
        val bodyAdapter = AssemblyListAdapter<Any?>(bodyItemFactoryList)
        bodyAdapter.submitDataList(dataManager.getDataList())
        dataManager.addCallback {
            bodyAdapter.submitDataList(dataManager.getDataList())
        }

        val moreFixedItem = itemManager.moreFixedItem
        val moreAdapter = if (moreFixedItem != null) {
            val moreItemFactoryCompat = CompatItemFactory(moreFixedItem.itemFactory)
            AssemblySingleDataListAdapter(moreItemFactoryCompat).apply {
                data = if (moreFixedItem.isEnabled && bodyAdapter.count > 0) moreFixedItem.data else null
                moreFixedItem.callback = CompatMoreFixedItem.Callback {
                    data = if (moreFixedItem.isEnabled && bodyAdapter.count > 0) moreFixedItem.data else null
                }
                bodyAdapter.registerDataSetObserver(object: DataSetObserver() {
                    override fun onChanged() {
                        super.onChanged()
                        data = if (moreFixedItem.isEnabled && bodyAdapter.count > 0) moreFixedItem.data else null
                    }

                    override fun onInvalidated() {
                        super.onInvalidated()
                        data = if (moreFixedItem.isEnabled && bodyAdapter.count > 0) moreFixedItem.data else null
                    }
                })
            }
        } else {
            null
        }

        val adapters = ArrayList<BaseAdapter>().apply {
            addAll(headerAdapters)
            add(bodyAdapter)
            addAll(footerAdapters)
            if (moreAdapter != null) {
                add(moreAdapter)
            }
        }

        val hasStableIds = hasStableIds()
        val config = ConcatListAdapter.Config.Builder().setStableIdMode(
            if (hasStableIds) {
                ConcatListAdapter.Config.StableIdMode.ISOLATED_STABLE_IDS
            } else {
                ConcatListAdapter.Config.StableIdMode.NO_STABLE_IDS
            }
        ).build()
        return ConcatListAdapter(config, adapters)
    }

    override fun getCount(): Int {
        return (concatAdapter ?: createConcatAdapter().apply {
            registerDataSetObserver(AdapterDataSetObserverWrapper(this@CompatAssemblyListAdapter))
            this@CompatAssemblyListAdapter.concatAdapter = this
        }).count
    }

    override fun getItem(position: Int): Any? {
        return (concatAdapter
            ?: throw IllegalStateException("Please call this method after setAdapter"))
            .getItem(position)
    }

    override fun getItemId(position: Int): Long {
        return concatAdapter?.getItemId(position)
            ?: throw IllegalStateException("Please call this method after setAdapter")
    }

    override fun getViewTypeCount(): Int {
        return concatAdapter?.viewTypeCount
            ?: throw IllegalStateException("Please call this method after setAdapter")
    }

    override fun getItemViewType(position: Int): Int {
        return concatAdapter?.getItemViewType(position)
            ?: throw IllegalStateException("Please call this method after setAdapter")
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return concatAdapter?.getView(position, convertView, parent)
            ?: throw IllegalStateException("Please call this method after setAdapter")
    }


    override fun <DATA> addItemFactory(itemFactory: CompatAssemblyItemFactory<DATA>) {
        require(concatAdapter == null) { "item factory list locked" }
        itemManager.addItemFactory(itemFactory, this)
    }

    override val itemFactoryList: List<CompatAssemblyItemFactory<*>>
        get() = itemManager.itemFactoryList


    override fun <DATA> addHeaderItem(fixedItem: CompatFixedItem<DATA>): CompatFixedItem<DATA> {
        return itemManager.addHeaderItem(fixedItem, this)
    }

    override fun <DATA> addHeaderItem(
        itemFactory: CompatAssemblyItemFactory<DATA>,
        data: DATA?
    ): CompatFixedItem<DATA> {
        require(concatAdapter == null) { "item factory list locked" }
        return itemManager.addHeaderItem(itemFactory, data, this)
    }

    override fun <DATA> addHeaderItem(itemFactory: CompatAssemblyItemFactory<DATA>): CompatFixedItem<DATA> {
        require(concatAdapter == null) { "item factory list locked" }
        return itemManager.addHeaderItem(itemFactory, this)
    }

    override fun <DATA> getHeaderItemByFactoryClass(
        clazz: Class<out CompatAssemblyItemFactory<DATA>?>,
        number: Int
    ): CompatFixedItem<DATA> {
        return itemManager.headerItemManager.getItemByFactoryClass(clazz, number)
    }

    override fun <DATA> getHeaderItemByFactoryClass(clazz: Class<out CompatAssemblyItemFactory<DATA>?>): CompatFixedItem<DATA> {
        return itemManager.headerItemManager.getItemByFactoryClass(clazz)
    }

    override fun getHeaderItem(positionInHeaderItemList: Int): CompatFixedItem<*> {
        return itemManager.headerItemManager.getItem(positionInHeaderItemList)
    }

    override fun getHeaderItemData(positionInHeaderItemList: Int): Any? {
        return itemManager.headerItemManager.getItem(positionInHeaderItemList).data
    }

    override fun setHeaderItemData(positionInHeaderItemList: Int, data: Any?) {
        itemManager.headerItemManager.setItemData(positionInHeaderItemList, data)
    }

    override fun isHeaderItemEnabled(positionInHeaderItemList: Int): Boolean {
        return itemManager.headerItemManager.isItemEnabled(positionInHeaderItemList)
    }

    override fun setHeaderItemEnabled(positionInHeaderItemList: Int, enabled: Boolean) {
        itemManager.headerItemManager.setItemEnabled(positionInHeaderItemList, enabled)
    }

    override val headerCount: Int
        get() = itemManager.headerItemManager.availableCount

    override fun getHeaderData(positionInHeaderList: Int): Any? {
        return itemManager.headerItemManager.getItemInAvailableList(positionInHeaderList).data
    }


    override fun <DATA> addFooterItem(fixedItem: CompatFixedItem<DATA>): CompatFixedItem<DATA> {
        require(concatAdapter == null) { "item factory list locked" }
        return itemManager.addFooterItem(fixedItem, this)
    }

    override fun <DATA> addFooterItem(
        itemFactory: CompatAssemblyItemFactory<DATA>,
        data: DATA?
    ): CompatFixedItem<DATA> {
        require(concatAdapter == null) { "item factory list locked" }
        return itemManager.addFooterItem(itemFactory, data, this)
    }

    override fun <DATA> addFooterItem(itemFactory: CompatAssemblyItemFactory<DATA>): CompatFixedItem<DATA> {
        require(concatAdapter == null) { "item factory list locked" }
        return itemManager.addFooterItem(itemFactory, this)
    }

    override fun <DATA> getFooterItemByFactoryClass(
        clazz: Class<out CompatAssemblyItemFactory<DATA>?>,
        number: Int
    ): CompatFixedItem<DATA> {
        return itemManager.footerItemManager.getItemByFactoryClass(clazz, number)
    }

    override fun <DATA> getFooterItemByFactoryClass(clazz: Class<out CompatAssemblyItemFactory<DATA>?>): CompatFixedItem<DATA> {
        return itemManager.footerItemManager.getItemByFactoryClass(clazz)
    }

    override fun getFooterItem(positionInFooterItemList: Int): CompatFixedItem<*> {
        return itemManager.footerItemManager.getItem(positionInFooterItemList)
    }

    override fun getFooterItemData(positionInFooterItemList: Int): Any? {
        return itemManager.footerItemManager.getItem(positionInFooterItemList).data
    }

    override fun setFooterItemData(positionInFooterItemList: Int, data: Any?) {
        itemManager.footerItemManager.setItemData(positionInFooterItemList, data)
    }

    override fun isFooterItemEnabled(positionInFooterItemList: Int): Boolean {
        return itemManager.footerItemManager.isItemEnabled(positionInFooterItemList)
    }

    override fun setFooterItemEnabled(positionInFooterItemList: Int, enabled: Boolean) {
        itemManager.footerItemManager.setItemEnabled(positionInFooterItemList, enabled)
    }

    override val footerCount: Int
        get() = itemManager.footerItemManager.availableCount

    override fun getFooterData(positionInFooterList: Int): Any? {
        return itemManager.footerItemManager.getItemInAvailableList(positionInFooterList).data
    }


    override fun setMoreItem(itemFactory: CompatAssemblyMoreItemFactory): CompatMoreFixedItem {
        require(concatAdapter == null) { "item factory list locked" }
        return itemManager.setMoreItem(itemFactory, this)
    }

    override fun setMoreItem(moreFixedItem: CompatMoreFixedItem): CompatMoreFixedItem {
        require(concatAdapter == null) { "item factory list locked" }
        return itemManager.setMoreItem(moreFixedItem, this)
    }

    override val moreItem: CompatMoreFixedItem?
        get() = itemManager.moreFixedItem

    override val hasMoreFooter: Boolean
        get() = itemManager.hasMoreFooter()

    override fun setMoreItemEnabled(enabled: Boolean) {
        itemManager.moreFixedItem?.isEnabled = enabled
    }

    override fun loadMoreFinished(end: Boolean) {
        itemManager.moreFixedItem?.loadMoreFinished(end)
    }

    override fun loadMoreFailed() {
        itemManager.moreFixedItem?.loadMoreFailed()
    }


    override val dataCount: Int
        get() = dataManager.getDataCount()

    override fun getData(positionInDataList: Int): Any? {
        return dataManager.getData(positionInDataList)
    }

    override var dataList: List<Any?>?
        get() = dataManager.getDataList()
        set(value) {
            dataManager.setDataList(value)
        }

    override fun addAll(collection: Collection<Any?>?) {
        dataManager.addAll(collection)
    }

    override fun addAll(vararg items: Any?) {
        dataManager.addAll(*items)
    }

    override fun insert(`object`: Any, index: Int) {
        dataManager.insert(`object`, index)
    }

    override fun remove(`object`: Any) {
        dataManager.remove(`object`)
    }

    override fun clear() {
        dataManager.clear()
    }

    override fun sort(comparator: Comparator<Any?>) {
        dataManager.sort(comparator)
    }


    override fun getItemCount(): Int {
        return concatAdapter?.count ?: 0
    }

    override fun getPositionInPart(position: Int): Int {
        return itemManager.getPositionInPart(position)
    }

    override fun isHeaderItem(position: Int): Boolean {
        return itemManager.isHeaderItem(position)
    }

    override fun isBodyItem(position: Int): Boolean {
        return itemManager.isBodyItem(position)
    }

    override fun isFooterItem(position: Int): Boolean {
        return itemManager.isFooterItem(position)
    }

    override fun isMoreFooterItem(position: Int): Boolean {
        return itemManager.isMoreFooterItem(position)
    }

    override fun getItemFactoryByPosition(position: Int): CompatAssemblyItemFactory<*> {
        return itemManager.getItemFactoryByPosition(position)
    }

    private class AdapterDataSetObserverWrapper(private val adapter: BaseAdapter) :
        DataSetObserver() {

        override fun onChanged() {
            super.onChanged()
            adapter.notifyDataSetChanged()
        }

        override fun onInvalidated() {
            super.onInvalidated()
            adapter.notifyDataSetInvalidated()
        }
    }
}