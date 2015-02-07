package com.example.pathandsvgdemo;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;


public class SvgDemo extends Activity {
    private SvgAnimView mSvgAnimView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.svg_demo);
        mSvgAnimView = (SvgAnimView)findViewById(R.id.svg_id);
        Button button = (Button)findViewById(R.id.btn_startanim);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSvgAnimView.startAnim();
            }
        });
    }
}
