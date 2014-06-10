package com.pisoft.headcontroller.head;

import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.speech.RecognizerIntent;

import com.pisoft.headcontroller.ControllingActivity;

public class HeadHearing extends AbstractHeadSense {
	private final Intent recognizeIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	private String ietfLanguage;
	
	public interface OnCompleteListener {
		public void onComplete(String text);
	}
	
	public HeadHearing(final ControllingActivity activity) {
		super(activity);
		
		if (recognizeIntent.resolveActivity(activity.getPackageManager()) != null) {
			markReady();
		}
	}
	
	protected void init() {
		setLanguage(Locale.US);
	}

	protected void pause() {
	}
	
	protected void destroy() {
	}

	public void setLanguage(final Locale language) {
		if (language == Locale.US) {
			ietfLanguage = "en-US";
		}
	}
	
	public boolean listen(final OnCompleteListener listener) {
		if (!isReady()) {
			return false;
		}
		
		recognizeIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, ietfLanguage);
		recognizeIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		recognizeIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "I am listening...");

		activity.requestOperation(recognizeIntent, new ControllingActivity.OperationListener() {
			public void onFailed() {
				listener.onComplete(null);
			}
			
			public void onComplete(Intent result) {
				List<String> matches = result.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				
				String bestMatch = getBestMatch(matches);
				listener.onComplete(bestMatch);
			}
		});

		return true;
	}
	

	private String getBestMatch(final List<String> candidates) {
		if (candidates.size() > 0) {
			return candidates.get(0);
		}
		
		return null;
	}
}
