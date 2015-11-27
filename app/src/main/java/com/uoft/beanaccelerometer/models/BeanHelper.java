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

    double xValue;
    double yValue;
    double zValue;
    String mBeanName;
    String mConnectStatus;
    Context mContext;
    String mAddress;

    public void connectBean() {
        mContext = this;

        BeanDiscoveryListener listener = new BeanDiscoveryListener() {
            @Override
            public void onBeanDiscovered(com.punchthrough.bean.sdk.Bean bean, int rssi) {
                mBean = bean;
            }

            @Override
            public void onDiscoveryComplete() {

                setBeanName(mBean.getDevice().getName());
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
            setConnectStatus("connected to Bean!");

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
                setxValue(result.x());
                setyValue(result.y());
                setzValue(result.z());

                //and schedule another call
                mHandler.postDelayed(mPollAccelerationRunnable, 250);

            }
        });
    }

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

    public String getBeanName() {
        return mBeanName;
    }

    public void setBeanName(String beanName) {
        mBeanName = beanName;
    }

    public String getConnectStatus() {
        return mConnectStatus;
    }

    public void setConnectStatus(String connectStatus) {
        mConnectStatus = connectStatus;
    }
}
