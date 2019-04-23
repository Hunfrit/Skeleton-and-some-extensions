package homecare.android.com.api

import android.text.TextUtils
import com.google.gson.Gson
import homecare.android.com.R
import homecare.android.com.data.responses.ErrorResponse
import homecare.android.com.extensions.getString
import homecare.android.com.skeleton.activity.BaseActivity
import homecare.android.com.skeleton.presentation.BaseView
import homecare.android.com.utils.DefaultCallbackDialogHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.EOFException
import java.io.Reader
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.UnknownHostException

/**
 * Class designed to replace default retrofit2 Callback and reduce overridden methods.
 * This class also provides default error handling and displays error message using {@link AlertDialog}
 */
abstract class DefaultCallback<T>(view: BaseView, private val activity: BaseActivity<*>? = null) : Callback<T> {

    companion object {
        const val HTTP_UNAUTHORIZED = 401
    }

    private val viewRef: WeakReference<BaseView> = WeakReference(view)

    abstract fun onResponse(body: T)

    override fun onResponse(call: Call<T>?, response: Response<T>) {
        if (response.code() == HTTP_UNAUTHORIZED) {
            viewRef.get()?.reboot()
            return
        }
        if (response.isSuccessful) {
            if (response.body() == null) {
                throw NullPointerException("Cannot deserialize response")
            }
            response.body()?.let {
                onResponse(it)
            }
        } else {
            hideLoading()
            showDialog(getErrorMessage(response))
        }
    }

    override fun onFailure(call: Call<T>?, t: Throwable?) {
        hideLoading()
        //display an error to user
        var message: String = ""
        message = when (t) {
            is EOFException -> {
                getString(R.string.all_no_connection_error)
            }
            is UnknownHostException -> {
                getString(R.string.all_no_connection_error)
            }
            else -> t?.message ?: getString(R.string.all_internal_server_error)
        }
        showDialog(message)
        //log output
        try {
            Timber.e(t, "onFailure: %s", call?.request()?.url().toString())
        } catch (e: Exception) {
            Timber.e(t, "onFailure: Cannot get original request")
        }
    }

    /**
     * @return already generated or newly generated message
     */
    private fun getErrorMessage(response: Response<T>): String = when (response.code()) {
        HttpURLConnection.HTTP_INTERNAL_ERROR -> getString(R.string.all_internal_server_error)
        else -> getBadResponseMessage(response)
    }

    /**
     * @return parsed error response or default rest client error message
     */
    private fun getBadResponseMessage(response: Response<T>)
            : String = retrieveMessage(response).apply {
        if (this.isNullOrEmpty()) {
            return getString(R.string.all_internal_server_error)
        } else {
            return@apply
        }
    }

    private fun retrieveMessage(response: Response<T>): String {
        val reader = getErrorReader(response)
                ?: return getString(R.string.all_no_connection_error)
        return getString(R.string.all_no_connection_error)
    }

    /**
     * Parse an error json into [ErrorResponse] model
     */
    private fun castBodyToSingleError(reader: Reader): ErrorResponse? {
        try {
            return Gson().fromJson<ErrorResponse>(reader, ErrorResponse::class.java)
        } catch (e: Exception) {
            Timber.e(e, "Cannot parse from json")
        }
        return null
    }

    private fun getErrorReader(response: Response<T>): Reader? = response.errorBody()?.charStream()

    private fun showDialog(message: String) {
        Timber.d("showFailureDialog -> message[%s]", message)
        if (viewRef.get() == null) {
            Timber.i("showFailureDialog: viewReference == NULL")
            return
        }
        if (TextUtils.isEmpty(message)) {
            Timber.i("showFailureDialog: message is EMPTY")
            return
        }
        try {
            DefaultCallbackDialogHelper.viewRef = viewRef
            DefaultCallbackDialogHelper.showDialog(message, activity)
        } catch (e: Exception) {
            Timber.e(e, "showFailureDialog: cannot clear and create dialog: ${e.message}")
        }
    }

    private fun hideLoading() {
        viewRef.get()?.hideProgressView()
    }

}
