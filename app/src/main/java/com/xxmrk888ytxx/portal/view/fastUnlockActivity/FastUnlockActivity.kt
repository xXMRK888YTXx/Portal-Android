package com.xxmrk888ytxx.portal.view.fastUnlockActivity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import javax.inject.Inject

class FastUnlockActivity @Inject constructor(
    private val fastUnlockActivityViewModelFactory: FastUnlockActivityViewModel.Factory
) : FragmentActivity() {

    private val fastUnlockActivityViewModel by viewModels<FastUnlockActivityViewModel> { fastUnlockActivityViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                fastUnlockActivityViewModel.onFinishEvent.collect {
                    finish()
                }
            }
        }
        setContent {  }
    }

    override fun onResume() {
        super.onResume()
        fastUnlockActivityViewModel.requestUnlock(this, intent)
    }

    companion object {
        const val SHORTCUT_ID_EXTRA = "shortcutId"

    }
}