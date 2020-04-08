package com.changwon.chiwen.frequency;

import android.util.Log;


/**
 * Desc:
 */
public class processor_add {
    static {
        try {
            //載入降噪庫
            System.loadLibrary("webrtc");
        } catch (UnsatisfiedLinkError e) {
            Log.e("TAG", e.getMessage());
        }

    }

    /**
     * 處理降噪
     *
     * @param data
     */
    public void processNoise(byte[] data) {
        if (data == null) return;
        int newDataLength = data.length / 2;
        if (data.length % 2 == 1) {
            newDataLength += 1;
        }
        //此處是將位元組資料轉換為short資料
        short[] newData = new short[newDataLength];
        for (int i = 0; i < newDataLength; i++) {
            byte low = 0;
            byte high = 0;
            if (2 * i < data.length) {
                low = data[2 * i];
            }
            if ((2 * i + 1) < data.length) {
                high = data[2 * i + 1];
            }
            newData[i] = (short) (((high << 8) & 0xff00) | (low & 0x00ff));
        }

        // 交給底層處理
        processNoise(newData);
        //處理完之後, 又將short資料轉換為位元組資料
        for (int i = 0; i < newDataLength; i++) {
            if (2 * i < data.length) {
                data[2 * i] = (byte) (newData[i] & 0xff);
            }
            if ((2 * i + 1) < data.length) {
                data[2 * i + 1] = (byte) ((newData[i] >> 8) & 0xff);
            }
        }

    }

    /**
     * 初始化降噪設置
     *
     * @param sampleRate 取樣速率
     * @return 是否初始化成功
     */
    public native boolean init(int sampleRate);

    /**
     * 處理降噪
     *
     * @param data
     * @return
     */
    public native boolean processNoise(short[] data);

    /**
     * 釋放降噪資源
     */
    public native void release();
}
