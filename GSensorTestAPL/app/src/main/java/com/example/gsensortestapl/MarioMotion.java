package com.example.gsensortestapl;


import android.os.CountDownTimer;
import android.widget.ImageView;


public class MarioMotion {
    public interface ActionListener {
        public void onJumpEnd(float max_y);
    }
    private class location {
        public float x;
        public float y;

        public void copy(location copyPoint){
            copyPoint.x = this.x;
            copyPoint.y = this.y;
        }
    }

    final static int JUMP_UPDATE_TIME = 100;            // ジャンプ更新時間(ms)
    final static int JUMP_TOTAL_TIME = 1807;            // ジャンプトータル時間(ms)
    final static int GRAVITY_ACCELERATION = 980;        // 重力加速度 9.8 m/s^2 → 980 dp /s^2
    final static int TIME_CARRY = 1000;                 // ms → s 桁上げ値

    // 内部フィールド
    private int timeCount;
    private int start_speed;                 // 初速度(dp/s)
    private float start_y;
    private float max_y;
    private CountDownTimer mCountDownTimer;
    private ImageView imgMario;
    private ActionListener mActionFinishNotify;

    // コンストラクタ
    public MarioMotion(ImageView imgMario){
        this.imgMario = imgMario;
    }

    // 外部メソッド
    public void Jump(ActionListener listener, int speed ){
        timeCount = 0;
        start_y = imgMario.getTop();
        max_y = 0;
        start_speed = speed;
        mActionFinishNotify = listener;
        startTimer();
        return;
    }

    // 内部メソッド
    private void startTimer(){
        mCountDownTimer = new CountDownTimer(JUMP_TOTAL_TIME,JUMP_UPDATE_TIME) {
            @Override
            public void onTick(long millisUntilFinished) {
                float move_y;
                long timeCount = JUMP_TOTAL_TIME - millisUntilFinished;
                move_y = ((start_speed * timeCount) / TIME_CARRY ) -
                        ((GRAVITY_ACCELERATION * timeCount * timeCount) / (2 * TIME_CARRY * TIME_CARRY));
                if( move_y < 0 ) {
                    move_y = 0;
                    this.cancel();
                    mActionFinishNotify.onJumpEnd(max_y);
                }else if(move_y > max_y ) {
                    max_y = move_y;
                }
                imgMario.layout(imgMario.getLeft(), (int)(start_y - move_y), imgMario.getLeft() + imgMario.getWidth(), (int)(start_y - move_y + imgMario.getHeight()));
            }

            @Override
            public void onFinish() {
                imgMario.layout(imgMario.getLeft(), (int)start_y, imgMario.getLeft() + imgMario.getWidth(), (int)(start_y + imgMario.getHeight()));
                mActionFinishNotify.onJumpEnd(max_y);
            }
        }.start();
    }
}
