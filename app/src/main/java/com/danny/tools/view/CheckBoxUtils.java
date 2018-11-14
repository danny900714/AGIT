package com.danny.tools.view;
import android.widget.*;
import android.view.View.*;
import android.view.*;

public class CheckBoxUtils
{
	public static void attachTextViewToCheckBox(final CheckBox checkBox, TextView textView) {
		textView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				boolean isChecked = checkBox.isChecked();
				checkBox.setChecked(!isChecked);
			}
		});
	}
}
