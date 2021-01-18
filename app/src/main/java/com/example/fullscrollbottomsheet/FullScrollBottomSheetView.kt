package com.example.fullscrollbottomsheet

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.core.widget.NestedScrollView
import com.example.fullscrollbottomsheet.utils.dpToPx

class FullScrollBottomSheetView : NestedScrollView {
    @IntDef(value = [STATE_HIDDEN, STATE_HALF, STATE_EXPAND, STATE_OVER])
    @Retention(AnnotationRetention.SOURCE)
    annotation class BottomSheetState

    companion object {
        const val STATE_HIDDEN  = 0
        const val STATE_HALF    = 1
        const val STATE_EXPAND  = 2
        const val STATE_OVER    = 3
    }

    @BottomSheetState var state: Int? = STATE_HIDDEN
    private var enableScroll = true

    private val view: View by lazy {
        (getChildAt(0) as ViewGroup).getChildAt(0)
    }
    private val scrollViewY: Int by lazy {
        IntArray(2).let {
            this.getLocationOnScreen(it)
            return@lazy it[1]
        }
    }

    var stateDeterminedRageDP = 80
    var flingGestureDeterminedRangeDp = 10
    var moveGestureDeterminedRangeDp = 10

    private var hiddenPositionY = 0
    private var halfPositionY = 0
    private var expandPositionY = 0

    //attrs
    private var fromTop: Int = 0
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
        val typedArray: TypedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.FullScrollBottomSheetView
        )

        fromTop = typedArray.getDimensionPixelSize(R.styleable.FullScrollBottomSheetView_fromTop, 0)
        fromEnd = typedArray.getDimensionPixelSize(R.styleable.FullScrollBottomSheetView_fromEnd, 0)
        typedArray.recycle()
    }

    var touchStartPosition  = 0
    var touchMiddlePosition = 0
    var touchEndPosition    = 0
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val contentRect = getContentViewRect()
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                touchStartPosition = contentRect.top - scrollViewY
                enableScroll = contentRect.contains(ev.rawX.toInt(), ev.rawY.toInt())
            }
            MotionEvent.ACTION_MOVE -> {
                touchMiddlePosition = contentRect.top - scrollViewY
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                touchEndPosition = contentRect.top - scrollViewY
                enableScroll = true
            }
        }
        if (enableScroll) {
            // scroll 을 dispatchTouchEvent 에서 소화
            super.onTouchEvent(ev)

            if (ev.actionMasked == MotionEvent.ACTION_UP) {
                detectGesture(touchStartPosition, touchMiddlePosition, touchEndPosition)
            }
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
        hiddenPositionY = heightPx - fromEnd
        halfPositionY = heightPx / 2
        expandPositionY = 0 + fromTop

        if (childCount > 0) {
            val child = getChildAt(0)
            child.setPadding(0, heightPx - fromEnd, 0, 0)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun detectGesture(startPosition: Int, middlePosition: Int, endPosition: Int) {
        val isFling = flingGestureDeterminedRangeDp.dpToPx().let {
            (middlePosition - endPosition) > it || (middlePosition - endPosition) < -it
        }
        val isMoveUp = startPosition > endPosition && (startPosition - endPosition) > moveGestureDeterminedRangeDp.dpToPx()
        val isMoveDown = startPosition < endPosition && (endPosition - startPosition) > moveGestureDeterminedRangeDp.dpToPx()

        if (endPosition < expandPositionY) {
            if (isFling && isMoveDown) {
                setState(STATE_EXPAND)
                return
            }

            if (expandPositionY - endPosition < stateDeterminedRageDP.dpToPx()) {
                setState(STATE_EXPAND)
            } else {
                if (isMoveUp) {
                    setState(STATE_OVER)
                } else if (isMoveDown) {
                    setState(STATE_OVER)
                }
            }
        } else {
            if (isFling.not() && expandPositionY - endPosition > -stateDeterminedRageDP.dpToPx()) {
                setState(STATE_EXPAND)
                return
            }
            if (isFling.not() && hiddenPositionY - endPosition < stateDeterminedRageDP.dpToPx()) {
                setState(STATE_HIDDEN)
                return
            }

            if (isFling && isMoveUp) {
                setState(STATE_EXPAND)
                return
            } else if (isFling && isMoveDown) {
                setState(STATE_HIDDEN)
                return
            }

            val withinStateHalfRange: Boolean = stateDeterminedRageDP.dpToPx().let {
                endPosition < halfPositionY + it && endPosition >halfPositionY - it
            }
            if (isFling.not() && withinStateHalfRange) {
                setState(STATE_HALF)
                return
            }

            when {
                isMoveUp -> {
                    setState(STATE_EXPAND)
                }
                isMoveDown -> {
                    setState(STATE_HIDDEN)
                }
                else -> {
                    setState(state!!)
                }
            }
        }
    }

    fun setState(@BottomSheetState state: Int) {
        this.state = state
        when(state) {
            STATE_HIDDEN -> {
                smoothScrollTo(0, 0, 1000)
            }
            STATE_HALF -> {
                smoothScrollTo(0, height / 2, 1000)
            }
            STATE_EXPAND -> {
                smoothScrollTo(0, height - fromTop - fromEnd, 1000)
            }
            STATE_OVER -> {
                //Nothing
            }
        }
    }
}