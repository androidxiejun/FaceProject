package com.example.xj.faceandidcardproject.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;

import com.example.xj.faceandidcardproject.R;

public class MainActivity extends AppCompatActivity {

    SurfaceView mPreSurfaceView;
    SurfaceView mRectSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
