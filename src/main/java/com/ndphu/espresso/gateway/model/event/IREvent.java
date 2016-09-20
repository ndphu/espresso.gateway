package com.ndphu.espresso.gateway.model.event;

import com.google.gson.annotations.SerializedName;

public class IREvent {
	@SerializedName("DeviceId")
	private String deviceId;
	@SerializedName("Event")
	private RemoteButton remoteButton;

	public class RemoteButton {
		@SerializedName("Code")
		private int code;
		@SerializedName("Repeat")
		private int repeat;
		@SerializedName("Button")
		private String button;
		@SerializedName("Remote")
		private String remote;

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public int getRepeat() {
			return repeat;
		}

		public void setRepeat(int repeat) {
			this.repeat = repeat;
		}

		public String getButton() {
			return button;
		}

		public void setButton(String button) {
			this.button = button;
		}

		public String getRemote() {
			return remote;
		}

		public void setRemote(String remote) {
			this.remote = remote;
		}
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public RemoteButton getRemoteButton() {
		return remoteButton;
	}

	public void setRemoteButton(RemoteButton remoteButton) {
		this.remoteButton = remoteButton;
	}

}
