package com.meliksahcakir.accountkeeper.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.view.isInvisible
import com.google.android.material.card.MaterialCardView
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.utils.drawable
import kotlinx.android.synthetic.main.user_view.view.*

class UserCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    var title: String = ""
        set(value) {
            field = value
            ucvTitleTextView.text = value
        }

    var subTitle: String = ""
        set(value) {
            field = value
            ucvSubTitleTextView.text = value
        }

    var drawableStart: Drawable? = null
        set(value) {
            field = value
            ucvStartImageView.setImageDrawable(value)
        }

    var drawableEnd: Drawable? = null
        set(value) {
            field = value
            ucvEndImageView.setImageDrawable(value)
        }

    var isStartDrawableVisible = false
        set(value) {
            field = value
            ucvStartImageView.isInvisible = !field
        }

    var isEndDrawableVisible = false
        set(value) {
            field = value
            ucvEndImageView.isInvisible = !field
        }

    var isTitleVisible = false
        set(value) {
            field = value
            ucvTitleTextView.isInvisible = !field
        }

    var isSubTitleVisible = false
        set(value) {
            field = value
            ucvSubTitleTextView.isInvisible = !field
        }

    init {
        View.inflate(context, R.layout.user_view, this)
        isClickable = true
        isFocusable = true
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true)
        this.foreground = context.drawable(typedValue.resourceId)

        val attributes = context.obtainStyledAttributes(
            attrs, R.styleable.UserCardView, defStyleAttr, 0
        )
        title = attributes.getString(R.styleable.UserCardView_ucvTitle) ?: ""
        subTitle = attributes.getString(R.styleable.UserCardView_ucvSubTitle) ?: ""
        drawableStart = attributes.getDrawable(R.styleable.SettingsCardView_scvDrawableStart)
            ?: context.drawable(R.drawable.ic_account_keeper)
        drawableEnd = attributes.getDrawable(R.styleable.SettingsCardView_scvDrawableEnd)
        attributes.recycle()
    }
}