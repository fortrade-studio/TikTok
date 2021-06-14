package com.fortradestudio.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Build
import android.os.Handler
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK
import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.core.graphics.scale
import androidx.core.view.children
import org.w3c.dom.Attr
import java.lang.Exception
import java.net.URL

/**
 * TODO: document your custom view class.
 */
class ProfileImagesViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {


    companion object {
        private const val TAG = "ProfileImagesViewGroup"
        final val handler = Handler()
    }


    private var isInit = false



    override fun onFinishInflate() {
        super.onFinishInflate()
        this.setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

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
        val first = this.children.first()
        if(first is AppCompatButton){
            first.layout(width-width/3, 0, width-width/5, 70)
        }else{
            throw Exception("Button Type For ProfileImagesViewGroup must be AppCompatButton located at index 0 of child tree")
        }

    }


    val paint = Paint().apply {
        this.flags = Paint.ANTI_ALIAS_FLAG
    }
    val p = Paint().apply {
        this.flags = Paint.ANTI_ALIAS_FLAG
        this.color = Color.WHITE
    }
//
//    val bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
//    val canvas = Canvas(bitmap)
//    val rect = Rect(0,0,width,height)
//    val rectF = RectF(rect)
//    canvas.drawRoundRect(0f,0f,width.toFloat(),height.toFloat(),15f,15f,paint)
//    canvas.drawBitmap(bit,rect,rectF,paint)

    val bitmap by lazy {
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    }
    val canvas by lazy {
        Canvas(bitmap)
    }
    val rect by lazy {
        Rect(0, 0, width, height)
    }
    val rectF by lazy {
        RectF(rect)
    }
    val roundedPath = Path()


    val m = Matrix()

    val arc = Paint().apply {
        this.flags = Paint.ANTI_ALIAS_FLAG
        this.color = Color.WHITE
        this.strokeWidth = 13f
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDraw(c: Canvas?) {
        if (bit != null) {
            c?.drawColor(Color.WHITE)
            val scalex= width/(bit.width.toFloat()+bit.width.toFloat()/4)
            val scaley = height/bit.height.toFloat()
            m.setScale(scalex,scaley,0f,0f)
            canvas.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), 15f, 15f, paint)
            canvas.setMatrix(m)
            canvas.drawBitmap(bit, 0f,0f, paint)
            roundedPath.addRoundRect(
                0f,
                0f,
                width.toFloat(),
                height.toFloat(),
                15f,
                15f,
                Path.Direction.CCW
            )
            c?.clipPath(roundedPath)
            c?.drawBitmap(bitmap, rect, rectF, paint)
            if(isDelete){
                c?.drawLine(0f,0f,width.toFloat(),height.toFloat(),arc)
            }
        }
    }

    private var bit: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.placeholder_add)

    private var isDelete = false

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setBitmap(bitmap: Bitmap) {
        this.bit = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        isInit = true
        invalidate()
    }

    fun deleteEffect(){
        isDelete = true
        invalidate()
    }

    fun setBitmapViaUrl(url:String){
        AsyncTask.execute {
            val URL = URL(url)
            val decodeStream = BitmapFactory.decodeStream(URL.openConnection().getInputStream())
            this.bit = decodeStream.copy(Bitmap.Config.ARGB_8888,true)
            isInit = true
            invalidate()
        }
    }

    private val longPressRunnable = Runnable {
        val first = this@ProfileImagesViewGroup.children.first()
        if (first is Button) {
            // we can show or hide it accordingly
            when (first.visibility) {
                View.GONE -> {
                    groove()
                    first.visibility = View.VISIBLE
                }
                else -> {
                    groove(false)
                    first.visibility = View.GONE
                }
            }
        }
    }


    override fun performClick(): Boolean {
        super.performClick()
        return true;
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            if(!isInit){
                performClick()
                return true
            }
            handler.postDelayed(longPressRunnable, ViewConfiguration.getLongPressTimeout().toLong())
        }
        if (event?.action == MotionEvent.ACTION_MOVE || event?.action == MotionEvent.ACTION_UP) {
            // user releases
            handler.removeCallbacks(longPressRunnable)
            Log.i(TAG, "onTouchEvent: action move")
        }
        return true;
    }

    private fun groove(cross: Boolean = true) {
        if (cross) {
            this.animate()
                .setDuration(300L)
                .scaleY(0.9f)
                .scaleX(0.9f)
                .setInterpolator(BounceInterpolator())
                .start()
        } else {
            this.animate()
                .setDuration(300L)
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(BounceInterpolator())
                .start()
        }
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        if(!hasWindowFocus){
            removeCross()
        }
    }

    private fun removeCross(){
        val first = this@ProfileImagesViewGroup.children.first()
        if (first is Button) {
            // we can show or hide it accordingly
            when (first.visibility) {
                View.VISIBLE -> {
                    groove(false)
                    first.visibility = View.GONE
                }
            }
        }
    }

}
