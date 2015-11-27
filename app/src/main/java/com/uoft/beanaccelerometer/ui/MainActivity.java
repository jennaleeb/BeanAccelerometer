package com.uoft.beanaccelerometer.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.uoft.beanaccelerometer.R;
import com.uoft.beanaccelerometer.models.BeanHelper;
import com.uoft.beanaccelerometer.models.StepCounter;

public class MainActivity extends AppCompatActivity {

    TextView xValueText;
    TextView yValueText;
    TextView zValueText;
    TextView stepCountText;

    Button mConnectBeanButton;
    Button mDisconnectBeanButton;
    BeanHelper mBeanHelper;
    StepCounter mStepCounter;

    Boolean mIsBeanOn = false;
    final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xValueText = (TextView) findViewById(R.id.xValueText);
        yValueText = (TextView) findViewById(R.id.yValueText);
        zValueText = (TextView) findViewById(R.id.zValueText);
        stepCountText = (TextView) findViewById(R.id.stepCountText);

        mConnectBeanButton = (Button) findViewById(R.id.connectBeanButton);
        mDisconnectBeanButton = (Button) findViewById(R.id.disconnectBeanButton);

        mStepCounter = new StepCounter();
        mBeanHelper = new BeanHelper(mStepCounter);





        mConnectBeanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mBeanHelper.connectBean();


                updateUI();

                mIsBeanOn = true;


            }
        });

        mDisconnectBeanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBeanHelper.disconnectBean();
                mIsBeanOn = false;


                // TODO: Values not going back to 0
                xValueText.setText("0.00");
                yValueText.setText("0.00");
                zValueText.setText("0.00");

            }
        });


    }

    private void updateUI() {

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {

                // TODO: Replace with some sort of timer?

                while((1 < 2)){

                    //The handler schedules the new runnable on the UI thread
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Update UI
                            xValueText.setText("" + mBeanHelper.getxValue());
                            yValueText.setText("" + mBeanHelper.getyValue());
                            zValueText.setText("" + mBeanHelper.getzValue());
                            stepCountText.setText("" + mStepCounter.getStepCount() );
                        }
                    });
                    //Add some downtime
                    try {
                        Thread.sleep(100);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        new Thread(runnable).start();

    }


}
