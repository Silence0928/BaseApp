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
     * newVersion : 0.4.2
     * forceUpdate : 1
     * appName : TMSApp
     * updateLink : http://webapi.i-tms.cn/updateapp/tmsapp/i-tms.apk
     * lastForceUpdateVer : 0
     * updateDesc : 1.加入添加合作车辆；\r\n2.优化性能
     * updateVersion: 1.4.4
     */

    private String newVersion;
    private String forceUpdate;
    private String appName;
    private String updateLink;
    private String lastForceUpdateVer;
    private String updateDesc;
    private String updateVersion;

    public UpdateBean() {
        super();
    }

    protected UpdateBean(Parcel in) {
        newVersion = in.readString();
        forceUpdate = in.readString();
        appName = in.readString();
        updateLink = in.readString();
        lastForceUpdateVer = in.readString();
        updateDesc = in.readString();
        updateVersion = in.readString();
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
        return updateVersion;
    }

    public void setUpdateVersion(String updateVersion) {
        this.updateVersion = updateVersion;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    public String getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(String forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getUpdateLink() {
        return updateLink;
    }

    public void setUpdateLink(String updateLink) {
        this.updateLink = updateLink;
    }

    public String getLastForceUpdateVer() {
        return lastForceUpdateVer;
    }

    public void setLastForceUpdateVer(String lastForceUpdateVer) {
        this.lastForceUpdateVer = lastForceUpdateVer;
    }

    public String getUpdateDesc() {
        return updateDesc;
    }

    public void setUpdateDesc(String updateDesc) {
        this.updateDesc = updateDesc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(newVersion);
        dest.writeString(forceUpdate);
        dest.writeString(appName);
        dest.writeString(updateLink);
        dest.writeString(lastForceUpdateVer);
        dest.writeString(updateDesc);
        dest.writeString(updateVersion);
    }
}
