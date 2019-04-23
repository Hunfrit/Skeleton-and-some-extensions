package homecare.android.com.utils

import android.util.Log
import com.crashlytics.android.Crashlytics
import timber.log.Timber

/**
 * Timber tree which will be work in release build
 */
class TimberCrashReportingTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
            return
        }
        if (t == null) {
            Crashlytics.logException(Exception(message))
        } else {
            Crashlytics.logException(t)
        }
    }

}
