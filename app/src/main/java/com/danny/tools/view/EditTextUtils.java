package com.danny.tools.view;
import android.support.design.widget.*;
import android.widget.*;
import android.text.*;
import java.lang.annotation.*;
import android.content.*;
import com.danny.agit.*;

public class EditTextUtils
{
	private Context context;
	
	public EditTextUtils(Context context) {
		this.context = context;
	}
	
	public boolean emptyChecker(TextInputLayout textInputLayout) {
		return emptyChecker(context, textInputLayout);
	}
	
	/** Check if the given TextInputLayout's EditText's text is empty.
	* If it is empty, we will set empty error message to it and return {@code false} since it is not valid.
	* On the contrary, if it isn't empty, we will return {@code true}
	*
	* @param context
	* 	The context of the given TextInputLayout attached to
	*
	* @param textInputLayout
	* 	The layout you want to check
	*
	* @return {@code true} if it is not empty, 
	* 	{@code false} if it is empty
	*/
	public static boolean emptyChecker(Context context, TextInputLayout textInputLayout) {
		EditText edt = textInputLayout.getEditText();
		String sContent = edt.getText().toString();
		
		if (TextUtils.isEmpty(sContent)) {
			textInputLayout.setErrorEnabled(true);
			textInputLayout.setError(context.getString(R.string.edt_blank_err));
			return false;
		} else {
			textInputLayout.setErrorEnabled(false);
			textInputLayout.setError(null);
			return true;
		}
	}
}
