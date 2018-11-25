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
import android.widget.RadioGroup.*;
import com.danny.agit.setting.*;
import android.graphics.drawable.*;
import org.eclipse.jgit.lib.*;

public class TagCreateDialog extends DialogFragment
{
	public static final String TAG = TagCreateDialog.class.getName();
	
	private OnReceiveListener listener;
	private String sTaggerName;
	private String sTaggerEmail;
	
	private TextInputLayout mEdtLayName, mEdtLayMessage;
	private EditText mEdtName, mEdtMessage;
	private LinearLayout mLayTagger;
	private CheckBox mChkAnnotated;
	private TextView mTxtAnnotated, mTxtTagger;

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
		
		// init views
		mEdtLayName = view.findViewById(R.id.dialogTagCreateEdtLayName);
		mEdtLayMessage = view.findViewById(R.id.dialogTagCreateEdtLayMessage);
		mEdtName = view.findViewById(R.id.dialogTagCreateEdtName);
		mEdtMessage = view.findViewById(R.id.dialogTagCreateEdtMessage);
		mLayTagger = view.findViewById(R.id.dialogTagCreateLayTagger);
		mChkAnnotated = view.findViewById(R.id.dialogTagCreateChkAnnotated);
		mTxtAnnotated = view.findViewById(R.id.dialogTagCreateTxtAnnotated);
		mTxtTagger = view.findViewById(R.id.dialogTagCreateTxtTagger);

		builder.setTitle(R.string.create_tag)
			.setView(view)
			.setPositiveButton(android.R.string.ok, null)
			.setNegativeButton(android.R.string.cancel, null);
		return builder.create();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// init checkbox sysytem
		CheckBoxUtils.attachTextViewToCheckBox(mChkAnnotated, mTxtAnnotated);
		
		// init listeners
		mLayTagger.setOnClickListener(onLayTaggerClick);
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
				String sName = mEdtName.getText().toString();
				String sMessage = mEdtMessage.getText().toString();
				boolean isAnnotated = mChkAnnotated.isChecked();
				PersonIdent tagger = new PersonIdent(sTaggerName, sTaggerEmail);
				listener.onTagCreateReceive(sName, sMessage, tagger, isAnnotated);
				dismiss();
			}
		}
	};
	
	private View.OnClickListener onLayTaggerClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			ChoosePeopleDialog choosePeopleDialog = new ChoosePeopleDialog();
			Bundle args = new Bundle();
			args.putInt(ChoosePeopleDialog.ARG_KEY_PEOPLE_TYPE, ChoosePeopleDialog.ARG_PEOPLE_TAGGER);
			choosePeopleDialog.setArguments(args);
			choosePeopleDialog.setOnPersonChooseListener(onPersonChooseListener);
			choosePeopleDialog.show(getActivity().getSupportFragmentManager(), ChoosePeopleDialog.TAG);
		}
	};
	
	private ChoosePeopleDialog.OnPersonChooseListener onPersonChooseListener = new ChoosePeopleDialog.OnPersonChooseListener() {
		@Override
		public void onPersonChoose(int peopleType, Drawable profile, String name, String email) {
			sTaggerName = name;
			sTaggerEmail = email;
			mTxtTagger.setText(name);
		}
	};

	public interface OnReceiveListener {
		public void onTagCreateReceive(String name, String message, PersonIdent tagger, boolean isAnnotated);
	}
}
