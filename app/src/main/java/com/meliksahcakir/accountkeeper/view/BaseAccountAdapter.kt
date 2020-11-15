package com.meliksahcakir.accountkeeper.view

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.data.Account
import com.meliksahcakir.accountkeeper.utils.DP_150_IN_PX
import com.meliksahcakir.accountkeeper.utils.copyToClipboard
import com.meliksahcakir.accountkeeper.utils.share
import kotlinx.android.synthetic.main.account_view.view.*
import kotlinx.android.synthetic.main.qr_code_dialog.view.*

abstract class BaseAccountAdapter(private val listener: AccountAdapterListener) :
    ListAdapter<Account, BaseAccountViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Account>() {
        override fun areItemsTheSame(oldItem: Account, newItem: Account): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Account, newItem: Account): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseAccountViewHolder {
        return BaseAccountViewHolder(parent)
    }

    override fun onBindViewHolder(holder: BaseAccountViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    fun deleteAccount(position: Int) {
        listener.onDeleteAccount(getItem(position))
    }
}

open class BaseAccountViewHolder(private val parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.account_view, parent, false)
) {

    private val accountImageView: ImageView by lazy { itemView.accountImageView }
    private val accountNameTextView: TextView by lazy { itemView.accountNameTextView }
    private val accountAddressTextView: TextView by lazy { itemView.accountAddressTextView }
    private val accountAddressImageView: ImageView by lazy { itemView.accountAddressImageView }
    protected val editImageView: ImageView by lazy { itemView.editImageView }
    private val accountLayout: ConstraintLayout by lazy { itemView.accountLayout }
    private val accountDescriptionTextView: TextView by lazy { itemView.accountDescriptionTextView }
    private val descriptionGroup: Group by lazy { itemView.descriptionGroup }
    private val qrImageView: ImageView by lazy { itemView.qrImageView }
    private val privacyImageView: ImageView by lazy { itemView.privacyImageView }
    private val copyAddressButton: MaterialButton by lazy { itemView.copyAddressButton }
    protected val shareImageView: ImageView by lazy { itemView.shareImageView }

    private val writer = QRCodeWriter()

    protected fun changeBackground(@DrawableRes resourceId: Int) {
        accountLayout.setBackgroundResource(resourceId)
    }

    open fun bind(account: Account, listener: AccountAdapterListener) {
        val resId = when (account.accountType) {
            Account.BANK_ACCOUNT -> R.drawable.ic_card
            Account.CRYPTO -> R.drawable.ic_bitcoin
            Account.EMAIL -> R.drawable.ic_email
            Account.PHONE -> R.drawable.ic_phone
            Account.LOCATION -> R.drawable.ic_location
            else -> R.drawable.ic_help
        }
        accountAddressImageView.setImageResource(resId)
        accountNameTextView.text = account.accountName
        accountAddressTextView.text = account.accountNumber
        accountDescriptionTextView.text = account.accountDescription
        descriptionGroup.isVisible = account.accountDescription != ""
        privacyImageView.setImageResource(if (account.global) R.drawable.ic_public else R.drawable.ic_private)
        copyAddressButton.setOnClickListener {
            val address = accountAddressTextView.text.toString()
            parent.context.copyToClipboard(address)
            parent.context.share(address)
        }
        qrImageView.setOnClickListener {
            showQrCodeDialog(accountAddressTextView.text.toString())
        }
        editImageView.setOnClickListener {
            listener.onEditButtonClicked(account)
        }
        shareImageView.setOnClickListener {
            listener.onShareAccount(account)
        }
    }

    private fun showQrCodeDialog(text: String) {
        val matrix: BitMatrix
        val builder = AlertDialog.Builder(parent.context)
        val customLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.qr_code_dialog, null)
        val iv = customLayout.dialogQrImageView
        try {
            matrix = writer.encode(text, BarcodeFormat.QR_CODE, DP_150_IN_PX, DP_150_IN_PX, null)
            val w = matrix.width
            val h = matrix.height
            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            for (x in 0 until w) {
                for (y in 0 until h) {
                    bitmap.setPixel(x, y, if (matrix.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }
            iv.setImageBitmap(bitmap)
        } catch (e: Exception) {
            return
        }
        with(builder) {
            setView(customLayout)
            create()
            show()
        }
    }
}

interface AccountAdapterListener {
    fun onEditButtonClicked(account: Account)
    fun onShareAccount(account: Account)
    fun onDeleteAccount(account: Account)
    fun onSaveAccount(account: Account)
}