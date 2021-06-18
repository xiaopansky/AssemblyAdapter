package com.github.panpf.assemblyadapter.sample.ui.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.fondesa.recyclerviewdivider.dividerBuilder
import com.github.panpf.assemblyadapter.pager.AssemblyPagerItemFactory
import com.github.panpf.assemblyadapter.recycler.AssemblyRecyclerAdapter
import com.github.panpf.assemblyadapter.sample.bean.Apps
import com.github.panpf.assemblyadapter.sample.databinding.FragmentAppsBinding
import com.github.panpf.tools4a.dimen.ktx.dp2px

class AppsPagerItemFactory : AssemblyPagerItemFactory<Apps>() {

    override fun match(data: Any?): Boolean {
        return data is Apps
    }

    override fun createView(
        context: Context, container: ViewGroup, position: Int, data: Apps?
    ): View =
        FragmentAppsBinding.inflate(LayoutInflater.from(context), container, false).apply {
            appsRecycler.apply {
                adapter = AssemblyRecyclerAdapter<Any>(
                    listOf(AppCardGridItemFactory()),
                    data?.appList
                )
                layoutManager = GridLayoutManager(context, 2)
                addItemDecoration(
                    context.dividerBuilder().asSpace()
                        .showSideDividers().showFirstDivider().showLastDivider()
                        .size(20.dp2px).build()
                )
            }
        }.root
}