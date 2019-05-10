package com.vvsemir.kindawk.ui

import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.view.View
import com.vvsemir.kindawk.R
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

open class PhotoRecyclerViewItemTouchListener(@NotNull recycleView: RecyclerView,
                                               @Nullable @IdRes specialIds: IntArray?,
                                               @NotNull clickListener: SelectionClickListener )
    : BaseRecyclerViewItemTouchListener<PhotoRecyclerViewItemTouchListener.SelectionClickListener >(recycleView, specialIds, clickListener) {

    companion object {
        private const val SPECIAL_VIEW_CLICK_AREA_EXTENSION = 8
    }

    private var clickPadding: Int

    init {
        clickPadding = (SPECIAL_VIEW_CLICK_AREA_EXTENSION * recycleView.resources.displayMetrics.density).toInt()
    }

    interface SelectionClickListener : BaseRecyclerViewItemTouchListener.ClickListener {
        fun onSelectClick(view: View, position: Int)
    }

    override fun onSpecialViewClick(@NotNull specialChildView: View,
                                    listPosition: Int) {
        when (specialChildView.id) {
            R.id.selectPhotoBox -> clickListener.onSelectClick(specialChildView, listPosition)
            else -> clickListener.onClick(specialChildView, listPosition)
        }
    }

    override fun getSpecialViewClickPadding(): Int = clickPadding
}