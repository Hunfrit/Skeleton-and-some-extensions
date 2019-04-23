package homecare.android.com.skeleton.fragment

import homecare.android.com.skeleton.activity.BaseActivity
import android.content.Context
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import homecare.android.com.skeleton.presentation.BasePresenter
import homecare.android.com.skeleton.presentation.BaseView

/**
 * Class designed as parent for all Fragments created in project.
 *
 * @param <A> parent activity for created fragment. Info: [activity]
 * @param <P> presenter for created fragment. The instance should be passed in [createPresenter()] method
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class BaseFragment<A : BaseActivity<*>, P : BasePresenter<*>>
    : Fragment(), BaseView {

    protected lateinit var presenter: P

    /**
     * Represent fragment tag
     */
    @Suppress("PropertyName")
    abstract val TAG: String

    /**
     * You this instead of [android.app.Fragment.getActivity]
     */
    protected lateinit var activity: A

    private var rootView: View? = null

    /**
     * Place your layout resource as return parameter
     *
     * @return resourceId of layout which designed for current fragment
     */
    @LayoutRes
    protected abstract fun getLayoutId(): Int

    /**
     * You should create an instance of your presenter here. After that you can use [presenter] everywhere in created fragment
     *
     * @return an instance of presenter for created fragment
     */
    protected abstract fun createPresenter(): P

    /**
     * Initialize all views here.
     * This method do the same as [android.app.Fragment.onCreateView]
     *
     * @param rootView already inflated view
     */
    protected abstract fun initViews(rootView: View?)

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        @Suppress("UNCHECKED_CAST")
        activity = getActivity() as A
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        presenter = createPresenter()
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        //Code below used to improve fragment custom animations
        var animation = super.onCreateAnimation(transit, enter, nextAnim)
        if (animation == null && nextAnim != 0) {
            animation = AnimationUtils.loadAnimation(activity, nextAnim)
        }
        if (animation != null) {
            view?.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) = Unit

                override fun onAnimationStart(animation: Animation?) = Unit

                override fun onAnimationEnd(animation: Animation?) {
                    view?.setLayerType(View.LAYER_TYPE_NONE, null)
                }
            })
        }
        return animation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = initViews(view)

    override fun getContext(): Context = activity

    override fun reboot() = activity.reboot()

    /**
     * Replaces existing fragment with allowing state loss
     *
     * @param containerId view where fragment will be replaced
     * @param fragment new fragment
     */
    protected fun replaceFragment(@IdRes containerId: Int, fragment: BaseFragment<*, *>) {
        childFragmentManager.beginTransaction().replace(containerId, fragment, fragment.TAG).commitAllowingStateLoss()
    }

    /**
     * Replace fragment with current one and adding it into backstack allowing state loss
     *
     * @param containerId view where fragment will be added
     * @param fragment new fragment
     */
    protected fun replaceFragmentWithBackstack(@IdRes containerId: Int, fragment: BaseFragment<*, *>) {
        childFragmentManager.beginTransaction().replace(containerId, fragment, fragment.TAG).addToBackStack(fragment.TAG).commitAllowingStateLoss()
    }

    override fun showProgressView() = activity.showProgressView()

    override fun hideProgressView() = activity.hideProgressView()

}
