package com.example.fullscrollbottomsheet

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView

class FullScrollBottomSheetView : NestedScrollView {
    private var enableScroll = true

    private val view: View by lazy {
        (getChildAt(0) as ViewGroup).getChildAt(0)
    }

    //attrs
    private var fromEnd: Int = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialize(attrs)
    }

    private fun initialize(attrs: AttributeSet?) {
       settingAttrs(attrs)
    }

    private fun settingAttrs(attrs: AttributeSet?) {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.FullScrollBottomSheetView)

        fromEnd = typedArray.getDimensionPixelSize(R.styleable.FullScrollBottomSheetView_fromEnd, 0)
        typedArray.recycle()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                enableScroll = getContentViewRect().contains(ev.rawX.toInt(), ev.rawY.toInt())
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                enableScroll = true
            }
        }
        if (enableScroll) {
            // scroll 을 dispatchTouchEvent 에서 소화
            super.onTouchEvent(ev)
        }

        return super.dispatchTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return enableScroll
    }

    private fun getContentViewRect(): Rect = Rect().apply {
        IntArray(2).let { xy ->
            view.getLocationOnScreen(xy)
            this.top = xy[1]
            this.bottom = xy[1] + view.height
            this.left = xy[0]
            this.right = xy[0] + view.width
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightPx = MeasureSpec.getSize(heightMeasureSpec)

        if (childCount > 0) {
            val child = getChildAt(0)
            child.setPadding(0,heightPx - fromEnd,0,0)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}