package org.androidtown.myapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.SeekBar;
import android.widget.TextView;


public class SettingActivity extends Activity {
    private SeekBar sb_height;
    private SeekBar sb_speed;
    private int height = 0;
    private int speed = 0;
    private TextView tv_strHeight;
    private TextView tv_strSpeed;
    private static final int GET_PIC_CODE = 1;
    SharedPreferences mPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        sb_height = (SeekBar) findViewById(R.id.sb_height);
        sb_speed = (SeekBar) findViewById(R.id.sb_speed);
        tv_strHeight = (TextView) findViewById(R.id.tv_strHeight);
        tv_strSpeed = (TextView) findViewById(R.id.tv_strSpeed);
        tv_strHeight.setText("PITCH " + String.valueOf(height + 1));
        tv_strSpeed.setText("SPEED " + String.valueOf(speed + 1));

        String pitch = mPref.getString("pitch", "PITCH 1");
        String speed = mPref.getString("speed", "SPEED 1");
        int pitch_progress = mPref.getInt("PITCH_PROGRESS", 1);
        int speed_progress = mPref.getInt("SPEED_PROGRESS", 1);
        tv_strHeight.setText(pitch);
        tv_strSpeed.setText(speed);
        sb_height.setProgress(pitch_progress);
        sb_speed.setProgress(speed_progress);

        sb_height.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //저장
                setHeight(progress + 1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                doAfterTrackHeight();
                SharedPreferences.Editor editor = mPref.edit();
                editor.putInt("PITCH_PROGRESS", seekBar.getProgress());
                editor.putString("pitch", tv_strHeight.getText().toString());
                editor.commit();
            }
        });

        sb_speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setSpeed(progress + 1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                doAfterTrackSpeed();
                SharedPreferences.Editor editor = mPref.edit();
                editor.putInt("SPEED_PROGRESS", seekBar.getProgress());
                editor.putString("speed", tv_strSpeed.getText().toString());
                editor.commit();
            }
        });


    }

    public void setHeight(int value){
        tv_strHeight.setText(String.valueOf(value));
        if(value == 1) MainActivity.myTTS.setPitch(1.f);
        else if(value == 2) MainActivity.myTTS.setPitch(2.f);
        else if(value == 3) MainActivity.myTTS.setPitch(3.f);
    }

    public void setSpeed(int value){
        tv_strSpeed.setText(String.valueOf(value));
        if(value == 1) MainActivity.myTTS.setSpeechRate(1.f);
        else if(value == 2) MainActivity.myTTS.setSpeechRate(2.f);
        else if(value == 3) MainActivity.myTTS.setSpeechRate(3.f);
    }

    public void doAfterTrackHeight(){
        tv_strHeight.setText("PITCH "+tv_strHeight.getText());
    }

    public void doAfterTrackSpeed(){
        tv_strSpeed.setText("SPEED " + tv_strSpeed.getText());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

}
