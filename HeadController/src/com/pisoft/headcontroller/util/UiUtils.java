package com.pisoft.headcontroller.util;

import android.app.Activity;
import android.util.TypedValue;

public class UiUtils {
	public static int convertToDip(final Activity activity, final int px) {
		return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, activity.getResources().getDisplayMetrics());
	}
}
