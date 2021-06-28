package com.github.panpf.assemblyadapter.recycler

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.panpf.assemblyadapter.ItemFactory
import com.github.panpf.assemblyadapter.MatchItemFactory
import kotlin.reflect.KClass

class AssemblyStaggeredGridLayoutManager : StaggeredGridLayoutManager,
    FullSpanStaggeredGridLayoutManager {

    private val fullSpanItemFactoryList: List<KClass<out MatchItemFactory>>

    constructor(
        context: Context, attrs: AttributeSet?,
        defStyleAttr: Int, defStyleRes: Int,
        fullSpanItemFactoryList: List<KClass<out MatchItemFactory>>
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        this.fullSpanItemFactoryList = fullSpanItemFactoryList
    }

    constructor(
        spanCount: Int, orientation: Int,
        fullSpanItemFactoryList: List<KClass<out MatchItemFactory>>
    ) : super(spanCount, orientation) {
        this.fullSpanItemFactoryList = fullSpanItemFactoryList
    }

    constructor(
        spanCount: Int,
        fullSpanItemFactoryList: List<KClass<out MatchItemFactory>>
    ) : super(spanCount, VERTICAL) {
        this.fullSpanItemFactoryList = fullSpanItemFactoryList
    }

    override fun setFullSpan(itemView: View, itemFactory: ItemFactory<*>) {
        val layoutParams = itemView.layoutParams
        if (layoutParams is LayoutParams && fullSpanItemFactoryList.contains(itemFactory::class)) {
            layoutParams.isFullSpan = true
        }
    }
}