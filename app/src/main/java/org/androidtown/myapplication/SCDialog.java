package org.androidtown.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;


public class SCDialog extends Dialog {
    public SCDialog(Context context) {
        super(context);

        /* Initial setting*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        /* Changes to FLAG_NOT_TOUCH_MODAL */
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL , WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);  //다이어로그 실행중 다른 view 가능하게하기

        setContentView(R.layout.custom_dialog);
    }
}
