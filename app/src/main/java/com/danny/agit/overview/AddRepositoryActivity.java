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

public class AddRepositoryActivity extends AppCompatActivity
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
	
	private ImageView mImgOk;
	private TextInputEditText mEdtName, mEdtLocation, mEdtUrl;
	private TextInputLayout mEdtLayName, mEdtLayLocation, mEdtLayUrl;
	private Button mBtnBrowse;
	
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
		
		// init visibilities
		if (action == R.string.clone_repository)
			mEdtLayUrl.setVisibility(View.VISIBLE);
		
		// init listeners
		mImgOk.setOnClickListener(onImgDoneClick);
		mBtnBrowse.setOnClickListener(onBtnBrowseClick);
		
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
					//addRecordToDb(sName, sLocation);
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
		/*try {
			Git git = Git.init().setDirectory(new File(sPath)).call();
		} catch (GitAPIException e) {
			Toast.makeText(AddRepositoryActivity.this, R.string.err_message, Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (IllegalStateException e) {
			Toast.makeText(AddRepositoryActivity.this, R.string.err_message, Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} finally {
			Toast.makeText(AddRepositoryActivity.this, R.string.create_repository_success, Toast.LENGTH_SHORT).show();
		}*/
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
}
