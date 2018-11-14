package com.danny.agit.repository;
import android.support.v4.app.*;
import android.os.*;
import android.app.Dialog;
import android.support.v7.app.*;
import com.danny.agit.*;
import android.content.*;
import android.view.*;
import android.widget.*;
import com.danny.tools.view.*;
import com.danny.tools.git.remote.*;
import java.util.*;
import android.app.Activity;
import com.danny.tools.*;

public class PushDialog extends DialogFragment
{
	public static final String TAG = PushDialog.class.getName();
	public static final String ARG_KEY_PATH = "PATH";
	
	private String argPath;
	private OnOkClickListener listener;
	
	private Spinner mSpnRemote;
	private CheckBox mChkPushAll;
	private TextView mTxtPushAll;

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (getArguments() != null) {
			Bundle args = getArguments();
			argPath = args.getString(ARG_KEY_PATH);
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppTheme_AlertDialog_ColorPrimary);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_push, null);
		
		// init views
		mSpnRemote = view.findViewById(R.id.dialogPushSpnRemote);
		mChkPushAll = view.findViewById(R.id.dialogPushChkPushAll);
		mTxtPushAll = view.findViewById(R.id.dialogPushTxtPushAll);
		
		builder.setTitle(R.string.push)
			.setPositiveButton(android.R.string.ok, onDialogButtonClick)
			.setNegativeButton(android.R.string.cancel, onDialogButtonClick)
			.setView(view);
		
		return builder.create();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// init spinner
		List<String> remoteNameList = RemoteUtils.getAllRemoteUrl(argPath);
		if (remoteNameList.size() == 0) {
			AddRemoteDialog addRemoteDialog = new AddRemoteDialog();
			AppCompatActivity activity = (AppCompatActivity) getActivity();
			addRemoteDialog.show(activity.getSupportFragmentManager(), AddRemoteDialog.TAG);
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, remoteNameList);
		mSpnRemote.setAdapter(adapter);
		
		// init checkbox system
		CheckBoxUtils.attachTextViewToCheckBox(mChkPushAll, mTxtPushAll);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (RemoteUtils.getAllRemoteUrl(argPath).size() == 0)
			dismiss();
	}
	
	private DialogInterface.OnClickListener onDialogButtonClick = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				String sName = mSpnRemote.getSelectedItem().toString();
				boolean isPushAll = mChkPushAll.isChecked();
				listener.onGetPushData(sName, isPushAll);
			}
		}
	};
	
	public interface OnOkClickListener {
		public void onGetPushData(String name, boolean isPushAll);
	}
}
