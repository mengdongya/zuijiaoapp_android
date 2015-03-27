package com.zuijiao.entity;

import com.zuijiao.controller.ThirdPartySdkManager;

import android.graphics.Bitmap;

public class ZuiJiaoUser {
	private static ZuiJiaoUser mUser = null;

	private String userName = null;
	private Bitmap head = null;
	private int cloudType = ThirdPartySdkManager.CLOUD_TYPE_NONE;

	private ZuiJiaoUser() {

	}

	public static ZuiJiaoUser getZuiJiaoUser() {
		if (mUser == null) {
			mUser = new ZuiJiaoUser();
		}
		return mUser;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Bitmap getHead() {
		return head;
	}

	public void setHead(Bitmap head) {
		this.head = head;
	}

	public int getCloudType() {
		return cloudType;
	}

	public void setCloudType(int cloudType) {
		this.cloudType = cloudType;
	}
}
