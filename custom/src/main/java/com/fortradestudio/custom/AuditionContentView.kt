package com.fortradestudio.custom

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.children


class AuditionContentView @JvmOverloads constructor(
    context: Context,
    val attrs: AttributeSet? = null,
    val defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    companion object {
        private const val TAG = "AuditionContentView"
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val requestedWidth = MeasureSpec.getSize(widthMeasureSpec)
        val requestedWidthMode = MeasureSpec.getMode(widthMeasureSpec)

        val requestedHeight = MeasureSpec.getSize(heightMeasureSpec)
        val requestedHeightMode = MeasureSpec.getMode(heightMeasureSpec)

        val width = requestedWidth
        val height = requestedHeight

        //set measured dimension
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val videoView = children.first()
        videoView.layout(0, 0, width, height)

        val left = width - width / 6.5;
        val top = height / 1.5
        val bottom = height - height / 4.5
        val mid = left + (width - left) / 14

        val shareTop = height / 1.5
        val shareBottom = height - height / 3.9

        val likeBtn = children.elementAt(1)
        likeBtn.layout(
            left.toInt(),
            shareBottom.toInt() + height / 70,
            width - width / 58,
            (shareBottom + shareBottom - shareTop).toInt() + height / 70
        )

        val likesView = children.elementAt(2)
        likesView.layout(
            mid.toInt(),
            (shareBottom + shareBottom - shareTop).toInt() + height / 160,
            width,
            (shareBottom + shareBottom - shareTop).toInt() + height / 20
        )

        val muteButton = children.elementAt(3)
        muteButton.layout(
            width / 2 - width / 16,
            height / 2 - height / 26,
            width / 2 + width / 16,
            height / 2 + height / 26
        )

        val shareButton = children.elementAt(4)
        shareButton.layout(left.toInt(), shareTop.toInt(), width - width / 58, shareBottom.toInt())

        val circleView = children.elementAt(5)
        circleView.layout(width / 16, height - height / 4, width / 6.toInt(), height - height / 8)

        val usernameTextView = children.elementAt(6)
        usernameTextView.layout(
            width / 5.9.toInt(),
            (height - height / 4.8).toInt(),
            width / 2,
            height - height / 6
        )
    }

    // state 0 means not liked and 1 means liked
    var state: Int = 0

    fun setChildText(count: String, liking: Boolean = false, idFill: Int = 0, idUnfill: Int = 0) {
        val counterView = children.elementAt(2) as TextView
        val counts = count.convertToCounts()
        counterView.text = counts

        if (liking && state == 0) {
            val likeButton = children.elementAt(1)
            likeButton.animate()
                .scaleY(1.2f)
                .scaleX(1.2f)
                .setDuration(200L)
                .withEndAction {
                    likeButton.setBackgroundResource(idFill)
                    likeButton.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(200L)
                        .start()

                    this.state = 1
                }
                .start()
        } else if (liking && state == 1) {
            val likeButton = children.elementAt(1)
            likeButton.animate()
                .scaleY(1.2f)
                .scaleX(1.2f)
                .setDuration(200L)
                .withEndAction {
                    likeButton.setBackgroundResource(idUnfill)
                    likeButton.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(200L)
                        .start()

                    this.state = 0
                }
                .start()
        }
    }

    private fun String.convertToCounts(): String {
        Log.i(TAG, "convertToCounts: $this")
        val value = toDouble()
        if (value < 1000) {
            return if (value.toString().length == 3) {
                "   " + value.toString()
            } else if (value.toString().length == 4) {
                "  " + value.toString()
            } else {
                "  " + value.toString()
            }
        } else if (value >= 1000 && value < 100000) {
            // b/w thousand and 0.1 million
            val div = value.div(1000)
            val format = "%.2f".format(div)
            return "$format k"
        } else if (value >= 1_00_000 && value < 1_00_00_000) {
            val div = value.div(1_00_000)
            val format = "%.2f".format(div)
            return if (format.length == 4) {
                " $format L"
            } else {
                "$format L"
            }
        } else {
            val div = value.div(1_00_00_000)
            val format = "%.2f".format(div)
            return "$format C"
        }

    }

    fun like(resId:Int){
        val muteButton = children.elementAt(3)
        muteButton.setBackgroundResource(resId)

        if (muteButton.visibility == View.GONE) {
            muteButton.visibility = View.VISIBLE
            muteButton.animate()
                .alpha(0f)
                .setDuration(800L)
                .withEndAction {
                    muteButton.alpha = 1f
                    muteButton.visibility = View.GONE
                }
                .start()
        }
    }

    fun mute(unmute: Boolean = false) {
        val muteButton = children.elementAt(3)
        if (unmute) {
            muteButton.setBackgroundResource(R.drawable.ic_no_sound)
        } else {
            muteButton.setBackgroundResource(R.drawable.ic_volume_up)
        }
        if (muteButton.visibility == View.GONE) {
            muteButton.visibility = View.VISIBLE
            muteButton.animate()
                .alpha(0f)
                .setDuration(800L)
                .withEndAction {
                    muteButton.alpha = 1f
                    muteButton.visibility = View.GONE
                }
                .start()
        }
    }



}