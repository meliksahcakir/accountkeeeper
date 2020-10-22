package com.meliksahcakir.accountkeeper.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.view.BaseAccountAdapter

class SwipeCallbacks(private val context: Context, private val adapter: BaseAccountAdapter) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete)!!
    private val deleteBackground =
        ColorDrawable(ContextCompat.getColor(context, R.color.colorRed))

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        adapter.deleteAccount(position)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        if (dX > 0) { // Swiping to the right
            onSwipeRight(c, viewHolder.itemView, dX.toInt())
        } else if (dX < 0) { // Swiping to the left
            onSwipeLeft(c, viewHolder.itemView, dX.toInt())
        }
    }

    private fun onSwipeRight(c: Canvas, view: View, dX: Int) {
        val backgroundCornerOffset = 20
        val iconOffsetY = (view.height - deleteIcon.intrinsicHeight) / 2
        val iconOffsetX = 50
        val top = view.top + iconOffsetY
        val bottom = top + deleteIcon.intrinsicHeight
        val left = view.left + iconOffsetX
        val right = view.left + iconOffsetX + deleteIcon.intrinsicWidth
        deleteIcon.setBounds(left, top, right, bottom)
        deleteBackground.setBounds(
            view.left,
            view.top,
            view.left + dX + backgroundCornerOffset,
            view.bottom
        )
        deleteBackground.draw(c)
        deleteIcon.draw(c)
    }

    private fun onSwipeLeft(c: Canvas, view: View, dX: Int) {
        val backgroundCornerOffset = 20
        val iconOffsetY = (view.height - deleteIcon.intrinsicHeight) / 2
        val iconOffsetX = 50
        val top = view.top + iconOffsetY
        val bottom = top + deleteIcon.intrinsicHeight
        val left = view.right - iconOffsetX - deleteIcon.intrinsicWidth
        val right = view.right - iconOffsetX
        deleteIcon.setBounds(left, top, right, bottom)
        deleteBackground.setBounds(
            view.right + dX - backgroundCornerOffset,
            view.top,
            view.right,
            view.bottom
        )
        deleteBackground.draw(c)
        deleteIcon.draw(c)
    }
}