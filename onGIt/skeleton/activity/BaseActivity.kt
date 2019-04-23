package homecare.android.com.skeleton.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import homecare.android.com.R
import homecare.android.com.extensions.consume
import homecare.android.com.extensions.gone
import homecare.android.com.extensions.inflate
import homecare.android.com.extensions.visible
import homecare.android.com.preferences.AppPreferences
import homecare.android.com.screens.MainActivity
import homecare.android.com.skeleton.fragment.BaseFragment
import homecare.android.com.skeleton.presentation.BasePresenter
import homecare.android.com.skeleton.presentation.BaseView
import homecare.android.com.utils.KeyboardController
import org.jetbrains.anko.startActivity

/**
 * Class designed as parent for all Activities created in project.
 *
 * @param <P> presenter for created activity. The instance should be passed in [createPresenter()] method
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class BaseActivity<P : BasePresenter<*>> : AppCompatActivity(),
    BaseView, KeyboardController {

    protected lateinit var presenter: P

    /**
     * Place your layout resource as return parameter
     *
     * @return resourceId of layout which designed for current fragment
     */
    @LayoutRes
    protected abstract fun getLayoutId(): Int

    /**
     * You should create an instance of your presenter here. After that you can use [presenter] everywhere in created activity
     *
     * @return an instance of presenter for created activity
     */
    protected abstract fun createPresenter(): P

    /**
     * Initialize all views here.
     * This method do the same as [android.app.Activity.onCreate]
     */
    protected abstract fun initViews()

    private lateinit var progressView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        progressView = generateProgressView()
        presenter = createPresenter()
        initViews()
        addContentView(progressView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        // Respond to the action bar's Up/Home button
        android.R.id.home -> consume { closeKeyboard() }
        else -> super.onOptionsItemSelected(item)
    }

    override fun showProgressView() {
        runOnUiThread {
            progressView.visible()
        }
    }

    override fun hideProgressView() {
        runOnUiThread {
            progressView.gone()
        }
    }

    override fun closeKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    override fun getContext(): Context = this

    override fun reboot() {
        AppPreferences.clear()
        startAffinity<MainActivity>()
    }

    /**
     * Start next and finishes current activity
     */
    protected inline fun <reified T : Activity> startAffinity(vararg params: Pair<String, Any?>) {
        startActivity<T>(*params)
        finishAffinity()
    }

    /**
     * Replaces existing fragment with allowing state loss
     *
     * @param containerId view where fragment will be replaced
     * @param fragment new fragment
     */
    protected fun replaceFragment(@IdRes containerId: Int, fragment: BaseFragment<*, *>) {
        supportFragmentManager.beginTransaction().replace(containerId, fragment, fragment.TAG).commitAllowingStateLoss()
    }

    /**
     * Adds fragment into backstack above existing fragment allowing state loss
     *
     * @param containerId view where fragment will be added
     * @param fragment new fragment
     */
    protected fun addFragment(@IdRes containerId: Int, fragment: BaseFragment<*, *>) {
        supportFragmentManager.beginTransaction().add(containerId, fragment, fragment.TAG).addToBackStack(fragment.TAG).commitAllowingStateLoss()
    }

    /**
     * Creates progress view. You can implement your own progress view. <br></br>
     * Just replace first parameter of inflate() method on your own implementation
     *
     * @return generated progress view
     */
    private fun generateProgressView(): View = inflate(R.layout.widget_progress_view)

}
