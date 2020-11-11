package com.meliksahcakir.accountkeeper.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.android.material.card.MaterialCardView
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.utils.drawable
import kotlinx.android.synthetic.main.settings_card_view.view.*

/**
 * TODO: document your custom view class.
 */
class SettingsCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    var text: String = ""
        set(value) {
            field = value
            scvTextView.text = value
        }

    var drawableStart: Drawable? = null
        set(value) {
            field = value
            scvStartImageView.setImageDrawable(value)
        }

    var drawableEnd: Drawable? = null
        set(value) {
            field = value
            scvEndImageView.setImageDrawable(value)
        }

    var busy = false
        set(value) {
            field = value
            progressBar.isVisible = value
            scvEndImageView.isInvisible = value
        }

    init {
        View.inflate(context, R.layout.settings_card_view, this)
        isClickable = true
        isFocusable = true
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true)
        this.foreground = context.drawable(typedValue.resourceId)

        val attributes = context.obtainStyledAttributes(
            attrs, R.styleable.SettingsCardView, defStyleAttr, 0
        )
        text = attributes.getString(R.styleable.SettingsCardView_scvText) ?: ""
        drawableStart = attributes.getDrawable(R.styleable.SettingsCardView_scvDrawableStart)
        drawableEnd = attributes.getDrawable(R.styleable.SettingsCardView_scvDrawableEnd)
        attributes.recycle()
    }
}