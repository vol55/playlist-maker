package com.example.playlistmaker.player.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import com.example.playlistmaker.R


class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var playIcon: Drawable? = null
    private var pauseIcon: Drawable? = null

    private var isPlaying = false

    private val drawableRect = RectF()
    private val drawableRectInt = android.graphics.Rect()

    var onPlayPauseToggle: (() -> Unit)? = null

    init {
        context.withStyledAttributes(attrs, R.styleable.PlaybackButtonView) {
            playIcon = getDrawable(R.styleable.PlaybackButtonView_play)
            pauseIcon = getDrawable(R.styleable.PlaybackButtonView_pause)
        }
    }

    fun updateIcon(playing: Boolean) {
        isPlaying = playing
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawableRect.set(0f, 0f, w.toFloat(), h.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val drawable = if (isPlaying) pauseIcon else playIcon
        drawable?.let {
            drawableRectInt.set(
                drawableRect.left.toInt(),
                drawableRect.top.toInt(),
                drawableRect.right.toInt(),
                drawableRect.bottom.toInt()
            )
            it.bounds = drawableRectInt
            it.draw(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> return true
            MotionEvent.ACTION_UP -> {
                isPlaying = !isPlaying
                invalidate()
                onPlayPauseToggle?.invoke()
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}
