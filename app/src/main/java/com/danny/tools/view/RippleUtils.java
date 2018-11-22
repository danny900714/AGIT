package com.danny.tools.view;
import android.content.*;
import android.view.*;

public class RippleUtils
{
	public static void bindView(final View targetView, View clickedView) {
		clickedView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				targetView.setPressed(true);
				targetView.invalidate();
				targetView.postDelayed(new Runnable() {
					@Override
					public void run() {
						targetView.setPressed(false);
						targetView.invalidate();
					}
				}, 1500);
			}
		});
	}
}
