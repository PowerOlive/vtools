package com.omarea.common.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.UiModeManager
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.omarea.common.R

class DialogHelper {
    class DialogButton(public val text: String, public val onClick: Runnable? = null, public val dismiss: Boolean = true) {
    }

    class DialogWrap(private val d: AlertDialog) {
        public val context = dialog.context

        public fun setCancelable(cancelable: Boolean) {
            d.setCancelable(cancelable)
        }

        public fun setOnDismissListener(onDismissListener: DialogInterface.OnDismissListener): DialogWrap {
            d.setOnDismissListener(onDismissListener)

            return this
        }

        public val dialog: AlertDialog
            get() {
                return d
            }

        public fun dismiss() {
            try {
                d.dismiss()
            } catch (ex: Exception) {
            }
        }

        public fun hide() {
            try {
                d.hide()
            } catch (ex: Exception) {
            }
        }

        public val isShowing: Boolean
            get() {
                return d.isShowing()
            }
    }

    companion object {
        // 是否禁用模糊背景
        public var disableBlurBg = false

        fun animDialog(dialog: AlertDialog?): DialogWrap? {
            if (dialog != null && !dialog.isShowing) {
                dialog.window?.run {
                    setWindowAnimations(R.style.windowAnim)
                }
                dialog.show()
            }
            return if (dialog != null) DialogWrap(dialog) else null
        }

        fun animDialog(builder: AlertDialog.Builder): DialogWrap {
            val dialog = builder.create()
            animDialog(dialog)
            return DialogWrap(dialog)
        }

        fun helpInfo(context: Context, message: String, onDismiss: Runnable? = null): DialogWrap {
            return helpInfo(context, "", message, onDismiss)
        }

        fun helpInfo(context: Context, title: String, message: String, onDismiss: Runnable? = null): DialogWrap {
            val layoutInflater = LayoutInflater.from(context)
            val dialog = layoutInflater.inflate(R.layout.dialog_help_info, null)
            val alert = AlertDialog.Builder(context).setView(dialog)
            alert.setCancelable(true)

            (dialog.findViewById(R.id.dialog_help_title) as TextView).run {
                if (title.isNotEmpty()) {
                    text = title
                    visibility = View.VISIBLE
                } else {
                    visibility = View.GONE
                }
            }

            (dialog.findViewById(R.id.dialog_help_info) as TextView).run {
                if (message.isNotEmpty()) {
                    text = message
                    visibility = View.VISIBLE
                } else {
                    visibility = View.GONE
                }
            }
            if (onDismiss != null) {
                alert.setPositiveButton(R.string.btn_confirm) { d, _ ->
                    d.dismiss()
                }
                alert.setCancelable(false)
            }
            alert.setOnDismissListener {
                onDismiss?.run()
            }

            return animDialog(alert)
        }

        fun confirm(context: Context,
                    title: String = "",
                    message: String = "",
                    onConfirm: Runnable? = null,
                    onCancel: Runnable? = null): DialogWrap {
            return openContinueAlert(context, R.layout.dialog_confirm, title, message, onConfirm, onCancel)
        }

        fun warning(context: Context,
                    title: String = "",
                    message: String = "",
                    onConfirm: Runnable? = null,
                    onCancel: Runnable? = null): DialogWrap {
            return openContinueAlert(context, R.layout.dialog_warning, title, message, onConfirm, onCancel)
        }

        private fun getCustomDialogView(context: Context,
                                        layout: Int,
                                        title: String = "",
                                        message: String = "",
                                        contentView: View? = null): View {

            val view = LayoutInflater.from(context).inflate(layout, null)
            view.findViewById<TextView?>(R.id.confirm_title)?.run {
                if (title.isEmpty()) {
                    visibility = View.GONE
                } else {
                    setText(title)
                }
            }

            view.findViewById<TextView?>(R.id.confirm_message)?.run {
                if (message.isEmpty()) {
                    visibility = View.GONE
                } else {
                    setText(message)
                }
            }

            if (contentView != null) {
                view.findViewById<FrameLayout?>(R.id.confirm_custom_view)?.addView(contentView)
            }

            return view
        }

        fun confirm(context: Context,
                    title: String = "",
                    message: String = "",
                    contentView: View? = null,
                    onConfirm: Runnable? = null,
                    onCancel: Runnable? = null): DialogWrap {
            val view = getCustomDialogView(context, R.layout.dialog_confirm, title, message, contentView)

            val dialog = customDialog(context, view)
            view.findViewById<View>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
                onCancel?.run()
            }
            view.findViewById<View>(R.id.btn_confirm).setOnClickListener {
                dialog.dismiss()
                onConfirm?.run()
            }

            return dialog
        }

        fun confirm(context: Context,
                    title: String = "",
                    message: String = "",
                    onConfirm: DialogButton? = null,
                    onCancel: DialogButton? = null): DialogWrap {
            return confirm(context, title, message, null, onConfirm, onCancel)
        }

        fun confirm(context: Context,
                    title: String = "",
                    message: String = "",
                    contentView: View? = null,
                    onConfirm: DialogButton? = null,
                    onCancel: DialogButton? = null): DialogWrap {
            val view = getCustomDialogView(context, R.layout.dialog_confirm, title, message, contentView)

            val dialog = customDialog(context, view)

            val btnConfirm = view.findViewById<TextView?>(R.id.btn_confirm)
            if (onConfirm != null) {
                btnConfirm?.text = onConfirm.text
            }
            btnConfirm?.setOnClickListener {
                if (onConfirm != null) {
                    if (onConfirm.dismiss) {
                        dialog.dismiss()
                    }
                    onConfirm.onClick?.run()
                } else {
                    dialog.dismiss()
                }
            }


            val btnCancel = view.findViewById<TextView?>(R.id.btn_cancel)
            if (onCancel != null) {
                btnCancel?.text = onCancel.text
            }
            btnCancel.setOnClickListener {
                if (onCancel != null) {
                    if (onCancel.dismiss) {
                        dialog.dismiss()
                    }
                    onCancel.onClick?.run()
                } else {
                    dialog.dismiss()
                }
            }

            return dialog
        }

        fun confirm(context: Context, contentView: View? = null, onConfirm: DialogButton? = null, onCancel: DialogButton? = null): DialogWrap {
            return this.confirm(context, "", "", contentView, onConfirm, onCancel)
        }

        private fun getWindowBackground(context: Context, defaultColor:Int = Color.TRANSPARENT): Int {
            // val attrsArray = intArrayOf(android.R.attr.windowBackground)
            val attrsArray = intArrayOf(android.R.attr.background)
            val typedArray = context.obtainStyledAttributes(attrsArray)
            val color = typedArray.getColor(0, defaultColor)
            typedArray.recycle()
            return color
        }

        private fun getStatusBarColor(context: Context): Int {
            val defaultColor = Color.WHITE
            val attrsArray = intArrayOf(android.R.attr.statusBarColor)
            val typedArray = context.obtainStyledAttributes(attrsArray)
            val color = typedArray.getColor(0, defaultColor)
            typedArray.recycle()
            return color
        }

        private fun openContinueAlert(context: Context,
                                      layout: Int,
                                      title: String = "",
                                      message: String = "",
                                      onConfirm: Runnable? = null,
                                      onCancel: Runnable? = null): DialogWrap {
            val view = getCustomDialogView(context, layout, title, message, null)

            val dialog = customDialog(context, view)
            view.findViewById<View?>(R.id.btn_cancel)?.setOnClickListener {
                dialog.dismiss()
                onCancel?.run()
            }
            view.findViewById<View?>(R.id.btn_confirm)?.setOnClickListener {
                dialog.dismiss()
                onConfirm?.run()
            }

            return dialog
        }

        fun confirmBlur(context: Activity,
                        title: String = "",
                        message: String = "",
                        onConfirm: Runnable? = null,
                        onCancel: Runnable? = null): DialogWrap {
            return openContinueAlert(context, R.layout.dialog_confirm, title, message, onConfirm, onCancel)
        }

        fun alert(context: Context,
                  title: String = "",
                  message: String = "",
                  onConfirm: Runnable? = null): DialogWrap {
            return openContinueAlert(context, R.layout.dialog_alert, title, message, onConfirm, null)
        }

        fun customDialog(context: Context, view: View, cancelable: Boolean = true): DialogWrap {
            val useBlur = context is Activity

            val dialog = (if (useBlur) {
                AlertDialog.Builder(context, R.style.custom_alert_dialog)
            } else {
                AlertDialog.Builder(context)
            }).setView(view).setCancelable(cancelable).create()

            if (context is Activity) {
                dialog.show()
                dialog.window?.run {
                    setWindowBlurBg(this, context)
                    decorView.run {
                        systemUiVisibility = context.window.decorView.systemUiVisibility // View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    }

                    /*
                    // 隐藏状态栏和导航栏
                    decorView.run {
                        systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        setOnSystemUiVisibilityChangeListener {
                            var uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or  //布局位于状态栏下方
                                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or  //全屏
                                    View.SYSTEM_UI_FLAG_FULLSCREEN or  //隐藏导航栏
                                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            uiOptions = uiOptions or 0x00001000
                            systemUiVisibility = uiOptions
                        }
                    }
                    */

                    // setWindowAnimations(R.style.windowAnim2)
                }
            } else {
                dialog.window?.run {
                    setWindowAnimations(R.style.windowAnim2)
                }
                dialog.show()
                dialog.window?.run {
                    setBackgroundDrawableResource(android.R.color.transparent)
                }
            }

            return DialogWrap(dialog)
        }

        fun helpInfo(context: Context, title: Int, message: Int): DialogWrap {
            val dialog =
                    AlertDialog.Builder(context)
                            .setTitle(title)
                            .setMessage(message)
                            .setPositiveButton(R.string.btn_confirm) { _, _ ->
                            }
            return animDialog(dialog)
        }

        private fun isNightMode(context: Context): Boolean {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                return true
            } else if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
                val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                return uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES
            } else {
                return false
            }
        }

        fun setWindowBlurBg(window: Window, activity: Activity) {
            // 是否使用了动态壁纸
            val wallpaperMode = activity.window.attributes.flags and WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER != 0

            window.run {
                // TODO:处理模糊背景
                // BlurBackground(activity).setScreenBgLight(dialog)

                // val attrs = attributes
                // attrs.alpha = 0.1f
                // attributes =attrs
                // decorView.setPadding(0, 0, 0, 0)

                val blurBitmap = if (disableBlurBg || wallpaperMode) {
                    null
                } else {
                    FastBlurUtility.getBlurBackgroundDrawer(activity)
                }

                // window.setDimAmount(0f)
                if (blurBitmap != null) {
                    setBackgroundDrawable(BitmapDrawable(activity.resources, blurBitmap))
                } else {
                    // setBackgroundDrawableResource(android.R.color.transparent)
                    try {
                        val bg = getWindowBackground(activity)
                        if (bg == Color.TRANSPARENT) {
                            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                            if (wallpaperMode || isNightMode(context)) {
                                val d = ColorDrawable(Color.argb(255, 18, 18, 18))
                                setBackgroundDrawable(d)
                            } else {
                                val d = ColorDrawable(Color.argb(255, 245, 245, 245))
                                setBackgroundDrawable(d)
                            }
                        } else {
                            val d = ColorDrawable(bg)
                            setBackgroundDrawable(d)
                        }
                    } catch (ex: java.lang.Exception) {
                        val d = ColorDrawable(Color.argb(255, 245, 245, 245))
                        setBackgroundDrawable(d)
                    }
                }
            }
        }
    }
}
