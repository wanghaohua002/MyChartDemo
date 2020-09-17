package com.wanghaohua.mychartlinedemo.timetrend;

import androidx.annotation.IntDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wanghaohua
 * @date 2018/4/10 16:18
 */

public class TimeTrendModel {

    public static final int STATE_FLAT = 0;
    public static final int STATE_FALL = 1;
    public static final int STATE_RISE = 2;
    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.FIELD)
    @IntDef({STATE_FLAT, STATE_FALL, STATE_RISE})
    public @interface VolumeState {
    }

    @SerializedName("m")
    private String time;    //时间

    @SerializedName("p")
    private String price;   //当前价
    @SerializedName("v")
    private String volume;  //成交量
    @VolumeState public int state; //false:上涨还是 true:下跌

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }
}
