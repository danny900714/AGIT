package com.danny.agit.repository;
import android.support.v4.app.*;
import android.widget.*;
import android.app.Activity;
import com.danny.tools.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.app.Dialog;
import com.danny.agit.*;
import java.util.*;
import org.eclipse.jgit.lib.*;
import com.danny.tools.git.branch.*;
import org.eclipse.jgit.api.errors.*;
import java.io.*;
import android.content.*;

public class BranchDeleteDialog extends DialogFragment
{
	public static final String TAG = BranchDeleteDialog.class.getName();
	public static final String ARG_KEY_PATH = "PATH";

	private String argPath;
	private OnReceiveListener listener;

	private Spinner mSpnBranch;

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
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppTheme_AlertDialog_ColorAccent);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_branch_delete, null);

		// init views
		mSpnBranch = view.findViewById(R.id.dialogBranchDeleteSpnBranch);
		builder.setTitle(R.string.delete_branch)
			.setView(view)
			.setPositiveButton(android.R.string.ok, onPositiveButtonClick)
			.setNegativeButton(android.R.string.cancel, null);
		return builder.create();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// init mSpnBranch
		try {
			List<Ref> localBranchList = BranchUtils.getLocalBranches(argPath);
			List<String> localBranchNameList = BranchUtils.getPureBranchList(localBranchList);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, localBranchNameList);
			mSpnBranch.setAdapter(adapter);
		} catch (GitAPIException e) {
			ExceptionUtils.toastException(getContext(), e);
		}
	}

	private DialogInterface.OnClickListener onPositiveButtonClick = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			String sBranchName = mSpnBranch.getSelectedItem().toString();
			listener.onBranchDeleteReceive(sBranchName);
		}
	};

	public interface OnReceiveListener {
		public void onBranchDeleteReceive(String branchName);
	}
}
