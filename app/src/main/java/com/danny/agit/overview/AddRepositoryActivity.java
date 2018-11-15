package com.danny.agit.overview;
import android.support.v7.app.*;
import android.os.*;
import com.danny.agit.*;
import android.support.v7.widget.Toolbar;
import android.content.*;
import android.widget.*;
import android.view.View.*;
import android.view.*;
import android.util.*;
import java.io.*;
import android.database.*;
import android.net.*;
import android.provider.*;
import android.annotation.*;
import java.net.*;
import com.danny.tools.*;
import android.text.*;
import android.content.res.*;
import android.support.design.widget.*;
import org.eclipse.jgit.storage.file.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import com.danny.tools.data.repository.*;
import com.danny.tools.git.commit.*;
import android.support.constraint.ConstraintLayout;
import com.danny.agit.repository.*;
import com.danny.tools.git.gitignore.*;

public class AddRepositoryActivity extends AppCompatActivity implements AddLanguageDialog.OnReceiveListener
{
	public static final int REQUEST_CODE = 1;
	public static final int RETURN_CODE_CLONE = 3;
	public static final int RETURN_CODE_CREATE = 4;
	public static final int RETURN_CODE_IMPORT = 5;
	public static final String KEY_NAME = "KEY_NAME";
	public static final String KEY_PATH = "KEY_PATH";
	public static final String KEY_URL = "KEY_URL";
	public static final String FRAGMENT_DIRECTORY_CHOOSER = "directory chooser";
	private static final int REQUEST_FILE_CHOOSER = 2;
	private int action;
	
	private RepositoryRecordDao recordDao;
	private String sLanguage;
	
	private ImageView mImgOk;
	private TextInputEditText mEdtName, mEdtLocation, mEdtUrl;
	private TextInputLayout mEdtLayName, mEdtLayLocation, mEdtLayUrl;
	private Button mBtnBrowse;
	private ConstraintLayout mLayLanguage;
	private TextView mTxtLanguage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_repository);
		
		// get extra
		Intent it = getIntent();
		action = it.getIntExtra("action", 0);
		
		// init toolbar
		Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		if (action != 0)
			actionBar.setTitle(action);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
		
		// init views
		mImgOk = findViewById(R.id.imgAddRepositorySave);
		mEdtLayName = findViewById(R.id.addRepositoryEdtLayName);
		mEdtLayLocation = findViewById(R.id.addRepositoryEdtLayLocation);
		mEdtLayUrl = findViewById(R.id.addRepositoryEdtLayUrl);
		mEdtName = findViewById(R.id.addRepositoryEdtName);
		mEdtLocation = findViewById(R.id.addRepositoryEdtLocation);
		mEdtUrl = findViewById(R.id.addRepositoryEdtUrl);
		mBtnBrowse = findViewById(R.id.addRepositoryBtnBrowse);
		mLayLanguage = findViewById(R.id.addRepositoryLayLanguage);
		mTxtLanguage = findViewById(R.id.addRepositoryTxtLanguage);
		
		// init visibilities
		if (action == R.string.clone_repository)
			mEdtLayUrl.setVisibility(View.VISIBLE);
		if (action == R.string.create_repository) {
			mLayLanguage.setVisibility(View.VISIBLE);
			String rawText = getString(R.string.add_gitignore);
			mTxtLanguage.setText(Html.fromHtml(rawText + "<b>" + getString(R.string.none) + "</b>"));
		}
		
		// init listeners
		mImgOk.setOnClickListener(onImgDoneClick);
		mBtnBrowse.setOnClickListener(onBtnBrowseClick);
		mLayLanguage.setOnClickListener(onLayLanguageClick);
		
		// init db
		recordDao = new RepositoryRecordDao(AddRepositoryActivity.this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO: Implement this method
		switch(requestCode) {
			case REQUEST_FILE_CHOOSER:
				if (data == null) {
					break;
				}
				
				String realPath = AndroidFileUtils.getPathFromUri(data.getData());
				if (realPath == null) {
					ExceptionUtils.toastException(this, NullPointerException.class);
					break;
				} 
				if (realPath.equals(AndroidFileUtils.ERROR_NOT_HANDLEABLE)) {
					Toast.makeText(this, R.string.file_not_support, Toast.LENGTH_LONG).show();
					break;
				}
				mEdtLocation.setText(realPath);
		}
		
	}

	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return false;
	}

	@Override
	public void onLanguageReceive(String language) {
		this.sLanguage = language;
		String sRawText = getString(R.string.add_gitignore);
		mTxtLanguage.setText(Html.fromHtml(sRawText + "<b>" + language + "</b>"));
	}
	
	private View.OnClickListener onImgDoneClick = new View.OnClickListener(){
		@Override
		public void onClick(View p1) {
			if (authUserInput()) {
				String sName = mEdtName.getText().toString();
				String sLocation = mEdtLocation.getText().toString();
				if (action == R.string.clone_repository) {
					String sUrl = mEdtUrl.getText().toString();
					CloneAsyncTask.Params param = new CloneAsyncTask.Params(sName, sLocation, sUrl);
					new CloneAsyncTask(AddRepositoryActivity.this).execute(param);
				} else if (action == R.string.create_repository) {
					createRepository(sName, sLocation);
					if (sLanguage != null)
						addGitignore(sLocation);
					finish();
				} else if (action == R.string.import_repository) {
					addRecordToDb(sName, sLocation);
					finish();
				}
			}
		}
	};
	
	private View.OnClickListener onBtnBrowseClick = new View.OnClickListener(){
		@Override
		public void onClick(View p1) {
			Intent it = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
			it.addCategory(Intent.CATEGORY_DEFAULT);
			startActivityForResult(Intent.createChooser(it, "Choose Directory"), REQUEST_FILE_CHOOSER);
		}
	};
	
	private View.OnClickListener onLayLanguageClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			AddLanguageDialog languageDialog = new AddLanguageDialog();
			languageDialog.show(getSupportFragmentManager(), AddLanguageDialog.TAG);
		}
	};
	
	private boolean authUserInput() {
		Resources res = getResources();
		boolean isLegal = true;
		
		String sName = mEdtName.getText().toString();
		String sPath = mEdtLocation.getText().toString();
		
		if (TextUtils.isEmpty(sName)) {
			mEdtLayName.setError(res.getString(R.string.edt_blank_err));
			isLegal = false;
		} else 
			mEdtLayName.setError(null);
		
		if (TextUtils.isEmpty(sPath)) {
			mEdtLayLocation.setError(res.getString(R.string.edt_blank_err));
			isLegal = false;
		} else 
			mEdtLayLocation.setError(null);
		
		if (action == R.string.clone_repository) {
			String sUrl = mEdtUrl.getText().toString();
			if (TextUtils.isEmpty(sUrl)) {
				mEdtLayUrl.setError(res.getString(R.string.edt_blank_err));
				isLegal = false;
			} else 
				mEdtLayUrl.setError(null);
		}
		
		if (!isLegal)
			return false;
		
		return true;
	}
	
	private void createRepository(final String sName, final String sPath) {
		InitAsyncTask initTask = new InitAsyncTask(AddRepositoryActivity.this);
		initTask.setOnTaskFinishListener(new InitAsyncTask.onTaskFinishListener() {
			@Override
			public void onTaskFinish(boolean isSuccess) {
				if (isSuccess)
					addRecordToDb(sName, sPath);
			}
			
		});
		initTask.execute(new String[]{sPath});
	}
	
	private void addRecordToDb(String sName, String sPath) {
		RepositoryRecord record = new RepositoryRecord(sName, sPath);
		recordDao.insert(record);
	}
	
	private void addGitignore(String path) {
		GitignoreManager manager = new GitignoreManager(AddRepositoryActivity.this);
		File dest = new File(path + File.separator + GitignoreManager.EXTENTION);

		if (dest.exists()) {
			Toast.makeText(AddRepositoryActivity.this, R.string.gitignore_exists, Toast.LENGTH_LONG).show();
			return;
		}

		manager.copyGitignoreTo(sLanguage, dest.toString());
	}
}
