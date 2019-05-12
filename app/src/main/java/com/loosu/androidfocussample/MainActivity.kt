package com.loosu.androidfocussample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import com.loosu.alog.ALog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view_list.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        view_list.adapter = KAdapter()

        btn_focus_backward.setOnClickListener(mOnClickListener)
        btn_focus_forward.setOnClickListener(mOnClickListener)

        btn_focus_up.setOnClickListener(mOnClickListener)
        btn_focus_down.setOnClickListener(mOnClickListener)
        btn_focus_left.setOnClickListener(mOnClickListener)
        btn_focus_right.setOnClickListener(mOnClickListener)

        layout_focus_root.setOnClickListener(mFocusTestClilckListener)
        layout_focus_group_1.setOnClickListener(mFocusTestClilckListener)
        layout_focus_group_2.setOnClickListener(mFocusTestClilckListener)
        layout_focus_group_1_class_1.setOnClickListener(mFocusTestClilckListener)
        layout_focus_group_1_class_2.setOnClickListener(mFocusTestClilckListener)
        layout_focus_group_2_class_1.setOnClickListener(mFocusTestClilckListener)
        layout_focus_group_2_class_2.setOnClickListener(mFocusTestClilckListener)
        view_focus_group_1_class_1_1.setOnClickListener(mFocusTestClilckListener)
        view_focus_group_1_class_1_2.setOnClickListener(mFocusTestClilckListener)
        view_focus_group_1_class_1_3.setOnClickListener(mFocusTestClilckListener)
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        ALog.d(TAG, event)
        currentFocus.requestFocus()
        return super.dispatchKeyEvent(event)
    }

    private fun onClickBtnFocusBackward(view: View) {
        ALog.d(TAG)
        requestFocusWhitSearch(View.FOCUS_BACKWARD)
    }

    private fun onClickBtnFocusForkward(view: View) {
        ALog.d(TAG)
        requestFocusWhitSearch(View.FOCUS_FORWARD)
    }

    private fun onClickBtnFocusUp(view: View) {
        ALog.d(TAG)
        requestFocusWhitSearch(View.FOCUS_UP)
    }

    private fun onClickBtnFocusDown(view: View) {
        ALog.d(TAG)
        requestFocusWhitSearch(View.FOCUS_DOWN)
    }

    private fun onClickBtnFocusLeft(view: View) {
        ALog.d(TAG)
        requestFocusWhitSearch(View.FOCUS_LEFT)
    }

    private fun onClickBtnFocusRight(view: View) {
        ALog.d(TAG)
        requestFocusWhitSearch(View.FOCUS_RIGHT)
    }

    private fun requestFocusWhitSearch(@ViewCompat.FocusRealDirection direction: Int) {
        val currentFocus = this.currentFocus
        val newFocus = currentFocus?.focusSearch(direction)
        val requestFocus = newFocus?.requestFocus()
        ALog.d(TAG, "currentFocus = $currentFocus, newFocus = $newFocus, requestFocus = $requestFocus")
    }

    private val mOnClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.btn_focus_backward -> {
                onClickBtnFocusBackward(view)
            }
            R.id.btn_focus_forward -> {
                onClickBtnFocusForkward(view)
            }
            R.id.btn_focus_up -> {
                onClickBtnFocusUp(view)
            }
            R.id.btn_focus_down -> {
                onClickBtnFocusDown(view)
            }
            R.id.btn_focus_left -> {
                onClickBtnFocusLeft(view)
            }
            R.id.btn_focus_right -> {
                onClickBtnFocusRight(view)
            }
            else -> {
            }
        }
    }

    private val mFocusTestClilckListener = View.OnClickListener { view ->
        val focus = view.requestFocus()
        ALog.d(TAG, "${view.contentDescription} -- mFocused = $focus")
        printFocused(layout_focus_root, 0)
    }

    private fun printFocused(view: View, deep: Int) {
        val sb = StringBuffer()
        for (i in 0 until deep) {
            sb.append('\t')
        }

        if (view is ViewGroup) {
            ALog.d(TAG, "${sb}${view.contentDescription}(isFocused = ${view.isFocused}) -- mFocused = ${view.focusedChild?.contentDescription}")
            for (i in 0 until view.childCount) {
                printFocused(view.getChildAt(i), deep + 1)
            }
        } else {
            ALog.d(TAG, "${sb}${view.contentDescription} (isFocused = ${view.isFocused})")
        }
    }
}
