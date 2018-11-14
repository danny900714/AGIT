package com.danny.agit.repository;
import android.support.v4.app.*;
import android.os.*;
import android.app.Dialog;
import android.support.v7.app.*;
import com.danny.agit.*;
import android.content.*;
import android.view.View.*;
import android.view.*;
import android.support.design.widget.*;
import android.widget.*;
import com.danny.tools.view.*;
import android.app.Activity;
import com.danny.tools.*;

public class AuthDialog extends DialogFragment
{
	public static final String TAG = AuthDialog.class.getName();
	
	private OnOkClickListener listener;
	
	private TextInputLayout mEdtLayUsername, mEdtLayPassword;
	private EditText mEdtUsername, mEdtPassword;
	private CheckBox mChkSave, mChkIgnore;
	private TextView mTxtSave, mTxtIgnore;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			listener = (OnOkClickListener) activity;
		} catch (ClassCastException e) {
			ExceptionUtils.toastException(activity, e);
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppTheme_AlertDialog_ColorPrimary);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_auth, null);
		
		// init views
		mEdtLayUsername = view.findViewById(R.id.dialogAuthEdtLayUsername);
		mEdtLayPassword = view.findViewById(R.id.dialogAuthEdtLayPassword);
		mEdtUsername = view.findViewById(R.id.dialogAuthEdtUsername);
		mEdtPassword = view.findViewById(R.id.dialogAuthEdtPassword);
		mChkSave = view.findViewById(R.id.dialogAuthChkSave);
		mChkIgnore = view.findViewById(R.id.dialogAuthChkIgnore);
		mTxtSave = view.findViewById(R.id.dialogAuthTxtSave);
		mTxtIgnore = view.findViewById(R.id.dialogAuthTxtIgnore);
		
		builder.setTitle(R.string.auth)
			.setPositiveButton(android.R.string.ok, onButtonClick)
			.setNegativeButton(android.R.string.cancel, onButtonClick)
			.setView(view);
		
		return builder.create();
	}

	@Override
	public void onResume() {
		super.onResume();
		final AlertDialog dialog = (AlertDialog)getDialog();
		if (dialog != null) {
			Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
			positiveButton.setOnClickListener(onPositiveButtonClick);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// init check box systems
		CheckBoxUtils.attachTextViewToCheckBox(mChkSave, mTxtSave);
		CheckBoxUtils.attachTextViewToCheckBox(mChkIgnore, mTxtIgnore);
		
		// init listeners
		mChkIgnore.setOnCheckedChangeListener(onChkIgnoreChange);
	}
	
	private View.OnClickListener onPositiveButtonClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (!mChkIgnore.isChecked()) {
				if (EditTextUtils.emptyChecker(getContext(), mEdtLayUsername) & EditTextUtils.emptyChecker(getContext(), mEdtLayPassword)) {
					String sUsername = mEdtUsername.getText().toString();
					String sPassword = mEdtPassword.getText().toString();
					boolean isSaved = mChkSave.isChecked();
					boolean isIgnored = mChkIgnore.isChecked();
					listener.onGetPushAuthData(sUsername, sPassword, isSaved, isIgnored);
					dismiss();
				}
			} else {
				boolean isSaved = mChkSave.isChecked();
				boolean isIgnored = mChkIgnore.isChecked();
				listener.onGetPushAuthData(null, null, isSaved, isIgnored);
				dismiss();
			}
		}
	};
	
	private DialogInterface.OnClickListener onButtonClick = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			
		}
	};
	
	private CompoundButton.OnCheckedChangeListener onChkIgnoreChange = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				mEdtLayUsername.setEnabled(false);
				mEdtLayPassword.setEnabled(false);
			} else {
				mEdtLayUsername.setEnabled(true);
				mEdtLayPassword.setEnabled(true);
			}
		}
	};
	
	public interface OnOkClickListener {
		public void onGetPushAuthData(String username, String password, boolean isSaved, boolean isIgnored);
	}
}
