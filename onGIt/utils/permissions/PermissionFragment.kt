package homecare.android.com.utils.permissions

import android.annotation.TargetApi
import android.app.Fragment
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import java.util.*

/**
 * Class needed by framework to handle [Fragment.requestPermissions] and
 * [Fragment.onRequestPermissionsResult] methods
 * which cannot be called in framework class
 */
class PermissionFragment : Fragment() {

    companion object {
        const val RC_PERMISSION = 111
    }

    private var deniedPermissions: MutableList<String> = ArrayList()
    private var listener: OnPermissionResultListener? = null

    private val isMarshMellow: Boolean
        get() = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    @TargetApi(Build.VERSION_CODES.M)
    internal fun requestPermissions(permissions: Array<out String>) {
        if (!isMarshMellow) {
            if (listener == null)
                throw NullPointerException("You should set listener before requesting permissions")
            listener!!.granted()
            return
        }
        deniedPermissions.clear()
        requestPermissions(permissions,
            RC_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RC_PERMISSION) {
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permissions[i])
                }
            }
            if (!deniedPermissions.isEmpty()) {
                requestPermissions(deniedPermissions.toTypedArray())
            } else {
                if (listener == null)
                    throw NullPointerException("You should set listener before requesting permissions")
                listener!!.granted()
            }
        }
    }

    fun setPermissionResultListener(permissionResultListener: OnPermissionResultListener) {
        listener = permissionResultListener
    }

    fun allGranted() = listener?.granted()

}
