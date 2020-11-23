package com.mobile.app.sdk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.mobile.sdk.sister.SisterX;

public class Java extends AppCompatActivity {

    public void test(){
        DialogFragment dialog = SisterX.INSTANCE.show(this,false);
        dialog.dismiss();
    }
}
