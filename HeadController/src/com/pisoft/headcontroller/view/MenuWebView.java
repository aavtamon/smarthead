package com.pisoft.headcontroller.view;

import android.content.Context;
import android.net.http.SslError;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MenuWebView extends WebView {
	private static final String TAG = "MenuView";
	
    public class PortalClient extends WebViewClient {
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        	Log.w(TAG, "WebView received an error: " + description + ", error code = " + errorCode);
        }
        
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            Log.w(TAG, "WebView received an SSL error: " + error);
        	handler.proceed();
        }
    }
    
    private static class PortalViewChromeClient extends WebChromeClient {
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        	Log.d(TAG, "Console log: file:" + consoleMessage.sourceId() + ":" + consoleMessage.lineNumber() + " " + consoleMessage.message());
            return true;
		}

        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.d(TAG, "Alert " + message);
            result.confirm();
            return true;
        }

        public boolean onJsTimeout() {
            Log.w(TAG, "Long running javascript...");
            return false; // Don't interrupt script
        }
    }
    
	
	
	public MenuWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
        setFocusable(true);
        setFocusableInTouchMode(true);
        
        getSettings().setJavaScriptEnabled(true);
        getSettings().setDomStorageEnabled(true);
        getSettings().setSupportZoom(false);
//        getSettings().setAllowUniversalAccessFromFileURLs(true);
        getSettings().setDomStorageEnabled(true);

       
        setWebViewClient(new PortalClient());
        setWebChromeClient(new PortalViewChromeClient());
	}
}
