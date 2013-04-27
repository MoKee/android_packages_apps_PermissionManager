package com.mokee.permissionsmanager.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class AppInfoDomain implements Parcelable{
	/**
	 * 
	 */
	public String appName;
	public Drawable icon;
	public int totalPermission;
	public int enabledNum;
	public int disabledNum;
	public PackageInfo packageInfo;
	public List<String> revokedPerList;

	public PackageInfo getPackageInfo() {
		return packageInfo;
	}

	public String getAppName() {
		return appName;
	}

	public Drawable getIcon() {
		return icon;
	}

	public List<String> getRevokedPerList() {
		return revokedPerList;
	}

	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeList(revokedPerList);
		dest.writeValue(packageInfo);
		dest.writeInt(disabledNum);
		dest.writeInt(enabledNum);
	}
	public int getTotalPermission() {
		return totalPermission;
	}

	public int getEnabledNum() {
		return enabledNum;
	}

	public int getDisabledNum() {
		return disabledNum;
	}
	public static final Parcelable.Creator<AppInfoDomain> CREATOR=new Creator<AppInfoDomain>() {
		
		@Override
		public AppInfoDomain[] newArray(int size) {
			
			return null;
		}
		
		@Override
		public AppInfoDomain createFromParcel(Parcel source) {
			AppInfoDomain aid=new AppInfoDomain();
			aid.revokedPerList=source.readArrayList(ArrayList.class.getClassLoader());
			aid.packageInfo=(PackageInfo) source.readValue(PackageInfo.class.getClassLoader());
			aid.enabledNum=source.readInt();
			aid.disabledNum=source.readInt();
			return aid;
		}
	};
}
