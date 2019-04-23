package homecare.android.com.utils

import homecare.android.com.skeleton.activity.BaseActivity
import homecare.android.com.skeleton.presentation.BaseView
import timber.log.Timber
import java.lang.ref.WeakReference

object DefaultCallbackDialogHelper {

    lateinit var viewRef: WeakReference<BaseView>

    private var dialog: InstantDialog? = null

    fun showDialog(message: String, activity: BaseActivity<*>? = null) {
        try {
            dialog?.dismiss()
        } catch (e: IllegalArgumentException) {
            Timber.e("WTF, ${e.message}")
        }
        dialog = InstantDialog(
            viewRef.get()!!.getContext(),
            activity = activity
        )
        dialog?.show(message)
    }
}