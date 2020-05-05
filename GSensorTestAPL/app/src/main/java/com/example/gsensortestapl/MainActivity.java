package com.example.gsensortestapl;

import androidx.appcompat.app.AppCompatActivity;


import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity implements MarioMotion.ActionListener,MotionJudge.JudgeListener {

    private enum AplStatus {
        WAIT,
        JUMPING
    }
    private ImageView imgMario;
    private TextView MainText;
    private Button buttonStartStop;

    private MarioMotion mMarioMotion;
    private MotionJudge mMotionJudge;
    private SoundPool mSoundPool;
    private int soundID;

    private String fileName = "file.txt";
    private String sensorLog;

    private AplStatus mAplStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgMario = (ImageView) findViewById(R.id.imgMario);
        MainText = (TextView) findViewById(R.id.textMain);
        buttonStartStop = (Button) findViewById(R.id.btnStartStop);

        mMarioMotion = new MarioMotion(imgMario);
        mMotionJudge = new MotionJudge((SensorManager) getSystemService(SENSOR_SERVICE));
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        mAplStatus = AplStatus.WAIT;
        // createFile(fileName);
        // sensorLog="";

        buttonStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickStartStopButton();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        soundID = mSoundPool.load(this,R.raw.jump05,0);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(mAplStatus != AplStatus.WAIT){
            StopProc();
        }
        mSoundPool.unload(soundID);
    }

    private void clickStartStopButton(){
        if(mAplStatus == AplStatus.WAIT){
            StartProc();
        }
        else {
            StopProc();
        }
    }
    @Override
    public void onJumpEnd(float max_y) {
        String LogText;
        LogText = MainText.getText() + String.format("JumpHeight:%f[m]\n",(max_y/100));
        MainText.setText(LogText);
//        saveFile(fileName,sensorLog);
        mMotionJudge.Restart();
    }

    @Override
    public void onJump(float diff){
        MainText.setText(String.format("Jump!!:%d\n",(int)(diff * 20)));
//        mMarioMotion.Jump(this, 886);
        mMarioMotion.Jump(this, (int)(diff * 20) );
        mSoundPool.play(soundID,1.0F,1.0F, 0,0,1.0F);
    }

    private void StartProc(){
        mMotionJudge.StartJudge(this);

        buttonStartStop.setText("停止");
        mAplStatus = AplStatus.JUMPING;
    }

    private void StopProc(){
        mMotionJudge.StopJudge();

        buttonStartStop.setText("開始");
        mAplStatus = AplStatus.WAIT;
    }

    private void createFile(String filename) {
        File file = new File(getFilesDir() + "/" + filename);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                MainText.setText("Error:Create File!");
            }
        }
        else {
            MainText.setText("File Exist."+getFilesDir());
        }
    }

    private void saveFile(String file, String str) {
        try {
            FileOutputStream fos = openFileOutput(file, MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter writer = new BufferedWriter(osw);
            writer.write(str);
            writer.close();
            MainText.setText(MainText.getText() +"\n Write File!"+getFilesDir());
        } catch (IOException e) {
            MainText.setText("Error:Write File!"+getFilesDir());
        }
    }
}
