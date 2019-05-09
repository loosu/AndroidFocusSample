package com.loosu.androidfocussample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
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
        //currentFocus?.focusSearch(View.FOCUS_BACKWARD)!!.requestFocus()
    }

    private fun onClickBtnFocusForkward(view: View) {
        //currentFocus?.focusSearch(View.FOCUS_FORWARD)!!.requestFocus()
    }

    private fun onClickBtnFocusUp(view: View) {
        //currentFocus?.focusSearch(View.FOCUS_UP)!!.requestFocus()
        //currentFocus!!.requestFocus(View.FOCUS_UP)
        val curTime = System.currentTimeMillis()
        val keyCode = KeyEvent.KEYCODE_DPAD_UP
        val downEvent = KeyEvent(curTime, curTime, KeyEvent.ACTION_DOWN, keyCode, 0)
        val upEvent = KeyEvent(curTime + 80, curTime + 80, KeyEvent.ACTION_UP, keyCode, 0)
        onKeyDown(keyCode, downEvent)
        onKeyDown(keyCode, upEvent)
    }

    private fun onClickBtnFocusDown(view: View) {
        //currentFocus?.focusSearch(View.FOCUS_DOWN)!!.requestFocus()
        currentFocus!!.requestFocus(View.FOCUS_DOWN)
    }

    private fun onClickBtnFocusLeft(view: View) {
        //currentFocus?.focusSearch(View.FOCUS_LEFT)!!.requestFocus()
        currentFocus!!.requestFocus(View.FOCUS_LEFT)
    }

    private fun onClickBtnFocusRight(view: View) {
        //currentFocus?.focusSearch(View.FOCUS_RIGHT)!!.requestFocus()
        currentFocus!!.requestFocus(View.FOCUS_RIGHT)
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
