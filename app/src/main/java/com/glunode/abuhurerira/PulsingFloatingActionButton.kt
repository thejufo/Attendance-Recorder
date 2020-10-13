// Created by abdif on 6/13/2020

package com.glunode.abuhurerira

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PulsingFloatingActionButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FloatingActionButton(context, attrs, defStyleAttr) {

    private var pulsing = false

    fun startPulsing(periodMillis: Long) {
        if (!pulsing) {
            pulsing = true
            pulse(periodMillis)
        }
    }

    private fun pulse(periodMillis: Long) {
        if (!pulsing) return

        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.2f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.2f)
        val animator = ObjectAnimator.ofPropertyValuesHolder(scaleX, scaleY)
        animator.duration = 150
        animator.repeatCount = 10
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                pulse(periodMillis)
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
        animator.start()
    }

    fun stopPulsing() {
        pulsing = false
    }
}