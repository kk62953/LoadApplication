package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    //Variables for custom button attributes
    private var downbackgroundColor=0
    private var loadingbackgroundColor=0
    private var textColor=0

    //Variable for animations
    private var animationProgress = 0f

    //Create Paint object
    private val paint= Paint(Paint.ANTI_ALIAS_FLAG).apply{
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize=55.0f
    }



     var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        if(buttonState==ButtonState.Loading){
            val valueAnimator = ValueAnimator.ofFloat(0f,widthSize.toFloat())
            valueAnimator.duration=3000
            valueAnimator.addUpdateListener{
                animationProgress = valueAnimator.animatedValue as Float
                invalidate()
                if(animationProgress == widthSize.toFloat()){

                    buttonState =ButtonState.Completed
                }
            }
            valueAnimator.start()
        }
    }


    init {
        //Inititalize styled attributes to custom buttom
        context.withStyledAttributes(attrs,R.styleable.LoadingButton){
            downbackgroundColor=getColor(R.styleable.LoadingButton_downloadColor,0)
            loadingbackgroundColor=getColor(R.styleable.LoadingButton_loadingColor,0)
            textColor=getColor(R.styleable.LoadingButton_textColor,0)
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawCommonButtonFeatures(canvas)
    }

    private fun drawCommonButtonFeatures(canvas: Canvas?) {
        paint.color =downbackgroundColor
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        when (buttonState) {
            ButtonState.Loading ->drawLoadingAnimation(canvas)
            ButtonState.Completed ->drawDefaultAnimation(canvas)
        }
    }

    private fun drawLoadingAnimation(canvas: Canvas?){
        paint.color =loadingbackgroundColor
        canvas?.drawRect(0f, 0f,animationProgress, heightSize.toFloat(), paint)

        //draw Text when loading
        paint.color=textColor
        val label=resources.getString(R.string.button_loading)
        canvas?.drawText(label,(widthSize/2).toFloat(),(heightSize/2).toFloat(),paint)

        //draw arc inside load button when loading
        val x = width *0.75f
        val y = height /4f
        val oval = RectF(x,y,x+height/2f,height-y)

        paint.color=textColor
        canvas?.drawArc(oval,0f,animationProgress/widthSize *360,true,paint)

    }

    private fun drawDefaultAnimation(canvas: Canvas?){
        paint.color =textColor
        val label=resources.getString(R.string.message_download)
        canvas?.drawText(label,(widthSize/2).toFloat(),(heightSize/2).toFloat(),paint)
    }



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}