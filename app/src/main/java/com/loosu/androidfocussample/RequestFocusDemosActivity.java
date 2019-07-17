package com.loosu.androidfocussample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class RequestFocusDemosActivity extends AppCompatActivity {

    private View view1;
    private View btn1;
    private View view2;
    private View btn2;
    private View view3;
    private View btn3;
    private View view4;
    private View btn4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_focus_demos);
        view1 = findViewById(R.id.view_1);
        btn1 = findViewById(R.id.btn_1);

        view2 = findViewById(R.id.view_2);
        btn2 = findViewById(R.id.btn_2);

        view3 = findViewById(R.id.view_3);
        btn3 = findViewById(R.id.btn_3);

        view4 = findViewById(R.id.view_4);
        btn4 = findViewById(R.id.btn_4);

        btn1.setOnClickListener(onClickListener);
        btn2.setOnClickListener(onClickListener);
        btn3.setOnClickListener(onClickListener);
        btn4.setOnClickListener(onClickListener);
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_1:
                    view1.requestFocus();
                    break;
                case R.id.btn_2:
                    view2.requestFocus();
                    break;
                case R.id.btn_3:
                    view3.requestFocus();
                    break;
                case R.id.btn_4:
                    view4.requestFocus();
                    break;
            }
        }
    };
}
