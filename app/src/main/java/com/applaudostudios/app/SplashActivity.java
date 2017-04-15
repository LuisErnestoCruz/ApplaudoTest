package com.applaudostudios.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.applaudostudios.network.RetrofitCall;
import com.bumptech.glide.Glide;

public class SplashActivity extends AppCompatActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        imageView = (ImageView) findViewById(R.id.imageSplash);
        Glide.with(this).load(R.drawable.windmill).placeholder(R.drawable.windmill).error(R.mipmap.image_not_found).into(imageView);
        callRetrofit();
    }

    public void callRetrofit()
    {
        try
        {
            RetrofitCall retrofitCall = new RetrofitCall(SplashActivity.this);
            retrofitCall.execute();
        }
        catch(Exception er)
        {
            er.printStackTrace();
        }
    }
}
