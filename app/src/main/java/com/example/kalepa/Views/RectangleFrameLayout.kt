package com.example.charactermanager.Views

import android.util.AttributeSet
import android.widget.FrameLayout
import android.content.Context
class RectangleFrameLayout @JvmOverloads constructor( // 1
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int,
                           heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec + (widthMeasureSpec/2)) // 2
    }
}