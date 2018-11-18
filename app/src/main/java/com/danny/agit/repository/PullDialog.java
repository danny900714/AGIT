package com.danny.agit.repository;
import android.support.v4.app.*;
import android.app.Activity;
import com.danny.tools.*;
import android.widget.*;
import android.os.*;
import android.app.Dialog;
import android.support.v7.app.*;
import android.view.*;
import com.danny.agit.*;
import java.util.*;
import com.danny.tools.git.remote.*;
import com.danny.tools.view.*;
import android.content.*;

public class PullDialog extends DialogFragment
{
	public static final String TAG = PullDialog.class.getName();
	public static final String ARG_KEY_PATH = "PATH";

	private String argPath;
	private OnReceiveListener listener;

	private Spinner mSpnRemote;
	private CheckBox mChkRebase;
	private TextView mTxtRebase;

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
		View view = inflater.inflate(R.layout.dialog_pull, null);

		// init views
		mSpnRemote = view.findViewById(R.id.dialogPullSpnRemote);
		mChkRebase = view.findViewById(R.id.dialogPullChkRebase);
		mTxtRebase = view.findViewById(R.id.dialogPullTxtRebase);

		builder.setTitle(R.string.pull)
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
		CheckBoxUtils.attachTextViewToCheckBox(mChkRebase, mTxtRebase);
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
				boolean isRebase = mChkRebase.isChecked();
				listener.onPullReceive(sName, isRebase);
			}
		}
	};

	public interface OnReceiveListener {
		public void onPullReceive(String name, boolean isRebase);
	}
}
