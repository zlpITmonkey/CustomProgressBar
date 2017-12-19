package com.example.customprogressbar;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {
    private TextView tv;
    private ProgressBar progress;
    private RoundProgressBarWithProgress progress2;
    private int i = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (i > 100) {
                return;
            }
            progress.setProgress(msg.arg1);
            progress2.setProgress(msg.arg1);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress = (ProgressBar) findViewById(R.id.progress);
        progress2 = (RoundProgressBarWithProgress) findViewById(R.id.progress2);
        new Thread() {
            @Override
            public void run() {
                while (i <= 100) {
                    Message msg = new Message();
                    msg.arg1 = i;
                    mHandler.dispatchMessage(msg);
                    i++;
                    try {
                        sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
