package com.lib_common.view.layout.dialog.update.download;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * author: zhaoke
 * blog  :
 * time  :2020/4/1 10:36
 * desc  : The constants of memory.
 */
public class UpdateBean implements Serializable, Parcelable {


    /**
     * NewVersion : 1.4.2
     * ForceUpdate : 1
     * AppName : TMSApp
     * UpdateLink : http://webapi.i-tms.cn/updateapp/tmsapp/i-tms.apk
     * LastForceUpdateVersion : 1.1.1
     * UpdateDes : 1.加入添加合作车辆；\r\n2.优化性能
     * UpdateVersion: 1.4.4
     */

    private String NewVersion; // 最新版本号
    private String ForceUpdate; // 是否强制升级  0-不升级  1-普通升级  2-强制升级
    private String AppName; // 应用名称
    private String UpdateLink; // 更新包下载地址
    private String LastForceUpdateVersion; // 上次强制升级版本号 该版本号小于等于newVersion
    private String UpdateDes; // 更新提示信息
    private String UpdateVersion; // 待升级版本号  该版本号大于等于newVersion

    public UpdateBean() {
        super();
    }

    protected UpdateBean(Parcel in) {
        NewVersion = in.readString();
        ForceUpdate = in.readString();
        AppName = in.readString();
        UpdateLink = in.readString();
        LastForceUpdateVersion = in.readString();
        UpdateDes = in.readString();
        UpdateVersion = in.readString();
    }

    public static final Creator<UpdateBean> CREATOR = new Creator<UpdateBean>() {
        @Override
        public UpdateBean createFromParcel(Parcel in) {
            return new UpdateBean(in);
        }

        @Override
        public UpdateBean[] newArray(int size) {
            return new UpdateBean[size];
        }
    };

    public String getUpdateVersion() {
        return UpdateVersion;
    }

    public void setUpdateVersion(String updateVersion) {
        this.UpdateVersion = updateVersion;
    }

    public String getNewVersion() {
        return NewVersion;
    }

    public void setNewVersion(String newVersion) {
        this.NewVersion = newVersion;
    }

    public String getForceUpdate() {
        return ForceUpdate;
    }

    public void setForceUpdate(String forceUpdate) {
        this.ForceUpdate = forceUpdate;
    }

    public String getAppName() {
        return AppName;
    }

    public void setAppName(String appName) {
        this.AppName = appName;
    }

    public String getUpdateLink() {
        return UpdateLink;
    }

    public void setUpdateLink(String updateLink) {
        this.UpdateLink = updateLink;
    }

    public String getLastForceUpdateVersion() {
        return LastForceUpdateVersion;
    }

    public void setLastForceUpdateVersion(String lastForceUpdateVersion) {
        this.LastForceUpdateVersion = lastForceUpdateVersion;
    }

    public String getUpdateDes() {
        return UpdateDes;
    }

    public void setUpdateDes(String updateDes) {
        this.UpdateDes = updateDes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(NewVersion);
        dest.writeString(ForceUpdate);
        dest.writeString(AppName);
        dest.writeString(UpdateLink);
        dest.writeString(LastForceUpdateVersion);
        dest.writeString(UpdateDes);
        dest.writeString(UpdateVersion);
    }
}
