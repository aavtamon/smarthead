package com.pisoft.headcontroller;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;

public abstract class ControllingActivity extends Activity {
	private int operationCode;
	private Map<Integer, OperationListener> operationListeners = new HashMap<Integer, OperationListener>();
	
	public interface OperationListener {
		public void onComplete(Intent result);
		public void onFailed();
	}
	
	
	public void requestOperation(final Intent operationIntent, final OperationListener listener) {
		operationCode++;
		if (operationCode == 1000) {
			operationCode = 0;
		}
		
		operationListeners.put(operationCode, listener);
		
		runOnUiThread(new Runnable() {
			public void run() {
				startActivityForResult(operationIntent, operationCode);
			}
		});
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		OperationListener listener = operationListeners.remove(requestCode);

		if (listener != null) {
			if (resultCode == RESULT_OK) {
				listener.onComplete(data);
			} else {
				listener.onFailed();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void notifyJSCallback(final String callback, final Object params) {
		runOnUiThread(new Runnable() {
			public void run() {
				callJSCallback(callback, params);
			}
		});
	}
	
	
	protected abstract void callJSCallback(final String callback, final Object params);
}
