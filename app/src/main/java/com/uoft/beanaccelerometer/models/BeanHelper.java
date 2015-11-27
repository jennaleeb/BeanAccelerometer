package com.uoft.beanaccelerometer.models;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanDiscoveryListener;
import com.punchthrough.bean.sdk.BeanListener;
import com.punchthrough.bean.sdk.BeanManager;
import com.punchthrough.bean.sdk.message.Acceleration;
import com.punchthrough.bean.sdk.message.BeanError;
import com.punchthrough.bean.sdk.message.Callback;
import com.punchthrough.bean.sdk.message.ScratchBank;

/**
 * Created by jenna on 15-11-09.
 */
public class BeanHelper extends Application {

    Bean mBean;
    Handler mHandler;
    Runnable mPollAccelerationRunnable;

    private double xValue;
    private double yValue;
    private double zValue;
    private double pValue;
    private double threshold;

    private double xPreviousValue;
    private double yPreviousValue;
    private double zPreviousValue;
    private double pPreviousValue;


    Context mContext;
    String mAddress;

    StepCounter mStepCounter;


    public BeanHelper(StepCounter stepCounter){
        mStepCounter = stepCounter;

        // intialize variables

        // TODO: play with this threshold
        threshold = 0.05;
        xPreviousValue = 0.0;
        yPreviousValue = 0.0;
        zPreviousValue = 0.0;
        pPreviousValue = 0.0;
    }

    public void connectBean() {
        mContext = this;

        BeanDiscoveryListener listener = new BeanDiscoveryListener() {
            @Override
            public void onBeanDiscovered(com.punchthrough.bean.sdk.Bean bean, int rssi) {
                mBean = bean;
            }

            @Override
            public void onDiscoveryComplete() {

                mBean.connect(mContext, beanListener);
                mAddress = mBean.getDevice().getAddress();
                Log.i("Address:", mAddress+"");
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
                // do something with the result (for view, delete later)
//                setxValue(result.x());
//                setyValue(result.y());
//                setzValue(result.z());

                xValue = result.x();
                yValue = result.y();
                zValue = result.z();

                pValue = Math.sqrt(xValue*xValue + yValue*yValue + zValue*zValue);

                if (Math.abs(yValue - yPreviousValue) > threshold && Math.abs(pValue - pPreviousValue) > threshold) {
                    stepCount++;
                    mStepCounter.setStepCount(stepCount);
                    Log.i("step count: ", stepCount + "");
                }

                xPreviousValue = xValue;
                yPreviousValue = yValue;
                zPreviousValue = zValue;
                pPreviousValue = pValue;



                //and schedule another call
                mHandler.postDelayed(mPollAccelerationRunnable, 250);

            }
        });
    }
    int stepCount = 0;


    public double getxValue() {
        return xValue;
    }

    public void setxValue(double xValue) {
        this.xValue = xValue;
    }

    public double getyValue() {
        return yValue;
    }

    public void setyValue(double yValue) {
        this.yValue = yValue;
    }

    public double getzValue() {
        return zValue;
    }

    public void setzValue(double zValue) {
        this.zValue = zValue;
    }

}
