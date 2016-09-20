package com.ndphu.espresso.gateway.model.command;

import com.google.gson.annotations.SerializedName;

public class GPIOCommand {
	@SerializedName("Pin")
	private int pin;

	@SerializedName("State")
	private PinState state;

	public int getPin() {
		return pin;
	}

	public void setPin(int pin) {
		this.pin = pin;
	}

	public PinState getState() {
		return state;
	}

	public void setState(PinState state) {
		this.state = state;
	}

	public enum PinState {
		HIGH, LOW, TOGGLE, PULLUP, PULLDOWN, PULLOFF
	}
}
