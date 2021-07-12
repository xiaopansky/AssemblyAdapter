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
package com.github.panpf.assemblyadapter.pager

import androidx.fragment.app.Fragment
import com.github.panpf.assemblyadapter.internal.Matchable
import kotlin.reflect.KClass

/**
 * @see ViewFragmentItemFactory
 */
abstract class FragmentItemFactory<DATA : Any>(val dataClass: KClass<DATA>) : Matchable {

    final override fun matchData(data: Any): Boolean {
        @Suppress("UNCHECKED_CAST")
        return dataClass.isInstance(data) && exactMatchData(data as DATA)
    }

    protected open fun exactMatchData(data: DATA): Boolean = true

    fun dispatchCreateFragment(
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: DATA
    ): Fragment {
        return createFragment(bindingAdapterPosition, absoluteAdapterPosition, data)
    }

    protected abstract fun createFragment(
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: DATA
    ): Fragment
}