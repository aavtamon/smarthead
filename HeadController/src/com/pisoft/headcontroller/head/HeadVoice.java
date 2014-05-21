package com.pisoft.headcontroller.head;

import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

public class HeadVoice {
	public interface OnCompleteListener {
		public void onComplete();
	}
	
	private TextToSpeech tts; 
	private boolean initialized = false;
	private HashMap<String, String> speechParams;
	
	
	public HeadVoice(final Activity activity) {
		tts = new TextToSpeech(activity.getApplicationContext(), new TextToSpeech.OnInitListener() {
			public void onInit(int status) {
		         if (status == TextToSpeech.SUCCESS) {
		             tts.setLanguage(Locale.US);
		             initialized = true;
		             
		             speechParams = new HashMap<String, String>();
		             speechParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "any");             
		             
		             Log.d("HeadVoice", "Speech engine successfully ionitialized");
		         } else {
		        	 Log.e("HeadVoice", "Error in speech engine init");
		         }
			}
		});
	}
	
	public void setLanguage(final Locale language) {
		tts.setLanguage(language);
	}
	
	public boolean isReady() {
		return initialized;
	}
	
	public boolean say(final String text, final UtteranceProgressListener listener) {
		if (!isReady()) {
			return false;
		}
		
		tts.setOnUtteranceProgressListener(listener);
		tts.speak(text, TextToSpeech.QUEUE_FLUSH, speechParams);
		
		return true;
	}
	
	public boolean stop() {
		if (!isReady()) {
			return false;
		}

		tts.stop();
		return true;
	}
}
