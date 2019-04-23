package homecare.android.com.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.style.ReplacementSpan

class RoundedBackgroundSpan(private val _backgroundColor: Int,
                            private val _textColor: Int,
                            private val _padding: Int
) : ReplacementSpan() {

    private val cornerRadius = 10f

    override fun getSize(paint: Paint?, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        return (_padding + (paint?.measureText(text?.subSequence(start, end).toString())
                ?: 0f) + _padding).toInt()
    }

    override fun draw(canvas: Canvas?, text: CharSequence?, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint?) {
        val width = paint?.measureText(text?.subSequence(start, end).toString())
        val rect = RectF(x, top.toFloat(), x + (width ?: 0f) + _padding * 2, bottom.toFloat())
        paint?.color = _backgroundColor
        canvas?.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
        paint?.color = _textColor
        canvas?.drawText(text, start, end, x + _padding, y.toFloat(), paint)
    }
}
