package com.xxmrk888ytxx.portal.view.shortcutUnlockActivity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import javax.inject.Inject

class ShortcutUnlockActivity @Inject constructor(
    private val shortcutUnlockActivityViewModelFactory: ShortcutUnlockActivityViewModel.Factory
) : FragmentActivity() {

    private val shortcutUnlockActivityViewModel by viewModels<ShortcutUnlockActivityViewModel> { shortcutUnlockActivityViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                shortcutUnlockActivityViewModel.onFinishEvent.collect {
                    finish()
                }
            }
        }
        setContent {  }
    }

    override fun onResume() {
        super.onResume()
        shortcutUnlockActivityViewModel.requestUnlock(this, intent)
    }

    companion object {
        const val UNLOCK_FROM_SHORTCUT_ACTION: String = "com.xxmrk888ytxx.portal.UNLOCK_FROM_SHORTCUT_ACTION"
        const val SHORTCUT_ID_EXTRA = "shortcutId"

    }
}