package com.pisoft.headcontroller.head;

import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;

import com.pisoft.headcontroller.ControllingActivity;

public class HeadHearing {
	private final ControllingActivity activity;
	private boolean initialized;
	
	public interface OnCompleteListener {
		public void onComplete(String text);
	}
	
	public HeadHearing(final ControllingActivity activity) {
		this.activity = activity;
		
		PackageManager pm = activity.getPackageManager();
		List<ResolveInfo> recognizeActivities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		
		initialized = recognizeActivities.size() > 0;
	}
	
	public void setLanguage(final Locale language) {
	}
	
	public boolean listen(final OnCompleteListener listener) {
		if (!isReady()) {
			listener.onComplete(null);
			
			return false;
		}
		
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "I am listening...");

		activity.requestOperation(intent, new ControllingActivity.OperationListener() {
			public void onFailed() {
			}
			
			public void onComplete(Intent result) {
				List<String> matches = result.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				
				String bestMatch = getBestMatch(matches);
				listener.onComplete(bestMatch);
			}
		});

		return true;
	}
	
	public boolean isReady() {
		return initialized;
	}
	
	
	private String getBestMatch(final List<String> candidates) {
		if (candidates.size() > 0) {
			return candidates.get(0);
		}
		
		return null;
	}
}
