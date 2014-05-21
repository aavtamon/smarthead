package com.pisoft.headcontroller.head;

import java.util.Locale;

import android.speech.tts.UtteranceProgressListener;
import android.webkit.JavascriptInterface;

import com.pisoft.headcontroller.ControllingActivity;

public class HeadController {
	private final ControllingActivity activity;
	
	private final HeadVoice voice;
	private final HeadVisual visual;
	private final HeadHearing hearing;
	private final HeadVision vision;
	
	public HeadController(final ControllingActivity activity) {
		this.activity = activity;
		
		voice = new HeadVoice(activity);
		visual = new HeadVisual(activity);
		hearing = new HeadHearing(activity);
		vision = new HeadVision(activity);
	}

	@JavascriptInterface
	public boolean isReady() {
		return voice.isReady() && hearing.isReady() && vision.isReady();
	}

	@JavascriptInterface
	public void setLanguage(final String lang) {
		if ("english".equalsIgnoreCase(lang)) {
			voice.setLanguage(Locale.US);
		} else if ("spanish".equalsIgnoreCase(lang)) {
		}
	}

	
	
	@JavascriptInterface
	public boolean communicate(final String text) {
		return voice.say(text, new UtteranceProgressListener() {
			public void onStart(String utteranceId) {
				visual.say(text);
			}
			
			public void onError(String utteranceId) {
				visual.stop();
			}
			
			public void onDone(String utteranceId) {
				visual.stop();
			}
		});
	}
	
	@JavascriptInterface
	public boolean listen(final String callback) {
		return hearing.listen(new HeadHearing.OnCompleteListener() {
			public void onComplete(String text) {
				activity.notifyJSCallback(callback, text);
			}
		});
	}
	
	@JavascriptInterface
	public boolean scanPeople(final String callback) {
		return vision.scanPeople(new HeadVision.OnCompleteListener() {
			public void onComplete(Object data) {
				activity.notifyJSCallback(callback, data);
			}
		});
	}

	@JavascriptInterface
	public boolean lookAtNextPersonClockwise() {
		return false;
	}

	@JavascriptInterface
	public boolean lookAtNextPersonAntiClockwise() {
		return false;
	}
	
	
}
