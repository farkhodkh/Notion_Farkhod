package ru.farkhodkhaknazarov.notion_farkhod.ui.camera

import android.content.Context
import android.util.AttributeSet
import android.view.TextureView
import kotlin.properties.Delegates

class AutoFitTextureView(context: Context, attrs: AttributeSet?, defStyle: Int) :
    TextureView(context, attrs, defStyle){

    var mRatioWidth = 0
    var mRatioHeight = 0

    constructor(context: Context): this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet): this(context, attrs, 0)

    fun setAspectRatio(width: Int, height: Int){
        require(!(width < 0 || height < 0)) { "Size cannot be negative." }
        mRatioWidth = width
        mRatioHeight = height
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height)
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth)
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height)
            }
        }
    }
}