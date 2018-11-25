package com.danny.agit.repository;
import android.app.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import com.danny.agit.*;
import com.danny.tools.*;
import com.danny.tools.view.*;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class TagCreateDialog extends DialogFragment
{
	public static final String TAG = TagCreateDialog.class.getName();

	private OnReceiveListener listener;

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
		View view = inflater.inflate(R.layout.dialog_tag_create, null);

		builder.setTitle(R.string.create_tag)
			.setView(view)
			.setPositiveButton(android.R.string.ok, null)
			.setNegativeButton(android.R.string.cancel, null);
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
			
		}
	};

	public interface OnReceiveListener {
		public void onTagCreateReceive(String name);
	}
}
