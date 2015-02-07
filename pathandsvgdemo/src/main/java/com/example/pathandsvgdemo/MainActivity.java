package com.example.pathandsvgdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button pathBtn = (Button)findViewById(R.id.path_effect_id);
        pathBtn.setOnClickListener(this);

        Button svgBtn = (Button)findViewById(R.id.svg_id);
        svgBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.path_effect_id:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, PathEffectsDemo.class);
                startActivity(intent);
                break;
            case R.id.svg_id:
                Intent intent2 = new Intent();
                intent2.setClass(MainActivity.this, SvgDemo.class);
                startActivity(intent2);
                break;
        }
    }
}
