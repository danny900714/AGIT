package com.danny.agit.repository;
import android.support.v4.app.*;
import android.app.Dialog;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import com.danny.agit.*;
import android.content.*;
import android.app.Activity;
import com.danny.tools.*;
import android.widget.*;
import java.util.*;
import com.danny.tools.git.remote.*;

public class FetchDialog extends DialogFragment
{
	public static final String TAG = FetchDialog.class.getName();
	public static final String ARG_KEY_PATH = "PATH";
	
	private String argPath;
	private OnReceiveListener listener;
	
	private Spinner mSpnRemote;
	
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
		View view = inflater.inflate(R.layout.dialog_fetch, null);

		// init views
		mSpnRemote = view.findViewById(R.id.dialogFetchSpnRemote);
		
		builder.setTitle(R.string.fetch)
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
				listener.onFetchReceive(sName);
			}
		}
	};
	
	public interface OnReceiveListener {
		public void onFetchReceive(String remoteName);
	}
}
