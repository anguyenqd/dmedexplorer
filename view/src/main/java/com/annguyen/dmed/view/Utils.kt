package com.annguyen.dmed.view

import android.graphics.Color
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar


fun CoordinatorLayout.displayNormalSnackBar(
    @StringRes content: Int
) = Snackbar.make(this, content, Snackbar.LENGTH_SHORT).also {
    it.show()
}

fun CoordinatorLayout.displayErrorSnackBar(
    @StringRes content: Int,
    @StringRes actionText: Int? = null,
    action: () -> Unit = {},
    autoDismiss: Boolean = false
) = Snackbar.make(
    this,
    content,
    if (autoDismiss) Snackbar.LENGTH_SHORT else Snackbar.LENGTH_INDEFINITE
).also { snackBar ->
    snackBar.setTextColor(Color.RED)
    actionText?.let { actionText ->
        snackBar.setAction(actionText) { action() }
    }
    snackBar.show()
}