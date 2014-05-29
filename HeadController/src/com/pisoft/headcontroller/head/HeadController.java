package com.pisoft.headcontroller.head;

import java.util.Locale;

import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.pisoft.headcontroller.ControllingActivity;

public class HeadController {
	private final ControllingActivity activity;
	
	private final HeadVoice voice;
	private final HeadVisualDirect visual;
	private final HeadHearing hearing;
	private final HeadVision vision;
	private final HeadMotion motion;
	
	public HeadController(final ControllingActivity activity) {
		this.activity = activity;
		
		voice = new HeadVoice(activity);
		visual = new HeadVisualDirect(activity);
		hearing = new HeadHearing(activity);
		vision = new HeadVision(activity);
		motion = new HeadMotion(activity);
	}

	@JavascriptInterface
	public boolean isReady() {
		return voice.isReady() && hearing.isReady() && vision.isReady() && motion.isReady();
	}

	@JavascriptInterface
	public void setLanguage(final String lang) {
		if ("english".equalsIgnoreCase(lang)) {
			voice.setLanguage(Locale.US);
		} else if ("spanish".equalsIgnoreCase(lang)) {
		}
	}

	
	
	@JavascriptInterface
	public boolean communicate(final String text, final String callback) {
		return voice.say(text, new UtteranceProgressListener() {
			public void onStart(String utteranceId) {
				visual.say(text);
			}
			
			public void onError(String utteranceId) {
				visual.stop();
			}
			
			public void onDone(String utteranceId) {
				visual.stop();
				activity.notifyJSCallback(callback, null);
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
	public boolean detectFaces(final String callback) {
		return vision.detectFaces(new HeadVision.OnCompleteListener() {
			public void onComplete(Object data) {
				activity.notifyJSCallback(callback, data);
			}
		});
	}

	@JavascriptInterface
	public boolean rotate(final int degree, final String callback) {
		return motion.rotate(degree, new HeadMotion.OnCompleteListener() {
			public void onComplete() {
				activity.notifyJSCallback(callback, null);
			}
		});
	}
	
	@JavascriptInterface
	public int getCurrentAngle() {
		return motion.getCurrentAngle();
	}
}
