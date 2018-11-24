package com.danny.agit.repository;
import android.support.v4.app.*;
import android.os.*;
import android.app.Dialog;
import android.support.v7.app.*;
import com.danny.agit.*;
import android.content.*;
import android.support.design.widget.*;
import android.widget.*;
import android.view.*;
import android.app.Activity;
import com.danny.tools.*;
import com.danny.tools.view.*;

public class BranchCreateDialog extends DialogFragment
{
	public static final String TAG = BranchCreateDialog.class.getName();
	
	private OnReceiveListener listener;
	
	private TextInputLayout mEdtLayName;
	private EditText mEdtName;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			listener = (OnReceiveListener) activity;
		} catch (ClassCastException e) {
			ExceptionUtils.toastException(activity, e);
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppTheme_AlertDialog_ColorAccent);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_branch_create, null);
		
		mEdtLayName = view.findViewById(R.id.dialogBranchCreateEdtLayName);
		mEdtName = view.findViewById(R.id.dialogBranchCreateEdtName);
		
		builder.setTitle(R.string.create_branch)
			.setView(view)
			.setPositiveButton(android.R.string.ok, null);
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
			if (EditTextUtils.emptyChecker(getContext(), mEdtLayName)) {
				listener.onBranchCreateReceive(mEdtName.getText().toString());
				dismiss();
			}
		}
	};
	
	public interface OnReceiveListener {
		public void onBranchCreateReceive(String name);
	}
}
