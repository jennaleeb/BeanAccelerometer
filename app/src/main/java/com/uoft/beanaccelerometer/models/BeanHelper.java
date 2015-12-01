package com.uoft.beanaccelerometer.models;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanDiscoveryListener;
import com.punchthrough.bean.sdk.BeanListener;
import com.punchthrough.bean.sdk.BeanManager;
import com.punchthrough.bean.sdk.message.Acceleration;
import com.punchthrough.bean.sdk.message.BeanError;
import com.punchthrough.bean.sdk.message.Callback;
import com.punchthrough.bean.sdk.message.ScratchBank;
import com.uoft.beanaccelerometer.DataRecorder;

/**
 * Created by jenna on 15-11-09.
 */
public class BeanHelper extends Application {

    Bean mBean;
    Handler mHandler;
    Runnable mPollAccelerationRunnable;

    private float xValue;
    private float yValue;
    private float zValue;
    private float pValue;

    private float threshold;
    private long systemStartTime;
    private long timeSinceStart;

    private float xPreviousValue;
    private float yPreviousValue;
    private float zPreviousValue;
    private float pPreviousValue;





    Context mContext;
    StepCounter mStepCounter;
    DataRecorder mLogData;


    public BeanHelper(StepCounter stepCounter){
        mStepCounter = stepCounter;
        mLogData = new DataRecorder();
        systemStartTime = System.nanoTime();

        // intialize variables

        // TODO: play with this threshold?
        threshold = (float) 0.5;
        xPreviousValue = 0;
        yPreviousValue = 0;
        zPreviousValue = 0;
        pPreviousValue = 0;

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
            }
        };

        BeanManager.getInstance().startDiscovery(listener);
    }

    public void disconnectBean() {
        mBean.disconnect();

        // Turn of logger and save file
        mLogData.disableLogger();
    }

    BeanListener beanListener = new BeanListener() {
        @Override
        public void onConnected() {

            mLogData.enableLogger();
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

                xValue = (float) result.x();
                yValue = (float) result.y();
                zValue = (float) result.z();
                pValue = (float) Math.sqrt(xValue * xValue + yValue * yValue + zValue * zValue);

                // Correct the start time
                timeSinceStart = System.nanoTime() - systemStartTime;
                mLogData.logAccelReadings(timeSinceStart, xValue, yValue, zValue);



                // Accel values to step count
                if (Math.abs(yValue - yPreviousValue) > threshold && Math.abs(pValue - pPreviousValue) > threshold) {
                    stepCount++;
                    mStepCounter.setStepCount(stepCount);

                }

                xPreviousValue = xValue;
                yPreviousValue = yValue;
                zPreviousValue = zValue;
                pPreviousValue = pValue;



                //and schedule another call
                mHandler.postDelayed(mPollAccelerationRunnable, 0);

            }
        });
    }
    int stepCount = 0;


    public float getxValue() {
        return xValue;
    }

    public void setxValue(float xValue) {
        this.xValue = xValue;
    }

    public float getyValue() {
        return yValue;
    }

    public void setyValue(float yValue) {
        this.yValue = yValue;
    }

    public float getzValue() {
        return zValue;
    }

    public void setzValue(float zValue) {
        this.zValue = zValue;
    }

}
