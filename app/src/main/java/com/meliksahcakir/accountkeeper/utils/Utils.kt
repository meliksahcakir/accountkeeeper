package com.meliksahcakir.accountkeeper.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.EditText
import android.widget.Toast
import com.meliksahcakir.accountkeeper.R
import kotlin.math.hypot

fun isEmailValid(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isPasswordValid(password: String): Boolean {
    return password.length > 5
}

fun isRepeatPasswordValid(password: String, repeatPassword: String): Boolean {
    return password == repeatPassword
}

fun dpToPx(dp: Int): Int {
    return (dp * Resources.getSystem().displayMetrics.density).toInt()
}

val DP_150_IN_PX = dpToPx(150)

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

fun Context.copyToClipboard(text: CharSequence) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("label", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(this, getString(R.string.copy_to_clipboard), Toast.LENGTH_SHORT).show()
}

fun Context.share(text: CharSequence) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    startActivity(shareIntent)
}

fun View.startCircularReveal() {
    addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
        override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int,
                                    oldRight: Int, oldBottom: Int) {
            v.removeOnLayoutChangeListener(this)
            val cx = (v.left + v.right) / 2
            val cy = (v.top + v.bottom) / 2
            val r = hypot(right.toDouble(), bottom.toDouble()).toInt()
            ViewAnimationUtils.createCircularReveal(v, cx, cy, 0f, r.toFloat()).apply {
                interpolator = DecelerateInterpolator(2f)
                duration = 1000
                start()
            }
        }
    })
}

fun View.exitCircularReveal(exitX: Int, exitY: Int, block: () -> Unit) {
    val startRadius = hypot(this.width.toDouble(), this.height.toDouble())
    ViewAnimationUtils.createCircularReveal(this, exitX, exitY, startRadius.toFloat(), 0f).apply {
        duration = 350
        interpolator = DecelerateInterpolator(1f)
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                block()
                super.onAnimationEnd(animation)
            }
        })
        start()
    }
}

fun View.findLocationOfCenterOnTheScreen(): (IntArray) {
    val positions = intArrayOf(0, 0)
    getLocationInWindow(positions)
    // Get the center of the view
    positions[0] = positions[0] + width / 2
    positions[1] = positions[1] + height / 2
    return positions
}