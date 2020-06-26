package com.strawhat.fitness_club.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FixedForAppBarLayoutManager : LinearLayoutManager {
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(context: Context?) : super(context, RecyclerView.VERTICAL, false)

    override fun findFirstVisibleItemPosition(): Int {
        val item = findVisibleItem(0, childCount, false)
        return item?.let { getPosition(it) } ?: -1
    }

    override fun findFirstCompletelyVisibleItemPosition(): Int {
        val item = findVisibleItem(0, childCount, true)
        return item?.let { getPosition(it) } ?: -1
    }

    override fun findLastVisibleItemPosition(): Int {
        val item = findVisibleItem(childCount - 1, -1, false)
        return item?.let { getPosition(it) } ?: -1
    }

    override fun findLastCompletelyVisibleItemPosition(): Int {
        val item = findVisibleItem(childCount - 1, -1, true)
        return item?.let { getPosition(it) } ?: -1
    }

    private fun findVisibleItem(
        fromIndex: Int,
        toIndex: Int,
        isCompletely: Boolean
    ): View? {
        val next = if (toIndex > fromIndex) 1 else -1
        var i = fromIndex
        while (i != toIndex) {
            val child = getChildAt(i)
            if (checkIsVisible(child, isCompletely)) {
                return child
            }
            i += next
        }
        return null
    }

    private fun checkIsVisible(
        child: View?,
        isCompletely: Boolean
    ): Boolean {
        val location = IntArray(2)
        child!!.getLocationOnScreen(location)
        val parent = child.parent as View
        val parentRect = Rect()
        parent.getGlobalVisibleRect(parentRect)
        return if (orientation == HORIZONTAL) {
            val childLeft = location[0]
            val childRight = location[0] + child.width
            if (isCompletely) childLeft >= parentRect.left && childRight <= parentRect.right else childLeft <= parentRect.right && childRight >= parentRect.left
        } else {
            val childTop = location[1]
            val childBottom = location[1] + child.height
            if (isCompletely) childTop >= parentRect.top && childBottom <= parentRect.bottom else childTop <= parentRect.bottom && childBottom >= parentRect.top
        }
    }
}