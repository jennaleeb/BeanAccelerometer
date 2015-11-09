package com.uoft.beanaccelerometer;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanDiscoveryListener;
import com.punchthrough.bean.sdk.BeanListener;
import com.punchthrough.bean.sdk.BeanManager;
import com.punchthrough.bean.sdk.message.Acceleration;
import com.punchthrough.bean.sdk.message.BeanError;
import com.punchthrough.bean.sdk.message.Callback;
import com.punchthrough.bean.sdk.message.ScratchBank;

public class MainActivity extends AppCompatActivity {

    TextView mBeanName;
    TextView mConnectStatus;
    TextView xValueText;
    TextView yValueText;
    TextView zValueText;

    Button mConnectBeanButton;
    Button mDisconnectBeanButton;

    Bean mBean;

    Handler mHandler;
    Runnable mPollAccelerationRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBeanName = (TextView) findViewById(R.id.textViewBeanName);
        mConnectStatus = (TextView) findViewById(R.id.textViewConnectStatus);
        xValueText = (TextView) findViewById(R.id.xValueText);
        yValueText = (TextView) findViewById(R.id.yValueText);
        zValueText = (TextView) findViewById(R.id.zValueText);
        mConnectBeanButton = (Button) findViewById(R.id.connectBeanButton);
        mDisconnectBeanButton = (Button) findViewById(R.id.disconnectBeanButton);

        mConnectBeanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectBean();
            }
        });

        mDisconnectBeanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnectBean();
                mConnectStatus.setText("Bean Disconnected");
                mBeanName.setText("Bean Name?");
                xValueText.setText("0.00");
                yValueText.setText("0.00");
                zValueText.setText("0.00");
            }
        });


    }

    public void connectBean() {
        BeanDiscoveryListener listener = new BeanDiscoveryListener() {
            @Override
            public void onBeanDiscovered(Bean bean, int rssi) {
                mBean = bean;
            }

            @Override
            public void onDiscoveryComplete() {

                mBeanName.setText(mBean.getDevice().getName());
                mBean.connect(getApplicationContext(), beanListener);
            }
        };

        BeanManager.getInstance().startDiscovery(listener);
    }

    public void disconnectBean() {
        mBean.disconnect();
    }

    BeanListener beanListener = new BeanListener() {
        @Override
        public void onConnected() {
            mConnectStatus.setText("connected to Bean!");

            mHandler = new Handler();
            mPollAccelerationRunnable = new Runnable() {
                @Override
                public void run() {
                    pollAcceleration();
                }
            };


            mPollAccelerationRunnable.run();

        }

        @Override
        public void onConnectionFailed() {

        }

        @Override
        public void onDisconnected() {

        }

        @Override
        public void onSerialMessageReceived(byte[] data) {

        }

        @Override
        public void onScratchValueChanged(ScratchBank bank, byte[] value) {

        }

        @Override
        public void onError(BeanError error) {

        }

    };

    public void pollAcceleration() {
        mBean.readAcceleration(new Callback<Acceleration>() {
            @Override
            public void onResult(Acceleration result) {
                // do something with the result
                xValueText.setText("" + result.x());
                yValueText.setText("" + result.y());
                zValueText.setText("" + result.z());
                // ...and schedule another call
                mHandler.postDelayed(mPollAccelerationRunnable, 250);
            }
        });
    }

}
