package com.yurazhovnir.myfeeling.base

import android.content.Context
import android.os.*
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.yurazhovnir.myfeeling.R

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewDataBinding>(private val inflate: Inflate<VB>) : Fragment(), OnBackPressed {
    abstract val TAG: String
    private var instanceStateSaved: Boolean = false

    private var _binding: VB? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return _binding!!.root
    }

    private val delegate = object : LayoutChangeListener.Delegate {
        private val uiHandler = Handler(Looper.getMainLooper())

        override fun layoutDidChange(oldHeight: Int, newHeight: Int, tempBottom: Int) {
            uiHandler.post {
                context?.let { context ->
                    if (tempBottom <= context
                            .resources
                            .getDimension(R.dimen.navigation_height)
                            .toInt()
                    ) return@post
                }
                keyboardStateChanged(newHeight > oldHeight)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.addOnLayoutChangeListener(LayoutChangeListener().also {
            it.delegate = delegate
        })
        view.findViewById<ViewGroup>(R.id.rootView)?.setOnClickListener {
            hideKeyboard()
        }
    }

    open fun alertClosed() {}

    override fun onResume() {
        super.onResume()
        instanceStateSaved = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        instanceStateSaved = true
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    private fun isRealRemoving(): Boolean =
        (isRemoving) ||
                ((parentFragment as? BaseFragment<out ViewDataBinding>)?.isRealRemoving() ?: false)

    private fun needCloseScope(): Boolean =
        when {
            activity?.isChangingConfigurations == true -> false
            activity?.isFinishing == true -> true
            else -> isRealRemoving()
        }

    protected fun replace(
        @IdRes hostId: Int = android.R.id.content,
        fragment: BaseFragment<out ViewDataBinding>,
        isAddToBackStack: Boolean = true,
    ) {
        val trans = activity?.supportFragmentManager?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_in_bottom,
                0,
                0,
                R.anim.slide_out_bottom
            )
            ?.replace(hostId, fragment, fragment.TAG)
        trans?.setReorderingAllowed(true)
        if (isAddToBackStack) {
            trans?.addToBackStack(fragment.TAG)
        }
        trans?.commit()
    }

    fun add(
        @IdRes hostId: Int = android.R.id.content,
        fragment: BaseFragment<out ViewDataBinding>,
    ) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_in_bottom,
                0,
                0,
                R.anim.slide_out_bottom
            )
            ?.add(hostId, fragment, fragment.TAG)
            ?.addToBackStack(fragment.TAG)
            ?.setReorderingAllowed(true)
            ?.commit()
    }

    internal fun hideKeyboard() {
        try {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity?.window?.decorView?.windowToken, 0)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    protected fun setStatusBarColor(colorResId: Int) {
        activity?.window?.apply {
            statusBarColor = ContextCompat.getColor(requireContext(), colorResId)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    open fun onBackClick() {
        activity?.supportFragmentManager?.popBackStack()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    open fun keyboardStateChanged(isShown: Boolean) {}

    interface AlertListener {
        fun actionNegative() {}
        fun actionPositive() {}
    }
}