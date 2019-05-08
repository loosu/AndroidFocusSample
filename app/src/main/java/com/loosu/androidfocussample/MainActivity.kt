package com.loosu.androidfocussample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view_list.layoutManager = LinearLayoutManager(this)
        view_list.adapter = KAdapter()

        btn_focus_backward.setOnClickListener(mOnClickListener)
        btn_focus_forward.setOnClickListener(mOnClickListener)

        btn_focus_up.setOnClickListener(mOnClickListener)
        btn_focus_down.setOnClickListener(mOnClickListener)
        btn_focus_left.setOnClickListener(mOnClickListener)
        btn_focus_right.setOnClickListener(mOnClickListener)
    }

    private fun onClickBtnFocusBackward(view: View) {
    }

    private fun onClickBtnFocusForkward(view: View) {

    }

    private fun onClickBtnFocusUp(view: View) {

    }

    private fun onClickBtnFocusDown(view: View) {

    }

    private fun onClickBtnFocusLeft(view: View) {

    }

    private fun onClickBtnFocusRight(view: View) {

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
}
