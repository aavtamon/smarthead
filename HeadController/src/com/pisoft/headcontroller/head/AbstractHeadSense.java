package com.pisoft.headcontroller.head;

import com.pisoft.headcontroller.ControllingActivity;

public abstract class AbstractHeadSense {
	protected final ControllingActivity activity;
	private boolean initialized;
	
	protected AbstractHeadSense(final ControllingActivity activity) {
		this.activity = activity;
	}
	
	protected abstract void init();
	protected abstract void pause();
	protected abstract void destroy();
	
	
	protected void markReady() {
		initialized = true;
	}
	
	protected void markNotReady() {
		initialized = false;
	}

	protected boolean isReady() {
		return initialized;
	}
}
