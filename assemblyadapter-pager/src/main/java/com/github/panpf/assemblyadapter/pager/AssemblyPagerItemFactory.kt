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
package com.github.panpf.assemblyadapter.pager

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.github.panpf.assemblyadapter.ItemFactory

abstract class AssemblyPagerItemFactory<DATA> : ItemFactory {

    abstract override fun match(data: Any?): Boolean

    fun dispatchCreateView(
        context: Context, container: ViewGroup, position: Int, data: DATA?
    ): View {
        return createView(context, container, position, data)
    }

    abstract fun createView(
        context: Context, container: ViewGroup, position: Int, data: DATA?
    ): View
}