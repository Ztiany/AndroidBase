package com.android.sdk.mediaselector;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * 裁剪配置类
 * Author: JPH
 * Date: 2016/7/27 13:19
 */
@SuppressWarnings("WeakerAccess")
public class CropOptions implements Parcelable {

    private int aspectX = 1;
    private int aspectY = 1;
    private int outputX;
    private int outputY;

    public CropOptions() {
    }

    protected CropOptions(Parcel in) {
        aspectX = in.readInt();
        aspectY = in.readInt();
        outputX = in.readInt();
        outputY = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(aspectX);
        dest.writeInt(aspectY);
        dest.writeInt(outputX);
        dest.writeInt(outputY);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CropOptions> CREATOR = new Creator<CropOptions>() {
        @Override
        public CropOptions createFromParcel(Parcel in) {
            return new CropOptions(in);
        }

        @Override
        public CropOptions[] newArray(int size) {
            return new CropOptions[size];
        }
    };

    int getAspectX() {
        return aspectX;
    }

    /**
     * 裁剪宽度比例 与aspectY组合，如16:9
     */
    public CropOptions setAspectX(int aspectX) {
        this.aspectX = aspectX;
        return this;
    }

    int getAspectY() {
        return aspectY;
    }

    /**
     * 高度比例与aspectX组合，如16:9
     */
    public CropOptions setAspectY(int aspectY) {
        this.aspectY = aspectY;
        return this;
    }

    int getOutputX() {
        return outputX;
    }

    /**
     * 输出图片的宽度
     */
    public CropOptions setOutputX(int outputX) {
        this.outputX = outputX;
        return this;
    }

    int getOutputY() {
        return outputY;
    }

    /**
     * 输入图片的高度
     */
    public CropOptions setOutputY(int outputY) {
        this.outputY = outputY;
        return this;
    }

}