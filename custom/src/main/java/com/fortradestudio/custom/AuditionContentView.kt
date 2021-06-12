package com.fortradestudio.custom

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

import android.widget.TextView
import androidx.core.view.children

class AuditionContentView @JvmOverloads constructor(
    context: Context,
    val attrs: AttributeSet?=null,
    val defStyle:Int=0
): ViewGroup(context,attrs,defStyle){


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
        videoView.layout(0,0,width,height)

        val left = width - width/5;
        val top = height/1.34421858
        val bottom = height - height/7
        val mid = left + (width - left)/4.2

        val likeBtn = children.elementAt(1)
        likeBtn.layout(left,top.toInt(),width,bottom)

        val likesView = children.elementAt(2)
        likesView.layout(mid.toInt(),bottom-30,width,height - height/12)

    }

    // state 0 means not liked and 1 means liked
    var state : Int = 0

    fun setChildText(count:String,liking:Boolean=false,idFill:Int=0,idUnfill:Int=0){
        val counterView = children.elementAt(2) as TextView
        val counts = count.convertToCounts()
        counterView.text = counts

        if(liking && state ==0){
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
        }else if (liking && state ==1){
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

    private fun String.convertToCounts():String{
        val value = toDouble()
        if(value<1000){
            return value.toString()
        }
        else if(value >=1000 && value<100000){
            // b/w thousand and 0.1 million
            val div = value.div(1000)
            val format = "%.2f".format(div)
            return "$format k"
        }
        else if (value>=1_00_000 && value<1_00_00_000){
            val div = value.div(1_00_000)
            val format = "%.2f".format(div)
            return "$format L"
        }
        else {
            val div = value.div(1_00_00_000)
            val format = "%.2f".format(div)
            return "$format C"
        }

    }



}