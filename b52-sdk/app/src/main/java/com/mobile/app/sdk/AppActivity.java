package com.mobile.app.sdk;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mobile.sdk.sister.SisterX;

public class AppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注意！！！
        // 假如需要登录后马上客服界面显示，请使用异步回调的方式
        SisterX.INSTANCE.isLogin().observe(this, isLogin -> {
            SisterX.INSTANCE.show(AppActivity.this, true);
        });
        SisterX.INSTANCE.setUser("username");
    }
}
