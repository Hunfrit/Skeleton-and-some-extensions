package homecare.android.com.utils.permissions

import android.app.Activity
import android.view.View

/**
 * Class designed in order to facilitate requesting permissions in app
 * NOTE: Because app may be restarted during the permission request, the request must be done during an initialization phase.
 * This may be [Activity.onCreate] or [View.onFinishInflate] methods, but not pausing methods like [Activity.onResume],
 * because you'll potentially create an infinite request loop, as your requesting activity is paused by the framework during the permission request.
 */
class PermissionUtil(activity: Activity) {

    private val permissionFragment: PermissionFragment

    init {
        permissionFragment = getPermissionFragment(activity)
    }

    companion object {
        private const val TAG = "PermissionUtil"
    }

    private fun getPermissionFragment(activity: Activity): PermissionFragment {
        var fragment: PermissionFragment? = findPermissionFragment(activity)
        if (fragment == null) {
            fragment = PermissionFragment()
            val manager = activity.fragmentManager
            manager.beginTransaction().add(fragment,
                TAG
            ).commitAllowingStateLoss()
            manager.executePendingTransactions()
        }
        return fragment
    }

    private fun findPermissionFragment(activity: Activity): PermissionFragment? {
        return activity.fragmentManager.findFragmentByTag(TAG) as? PermissionFragment
    }

    /**
     * Call this to start requesting permissions your app needed
     *
     * @param permissions which must be granted
     */
    fun request(vararg permissions: String) {
        if (permissions.isNotEmpty()) {
            permissionFragment.requestPermissions(permissions)
        } else {
            permissionFragment.allGranted()
        }
    }

    /**
     * Set listener to know when all permissions are granted
     */
    fun setListener(listener: OnPermissionResultListener) {
        permissionFragment.setPermissionResultListener(listener)
    }

}
