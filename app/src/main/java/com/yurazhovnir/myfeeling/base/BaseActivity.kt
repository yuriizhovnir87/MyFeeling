package com.yurazhovnir.myfeeling.base

import android.content.*
import android.os.*
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import androidx.appcompat.app.*
import androidx.databinding.ViewDataBinding
import com.yurazhovnir.myfeeling.R

abstract class BaseActivity<VB : ViewDataBinding>(private val inflate: Inflate<VB>) :
    AppCompatActivity() {
    private var _binding: VB? = null
    val binding get() = _binding!!
    private val delegate = object : LayoutChangeListener.Delegate {
        private val uiHandler = Handler(Looper.getMainLooper())

        override fun layoutDidChange(oldHeight: Int, newHeight: Int, tempBottom: Int) {
            uiHandler.post {
                if (tempBottom <= (resources?.getDimension(R.dimen.navigation_height)
                        ?.toInt() ?: 82)
                ) {
                    return@post
                }
                keyboardStateChanged(newHeight > oldHeight)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        _binding = inflate.invoke(layoutInflater, null, false)
        val view = binding.root
        setContentView(view)
        view.addOnLayoutChangeListener(LayoutChangeListener().also {
            it.delegate = delegate
        })
        hideKeyboard()
    }

    protected fun replace(
        @IdRes hostId: Int,
        fragment: BaseFragment<out ViewDataBinding>,
        isAddToBackStack: Boolean = true,
    ) {
        val trans = supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_bottom,
                0,
                0,
                R.anim.slide_out_bottom
            )
            .replace(hostId, fragment, fragment.TAG)
        if (isAddToBackStack) {
            trans.addToBackStack(fragment.TAG)
        }
        trans.commit()
    }

    fun add(@IdRes hostId: Int, fragment: BaseFragment<out ViewDataBinding>) {
        try {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_bottom,
                    0,
                    0,
                    R.anim.slide_out_bottom
                )
                .add(hostId, fragment, fragment.TAG)
                .addToBackStack(fragment.TAG)
                .commit()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun hideKeyboard() {
        try {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(window?.decorView?.windowToken, 0)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    open fun keyboardStateChanged(isShown: Boolean) {}

    override fun onBackPressed() {
        val fragment =
            this.supportFragmentManager.findFragmentById(android.R.id.content)
        if (false == (fragment as? OnBackPressed)?.onBackPressed()) {
            super.onBackPressed()
        }
        (fragment as? OnBackPressed)?.onBackPressed()?.not()?.let {

        }
    }
}

interface OnBackPressed {
    fun onBackPressed(): Boolean
}