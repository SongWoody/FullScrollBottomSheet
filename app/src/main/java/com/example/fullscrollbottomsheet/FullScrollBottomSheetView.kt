package com.example.fullscrollbottomsheet

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView

class FullScrollBottomSheetView : NestedScrollView {
    private var decorView: View? = null
    private var parentHeight = 0
    private var enableScroll = true

    private val view: View by lazy {
        (getChildAt(0) as LinearLayout).getChildAt(0)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onAttachedToWindow() {
        val ctx = context
        if (ctx is Activity) {
            decorView = ctx.window.decorView
            decorView?.viewTreeObserver?.addOnGlobalLayoutListener(preDrawListener)
        }

        super.onAttachedToWindow()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val rect = Rect()
        IntArray(2).let { xy ->
            view.getLocationOnScreen(xy)
            rect.top = xy[1]
            rect.bottom = xy[1] + view.height
            rect.left = xy[0]
            rect.right = xy[0] + view.width
        }


        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                enableScroll = rect.contains(ev.rawX.toInt(), ev.rawY.toInt())
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                enableScroll = true
            }
        }
        if (enableScroll) {
            super.onTouchEvent(ev)
        }

        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return enableScroll
    }

    private val preDrawListener = ViewTreeObserver.OnGlobalLayoutListener {
        decorView?.let{
            parentHeight = it.height
        }
        true
    }

}