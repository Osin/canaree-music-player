@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.extensions

import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.Px
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.forEach


fun View.toggleVisibility(visible: Boolean, gone: Boolean) {
    if (visible) {
        this.visibility = View.VISIBLE
    } else {
        if (gone) {
            this.visibility = View.GONE
        } else {
            this.visibility = View.INVISIBLE
        }
    }
}

inline fun View.setGone() {
    this.visibility = View.GONE
}

inline fun View.setVisible() {
    this.visibility = View.VISIBLE
}

inline fun View.setInvisible() {
    this.visibility = View.INVISIBLE
}

fun View.setPaddingTop(padding: Int) {
    setPadding(paddingLeft, padding, paddingRight, paddingBottom)
}

inline fun View.setPaddingBottom(padding: Int) {
    setPadding(paddingLeft, paddingTop, paddingRight, padding)
}

inline fun View.toggleSelected() {
    this.isSelected = !this.isSelected
}

inline fun ViewGroup.forEachRecursively(action: (view: View) -> Unit) {
    forEach {
        if (it is ViewGroup) {
            it.forEach(action)
        } else {
            action(it)
        }
    }
}

fun View.setHeight(@Px heightPx: Int) {
    val params = this.layoutParams
    when (params) {
        is FrameLayout.LayoutParams -> params.height = heightPx
        is LinearLayout.LayoutParams -> params.height = heightPx
        is RelativeLayout.LayoutParams -> params.height = heightPx
        is CoordinatorLayout.LayoutParams -> params.height = heightPx
        is ConstraintLayout.LayoutParams -> params.height = heightPx
    }
    layoutParams = params
}

fun View.setWidth(@Px heightPx: Int) {
    val params = this.layoutParams
    when (params) {
        is FrameLayout.LayoutParams -> params.width = heightPx
        is LinearLayout.LayoutParams -> params.width = heightPx
        is RelativeLayout.LayoutParams -> params.width = heightPx
        is CoordinatorLayout.LayoutParams -> params.width = heightPx
        is ConstraintLayout.LayoutParams -> params.width = heightPx
    }
    layoutParams = params
}

fun ViewGroup.findChild(filter: (View) -> Boolean): View? {
    var child: View? = null

    forEachRecursively {
        if (filter(it)) {
            child = it
            return@forEachRecursively
        }
    }

    return child
}

fun <T : View> View.findViewByIdNotRecursive(id: Int): T? {
    if (this is ViewGroup) {
        forEach { child ->
            if (child.id == id) {
                return child as T
            }
        }
    }
    return null
}

inline fun <reified T : View> View.findParentByType(): T? {
    var currentParent: ViewParent? = parent
    while (currentParent != null) {
        if (currentParent is T) {
            return currentParent
        }
        currentParent = currentParent.parent
    }
    return null
}