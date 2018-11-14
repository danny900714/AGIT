package com.danny.tools;
import android.widget.*;
import android.content.*;

public class ExceptionUtils
{
	private static final String MESSAGE_ERROR = "An error occured: ";
	
	public static void toastException(Context context, Exception e) {
		Toast.makeText(context, MESSAGE_ERROR + e.getClass().getName(), Toast.LENGTH_LONG).show();
	}
	
	public static void toastException(Context context, Class eClass) {
		Toast.makeText(context, MESSAGE_ERROR + eClass.getName(), Toast.LENGTH_LONG).show();
	}
}
