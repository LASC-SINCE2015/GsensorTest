package com.example.gsensortestapl;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class MotionJudge implements SensorEventListener {
    public interface JudgeListener{
        public void onJump(float speed);
    }

    private final static int JUMP_JUDGE_FLAME_NUM = 30;
    private final static float JUMP_JUDGE_Y_DIFF = (float)2.0;
    private final static float JUMP_JUDGE_Z_DIFF = (float)1.0;
    private enum MotionJudgeStatus{
        WAIT,
        JUMP_JUDGE,
        RESTART_WAIT
    }

    private SensorManager mSensorManager;
    private JudgeListener mActionNotify;
    private float sensorYPrev;
    private float sensorYStart;
    private float sensorYMax;
    private float sensorZPrev;
    private float sensorZStart;
    private float sensorZMax;
    private int judgeCount;
    private MotionJudgeStatus status;

    public MotionJudge(SensorManager sensorManager){
        mSensorManager = sensorManager;
    }

    public void StartJudge(JudgeListener lister){
        mActionNotify = lister;
        // Listenerの登録
        Sensor accel = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER);

        //sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
        //sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME);
        //sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI);

        sensorYPrev = 0;
        sensorZPrev = 0;
        status = MotionJudgeStatus.WAIT;
    }

    public void StopJudge() {
        // Listenerを解除
        mSensorManager.unregisterListener(this);
    }

    public void Restart(){
        sensorYPrev = 0;
        sensorZPrev = 0;
        status = MotionJudgeStatus.WAIT;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float sensorY,sensorZ;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // sensorX = event.values[0];
            sensorY = event.values[1];
            sensorZ = event.values[2];

            switch(status) {
                case WAIT:
                    if (((sensorYPrev != 0) && ((sensorZ - sensorYPrev) > JUMP_JUDGE_Y_DIFF))
                            && ((sensorZPrev != 0) && ((sensorZ - sensorZPrev) > JUMP_JUDGE_Z_DIFF)) ){
                        sensorYMax = 0;
                        sensorZMax = 0;
                        judgeCount = 0;
                        sensorYStart = sensorYPrev;
                        sensorZStart = sensorZPrev;
                        status = MotionJudgeStatus.JUMP_JUDGE;
                    }
                    break;
                case JUMP_JUDGE:
                    if(sensorYMax < sensorY){
                        sensorYMax = sensorY;
                    }
                    if(sensorZMax < sensorZ){
                        sensorZMax = sensorZ;
                    }
                    judgeCount++;
                    if(judgeCount >= JUMP_JUDGE_FLAME_NUM) {
                        mActionNotify.onJump(sensorYMax - sensorYStart);
                        status = MotionJudgeStatus.RESTART_WAIT;
                    }
                    break;
                case RESTART_WAIT:
                    // リスタートされるまで待つ
                    break;
                default:
                    status = MotionJudgeStatus.WAIT;
                    break;
            }
            sensorYPrev = sensorY;
            sensorZPrev = sensorZ;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

}
