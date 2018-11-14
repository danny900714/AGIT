package com.danny.agit.repository;
import android.support.v7.app.*;
import android.os.*;
import com.danny.agit.*;
import android.support.v7.widget.Toolbar;
import android.widget.*;
import android.view.View.*;
import android.view.*;
import android.text.*;
import android.support.design.widget.*;
import com.danny.agit.setting.*;
import android.content.*;
import android.graphics.drawable.*;
import android.graphics.*;
import org.eclipse.jgit.lib.*;
import com.danny.tools.git.commit.*;
import com.danny.tools.git.commit.CommitAsyncTask.*;
import android.content.res.*;

public class RepositoryCommitActivity extends AppCompatActivity implements ChoosePeopleDialog.OnPersonChooseListener
{
	public static final String PARAM_KEY_PATH = "PATH";
	
	private String paramPath;
	private ColorStateList defaultColor;
	private String sAuthorName;
	private String sAuthorEmail;
	private String sCommitterName;
	private String sCommitterEmail;
	
	private Toolbar toolbar;
	private ImageView mImgOk, mImgAuthor, mImgCommitter;
	private TextInputLayout mEdtLayMessage;
	private EditText mEdtMessage;
	private LinearLayout mLayAuthor, mLayCommitter;
	private TextView mTxtAuthor, mTxtCommitter;
	private CheckBox mChkAddAll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_repository_commit);
		
		// get params
		Intent it = getIntent();
		Bundle params = it.getExtras();
		paramPath = params.getString(PARAM_KEY_PATH);
		
		// init views
		toolbar = findViewById(R.id.toolbar);
		mImgOk = findViewById(R.id.repositoryCommitImgOk);
		mImgAuthor = findViewById(R.id.repositoryCommitImgAuthor);
		mImgCommitter = findViewById(R.id.repositoryCommitImgCommitter);
		mLayAuthor = findViewById(R.id.repositoryCommitLayAuthor);
		mLayCommitter = findViewById(R.id.repositoryCommitLayCommitter);
		mTxtAuthor = findViewById(R.id.repositoryCommitTxtAuthor);
		mTxtCommitter = findViewById(R.id.repositoryCommitTxtCommitter);
		mEdtLayMessage = findViewById(R.id.repositoryCommitEdtLayMessage);
		mEdtMessage = findViewById(R.id.repositoryCommitEdtMessage);
		mChkAddAll = findViewById(R.id.repositoryCommitChkAddAll);
		
		// init toolbar
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(R.string.commit);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
		
		// get default color
		defaultColor = mTxtAuthor.getTextColors();
		
		// init listener
		mImgOk.setOnClickListener(onImgOkClick);
		mLayAuthor.setOnClickListener(onLayAuthorClick);
		mLayCommitter.setOnClickListener(onLayCommitterClick);
	}

	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return false;
	}

	@Override
	public void onPersonChoose(int peopleType, Drawable profile, String name, String email) {
		if (peopleType == ChoosePeopleDialog.ARG_PEOPLE_AUTHOR) {
			sAuthorName = name;
			sAuthorEmail = email;
			mTxtAuthor.setText(sAuthorName);
		} else if (peopleType == ChoosePeopleDialog.ARG_PEOPLE_COMMITTER) {
			sCommitterName = name;
			sCommitterEmail = email;
			mTxtCommitter.setText(sCommitterName);
		}
	}
	
	private View.OnClickListener onImgOkClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (authUserInput()) {
				if (sAuthorName == null) {
					mTxtAuthor.setTextColor(Color.RED);
					return;
				}
				
				mTxtAuthor.setTextColor(defaultColor);
				String sMessage = mEdtMessage.getText().toString();
				boolean isAddAll = mChkAddAll.isChecked();
				
				// git commit
				if (sCommitterName == null) {
					PersonIdent author = new PersonIdent(sAuthorName, sAuthorEmail);
					CommitAsyncTask commitTask = new CommitAsyncTask(RepositoryCommitActivity.this);
					commitTask.setOnTaskFinishListener(onCommitFinish);
					CommitAsyncTask.Param param = new CommitAsyncTask.Param(isAddAll, paramPath, author, author, sMessage);
					commitTask.execute(new CommitAsyncTask.Param[] {param});
				} else {
					PersonIdent author = new PersonIdent(sAuthorName, sAuthorEmail);
					PersonIdent committer = new PersonIdent(sCommitterName, sCommitterEmail);
					CommitAsyncTask commitTask = new CommitAsyncTask(RepositoryCommitActivity.this);
					commitTask.setOnTaskFinishListener(onCommitFinish);
					CommitAsyncTask.Param param = new CommitAsyncTask.Param(isAddAll, paramPath, author, committer, sMessage);
					commitTask.execute(new CommitAsyncTask.Param[] {param});
				}
			}
		}
	};
	
	private View.OnClickListener onLayAuthorClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			ChoosePeopleDialog choosePeopleDialog = new ChoosePeopleDialog();
			Bundle args = new Bundle();
			args.putInt(ChoosePeopleDialog.ARG_KEY_PEOPLE_TYPE, ChoosePeopleDialog.ARG_PEOPLE_AUTHOR);
			choosePeopleDialog.setArguments(args);
			choosePeopleDialog.show(getSupportFragmentManager(), ChoosePeopleDialog.TAG);
		}
	};
	
	private View.OnClickListener onLayCommitterClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			ChoosePeopleDialog choosePeopleDialog = new ChoosePeopleDialog();
			Bundle args = new Bundle();
			args.putInt(ChoosePeopleDialog.ARG_KEY_PEOPLE_TYPE, ChoosePeopleDialog.ARG_PEOPLE_COMMITTER);
			choosePeopleDialog.setArguments(args);
			choosePeopleDialog.show(getSupportFragmentManager(), ChoosePeopleDialog.TAG);
		}
	};
	
	private CommitAsyncTask.OnTaskFinishListener onCommitFinish = new CommitAsyncTask.OnTaskFinishListener() {
		@Override
		public void onTaskFinish(CommitAsyncTask.Result result) {
			if (result.isSuccess()) {
				Toast.makeText(RepositoryCommitActivity.this, R.string.commit_success, Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	};
	
	private boolean authUserInput() {
		boolean isValid = true;
		
		String sMessage = mEdtMessage.getText().toString();
		
		if (TextUtils.isEmpty(sMessage)) {
			mEdtLayMessage.setError(getString(R.string.edt_blank_err));
			isValid = false;
		} else {
			mEdtLayMessage.setError(null);
			mEdtLayMessage.setErrorEnabled(false);
		}
		
		return isValid;
	}
}
