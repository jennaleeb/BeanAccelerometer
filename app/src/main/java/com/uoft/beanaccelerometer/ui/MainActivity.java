package com.uoft.beanaccelerometer.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.uoft.beanaccelerometer.R;
import com.uoft.beanaccelerometer.models.BeanHelper;

public class MainActivity extends AppCompatActivity {

    TextView mBeanNameText;
    TextView mConnectStatusText;
    TextView xValueText;
    TextView yValueText;
    TextView zValueText;

    Button mConnectBeanButton;
    Button mDisconnectBeanButton;
    BeanHelper mBeanHelper;

    Boolean mIsBeanOn = false;
    final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBeanNameText = (TextView) findViewById(R.id.textViewBeanName);
        mConnectStatusText = (TextView) findViewById(R.id.textViewConnectStatus);
        xValueText = (TextView) findViewById(R.id.xValueText);
        yValueText = (TextView) findViewById(R.id.yValueText);
        zValueText = (TextView) findViewById(R.id.zValueText);
        mConnectBeanButton = (Button) findViewById(R.id.connectBeanButton);
        mDisconnectBeanButton = (Button) findViewById(R.id.disconnectBeanButton);
        mBeanHelper = new BeanHelper();





        mConnectBeanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mBeanHelper.connectBean();

                mBeanNameText.setText(mBeanHelper.getBeanName());
                mConnectStatusText.setText(mBeanHelper.getConnectStatus());

                updateUI();

                mIsBeanOn = true;


            }
        });

        mDisconnectBeanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBeanHelper.disconnectBean();
                mIsBeanOn = false;

                mConnectStatusText.setText("Bean Disconnected");
                mBeanNameText.setText("Bean Name?");

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
