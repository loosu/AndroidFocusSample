package com.loosu.androidfocussample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class DemoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_request_focus_demo:
                startActivity(new Intent(this, RequestFocusDemosActivity.class));
                break;
        }
    }
}
