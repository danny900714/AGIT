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
import android.support.design.widget.*;
import java.util.*;
import com.danny.tools.git.branch.*;
import org.eclipse.jgit.api.errors.*;
import android.content.*;
import org.eclipse.jgit.lib.*;
import com.danny.tools.git.repository.*;
import java.io.*;

public class MergeBranchDialog extends DialogFragment
{
	public static final String TAG = MergeBranchDialog.class.getName();
	public static final String ARG_KEY_PATH = "PATH";

	private String argPath;
	private OnReceiveListener listener;
	private List<Ref> localBranchList;
	
	private Spinner mSpnBranch;
	private TextInputLayout mEdtLayCommit;
	private EditText mEdtCommit;
	private CheckBox mChkFastForward;
	private TextView mTxtFastForward;

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
		View view = inflater.inflate(R.layout.dialog_merge_branch, null);
		
		// init views
		mSpnBranch = view.findViewById(R.id.dialogMergeBranchSpnBranch);
		mEdtLayCommit = view.findViewById(R.id.dialogMergeBranchEdtLayCommit);
		mEdtCommit = view.findViewById(R.id.dialogMergeBranchEdtCommit);
		mChkFastForward = view.findViewById(R.id.dialogMergeBranchChkFastForward);
		mTxtFastForward = view.findViewById(R.id.dialogMergeBranchTxtFastForward);
		
		builder.setTitle(R.string.merge_branch)
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
			// remove the current branch
			Repository repository = RepositoryUtils.openRepository(argPath);
			String currentBranch = repository.getBranch();
			
			localBranchList = BranchUtils.getLocalBranches(argPath);
			List<String> localBranchNameList = BranchUtils.getPureBranchList(localBranchList);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, localBranchNameList);
			mSpnBranch.setAdapter(adapter);
		} catch (GitAPIException e) {
			ExceptionUtils.toastException(getContext(), e);
		} catch (IOException e) {
			ExceptionUtils.toastException(getContext(), e);
		}
		
		// init mChkFastForward
		CheckBoxUtils.attachTextViewToCheckBox(mChkFastForward, mTxtFastForward);
	}
	
	private DialogInterface.OnClickListener onPositiveButtonClick = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			int position = mSpnBranch.getSelectedItemPosition();
			Ref branch = localBranchList.get(position);
			String sCommitMessage = mEdtCommit.getText().toString();
			boolean isFastForward = mChkFastForward.isChecked();
			listener.onMergeBranchReceive(branch, sCommitMessage, isFastForward);
		}
	};

	public interface OnReceiveListener {
		public void onMergeBranchReceive(Ref branch, String commitMessage, boolean isFastForward);
	}
}
