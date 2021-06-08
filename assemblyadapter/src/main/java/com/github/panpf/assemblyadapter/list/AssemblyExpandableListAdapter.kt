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
package com.github.panpf.assemblyadapter.list

import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import com.github.panpf.assemblyadapter.Item
import com.github.panpf.assemblyadapter.ItemFactory
import com.github.panpf.assemblyadapter.R
import com.github.panpf.assemblyadapter.internal.DataManager
import com.github.panpf.assemblyadapter.internal.ItemManager
import java.util.*

class AssemblyExpandableListAdapter<GROUP_DATA, CHILD_DATA>(
    itemFactoryList: List<ItemFactory<*>>
) : BaseExpandableListAdapter() {

    private val itemManager = ItemManager(itemFactoryList)
    private val dataManager = DataManager<GROUP_DATA> { tryNotifyDataSetChanged() }
    private var callback: Callback? = null

    var stopNotifyDataSetChanged = false
    val dataListSnapshot: List<GROUP_DATA>
        get() = dataManager.dataListSnapshot

    constructor(
        itemFactoryList: List<ItemFactory<*>>,
        dataList: List<GROUP_DATA>?
    ) : this(itemFactoryList) {
        dataManager.setDataList(dataList)
    }

    fun getItemCount(): Int {
        return dataManager.dataCount
    }

    fun getItem(position: Int): GROUP_DATA? {
        return dataManager.getData(position)
    }

    override fun getGroupCount(): Int {
        return getItemCount()
    }

    override fun getGroup(groupPosition: Int): GROUP_DATA? {
        return getItem(groupPosition)
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupTypeCount(): Int {
        return itemManager.itemTypeCount
    }

    override fun getGroupType(groupPosition: Int): Int {
        return itemManager.getItemTypeByData(getGroup(groupPosition))
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        val group = getGroup(groupPosition)
        return if (group is AssemblyExpandableGroup) group.getChildCount() else 0
    }

    override fun getChild(groupPosition: Int, childPosition: Int): CHILD_DATA? {
        val group = getGroup(groupPosition)
        @Suppress("UNCHECKED_CAST")
        return if (group is AssemblyExpandableGroup) group.getChild(childPosition) as CHILD_DATA? else null
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildTypeCount(): Int {
        return itemManager.itemTypeCount
    }

    override fun getChildType(groupPosition: Int, childPosition: Int): Int {
        return itemManager.getItemTypeByData(getChild(groupPosition, childPosition))
    }

    override fun hasStableIds(): Boolean {
        return callback != null && callback!!.hasStableIds()
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return callback != null && callback!!.isChildSelectable(groupPosition, childPosition)
    }

    override fun getGroupView(
        groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup
    ): View {
        val groupData = getGroup(groupPosition)
        val groupItemView = convertView ?: itemManager.matchItemFactoryByData(groupData)
            .dispatchCreateItem(parent).apply {
                getItemView().setTag(R.id.aa_tag_item, this)
            }.getItemView()
        @Suppress("UNCHECKED_CAST")
        val groupItem = groupItemView.getTag(R.id.aa_tag_item) as Item<Any>
        if (groupItem is AssemblyExpandableItem<*>) {
            groupItem.groupPosition = groupPosition
            groupItem.isExpanded = isExpanded
            groupItem.childPosition = -1
            groupItem.isLastChild = false
        }
        groupItem.dispatchBindData(groupPosition, groupData)
        return groupItemView
    }

    override fun getChildView(
        groupPosition: Int, childPosition: Int, isLastChild: Boolean,
        convertView: View?, parent: ViewGroup
    ): View {
        val childData = getChild(groupPosition, childPosition)
        val childItemView = convertView ?: itemManager.matchItemFactoryByData(childData)
            .dispatchCreateItem(parent).apply {
                getItemView().setTag(R.id.aa_tag_item, this)
            }.getItemView()
        @Suppress("UNCHECKED_CAST")
        val childItem = childItemView.getTag(R.id.aa_tag_item) as Item<Any>
        if (childItem is AssemblyExpandableItem<*>) {
            childItem.groupPosition = groupPosition
            childItem.isExpanded = false
            childItem.childPosition = childPosition
            childItem.isLastChild = isLastChild
        }
        childItem.dispatchBindData(childPosition, childData)
        return childItemView
    }

    fun setDataList(datas: List<GROUP_DATA>?) {
        dataManager.setDataList(datas)
    }

    fun addData(data: GROUP_DATA): Boolean {
        return dataManager.addData(data)
    }

    fun addData(index: Int, data: GROUP_DATA) {
        dataManager.addData(index, data)
    }

    fun addAllData(datas: Collection<GROUP_DATA>?): Boolean {
        return dataManager.addAllData(datas)
    }

    @SafeVarargs
    fun addAllData(vararg datas: GROUP_DATA): Boolean {
        return dataManager.addAllData(*datas)
    }

    fun removeData(data: GROUP_DATA): Boolean {
        return dataManager.removeData(data)
    }

    fun removeData(index: Int): GROUP_DATA? {
        return dataManager.removeData(index)
    }

    fun removeAllData(datas: Collection<GROUP_DATA>): Boolean {
        return dataManager.removeAllData(datas)
    }

    fun clearData() {
        dataManager.clearData()
    }

    fun sortData(comparator: Comparator<GROUP_DATA>) {
        dataManager.sortData(comparator)
    }

    fun getItemFactoryByItemType(itemType: Int): ItemFactory<*> {
        return itemManager.getItemFactoryByItemType(itemType)
    }

    fun getItemFactoryByPosition(position: Int): ItemFactory<*> {
        return getItemFactoryByItemType(itemManager.getItemTypeByData(getItem(position)))
    }

    private fun tryNotifyDataSetChanged() {
        if (!stopNotifyDataSetChanged) {
            notifyDataSetChanged()
        }
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    interface Callback {
        fun hasStableIds(): Boolean
        fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean
    }
}