package com.danny.agit.repository;
import android.support.v4.app.*;
import android.app.Dialog;
import android.support.v7.app.*;
import android.os.*;
import android.view.*;
import com.danny.agit.*;
import android.widget.*;
import android.content.*;
import android.support.design.widget.*;
import android.app.Activity;
import com.danny.tools.*;
import com.danny.tools.view.*;

public class AddRemoteDialog extends DialogFragment
{
	public static final String TAG = AddRemoteDialog.class.getName();
	
	private OnOkClickListener listener;
	
	private TextInputLayout mEdtLayName, mEdtLayUrl;
	private EditText mEdtName, mEdtUrl;

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
		View view = inflater.inflate(R.layout.dialog_add_remote, null);

		// init views
		mEdtLayName = view.findViewById(R.id.dialogAddRemoteEdtLayName);
		mEdtLayUrl = view.findViewById(R.id.dialogAddRemoteEdtLayUrl);
		mEdtName = view.findViewById(R.id.dialogAddRemoteEdtName);
		mEdtUrl = view.findViewById(R.id.dialogAddRemoteEdtUrl);

		builder.setTitle(R.string.add_remote)
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
	
	private View.OnClickListener onPositiveButtonClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (EditTextUtils.emptyChecker(getContext(), mEdtLayName) & EditTextUtils.emptyChecker(getContext(), mEdtLayUrl)) {
				String sName = mEdtName.getText().toString();
				String sUrl = mEdtUrl.getText().toString();
				listener.onGetAddRemote(sName, sUrl);
				dismiss();
			}
		}
	};

	private DialogInterface.OnClickListener onButtonClick = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {}
	};
	
	public interface OnOkClickListener {
		public void onGetAddRemote(String name, String url);
	}
}
