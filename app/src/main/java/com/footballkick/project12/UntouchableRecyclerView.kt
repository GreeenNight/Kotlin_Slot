package com.footballkick.project12

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class UntouchableRecyclerView(context: Context, attrs: AttributeSet?) : RecyclerView(context, attrs) {
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false  // Consume the touch event and don't pass it to child views
    }

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        return true
    }

}