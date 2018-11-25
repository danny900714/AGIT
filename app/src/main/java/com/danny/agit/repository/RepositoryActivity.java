package com.danny.agit.repository;
import android.support.v7.app.*;
import android.os.*;
import com.danny.agit.*;
import android.content.*;
import android.support.v7.widget.*;
import android.support.v4.app.*;
import android.support.v7.app.ActionBar.*;
import android.support.design.widget.*;
import android.support.v4.view.*;
import android.support.design.widget.TabLayout.*;
import java.util.*;
import android.view.*;
import android.content.res.*;
import android.util.*;
import org.eclipse.jgit.revwalk.*;
import com.danny.tools.git.commit.*;
import org.eclipse.jgit.api.errors.*;
import com.danny.tools.*;
import android.widget.ImageView;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.lib.*;
import com.danny.tools.git.remote.*;
import java.io.*;
import com.danny.tools.git.push.*;
import com.danny.tools.git.push.PushAsyncTask.*;
import android.widget.Toast;
import com.danny.tools.data.auth.*;
import com.danny.tools.git.gitignore.*;
import com.danny.tools.git.fetch.*;
import com.danny.tools.git.fetch.FetchAsyncTask.*;
import com.danny.tools.git.pull.*;
import com.danny.tools.git.branch.*;
import com.danny.tools.git.checkout.*;
import com.danny.tools.git.merge.*;
import com.danny.tools.git.merge.MergeBranchAsyncTask.*;

public class RepositoryActivity extends AppCompatActivity implements PushDialog.OnOkClickListener, AuthDialog.OnOkClickListener, AddRemoteDialog.OnOkClickListener, AddLanguageDialog.OnReceiveListener, FetchDialog.OnReceiveListener, PullDialog.OnReceiveListener, BranchCreateDialog.OnReceiveListener, MergeBranchDialog.OnReceiveListener, BranchDeleteDialog.OnReceiveListener, TagCreateDialog.OnReceiveListener
{
	public static final String PARAM_NAME = "NAME";
	public static final String PARAM_PATH = "PATH";
	
	private String paramName;
	private String paramPath;
	private Operation currentOperation; 
	
	// auth
	private boolean isAuthSaved;
	
	// push
	private String sPushName;
	private boolean isPushAll;
	private String sPushUsername;
	private String sPushPassword;
	private boolean isPushAuthIgnored;
	
	// fetch
	private String sFetchName;
	private String sFetchUsername;
	private String sFetchPassword;
	private boolean isFetchAuthIgnored;
	
	// pull
	private String sPullName;
	private boolean isPullRebase;
	private String sPullUsername;
	private String sPullPassword;
	private boolean isPullAuthIgnored;
	
	// branch create
	private String sBranchCreateName;
	
	private TabLayout mTabLayout;
	private ViewPager mViewPager;
	private RepositoryPagerAdapter adapter;
	private FloatingActionButton mFabCommit;
	private ImageView mImgRemote, mImgBranch;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_repository);
		
		// init views
		mTabLayout = findViewById(R.id.tabLayout);
		mViewPager = findViewById(R.id.repositoryViewPager);
		mFabCommit = findViewById(R.id.repositoryFab);
		mImgRemote = findViewById(R.id.repositoryImgRemote);
		mImgBranch = findViewById(R.id.repositoryImgBranch);
		
		// get extras
		Intent it = getIntent();
		Bundle bundle = it.getExtras();
		paramName = bundle.getString(PARAM_NAME);
		paramPath = bundle.getString(PARAM_PATH);
		
		// init toolbar
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		if (paramName != null)
			actionBar.setTitle(paramName);
		
		// init file fragment
		RepositoryFileFragment fileFragment = new RepositoryFileFragment();
		Bundle args = new Bundle();
		args.putString(RepositoryFileFragment.ARG_PATH, paramPath);
		fileFragment.setArguments(args);
		
		// init commit fragment
		RepositoryCommitFragment commitFragment = new RepositoryCommitFragment();
		Bundle args2 = new Bundle();
		args2.putString(RepositoryCommitFragment.ARG_PATH, paramPath);
		commitFragment.setArguments(args2);
			
		// init view pager
		ArrayList<Fragment> fragmentList = new ArrayList<>();
		fragmentList.add(fileFragment);
		fragmentList.add(commitFragment);
		// get commit count
		int count = 0;
		try {
			List<RevCommit> commitList = LogUtils.getAllLogsInverse(paramPath);
			count = commitList.size();
		} catch (GitAPIException e) {
			ExceptionUtils.toastException(RepositoryActivity.this, e);
		}
		// init title
		ArrayList<String> titleList = new ArrayList<>();
		titleList.add(getString(R.string.repository_tab_title_file));
		titleList.add(getString(R.string.repository_tab_title_commit, count));
		adapter = new RepositoryPagerAdapter(getSupportFragmentManager(), fragmentList);
		adapter.setTitleList(titleList);
		mViewPager.setAdapter(adapter);
		mViewPager.addOnPageChangeListener(onPageChange);
		
		// init tab layout
		mTabLayout.setupWithViewPager(mViewPager);
		
		// init tab listener
		mTabLayout.addOnTabSelectedListener(onTabSelectedListener);
		mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
		
		// init listener
		mFabCommit.setOnClickListener(onFabCommitClick);
		mImgRemote.setOnClickListener(onImgRemoteClick);
		mImgBranch.setOnClickListener(onImgBranchClick);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_repository_file, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// TODO: Implement this method
		int id = item.getItemId();
		
		switch(id) {
			case R.id.database:
				AuthRecordDao authDao = new AuthRecordDao(RepositoryActivity.this);
				authDao.copyDbTo("/storage/emulated/0/AGIT/debug/database");
				return true;
			case R.id.addLanguage:
				AddLanguageDialog languageDialog = new AddLanguageDialog();
				languageDialog.show(getSupportFragmentManager(), AddLanguageDialog.TAG);
				return true;
			case R.id.createTag:
				TagCreateDialog tagCreateDialog = new TagCreateDialog();
				tagCreateDialog.show(getSupportFragmentManager(), TagCreateDialog.TAG);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		int position = mViewPager.getCurrentItem();
		Fragment selectedFragment = adapter.getItem(position);
		if (selectedFragment instanceof RepositoryFileFragment) {
			RepositoryFileFragment fileFragment = (RepositoryFileFragment) selectedFragment;
			if (!fileFragment.onBackPressed())
				super.onBackPressed();
		} else
			super.onBackPressed();
	}

	@Override
	public void onGetAddRemote(String name, String url) {
		if (RemoteUtils.getRemoteUrl(paramPath, name) == null) {
			try {
				RemoteUtils.remoteAdd(paramPath, name, url);
			} catch (IOException e) {
				ExceptionUtils.toastException(RepositoryActivity.this, e);
			}
		}
		
	}

	@Override
	public void onGetPushData(String name, boolean isPushAll) {
		this.sPushName = name;
		this.isPushAll = isPushAll;
		currentOperation = Operation.PUSH;
		
		AuthRecordDao authRecordDao = new AuthRecordDao(RepositoryActivity.this);
		AuthRecord authRecord = authRecordDao.get(paramName, sPushName);
		authRecordDao.close();
		
		if (authRecord == null) {
			AuthDialog authDialog = new AuthDialog();
			authDialog.show(getSupportFragmentManager(), AuthDialog.TAG);
		} else {
			sPushUsername = authRecord.getUserName();
			sPushPassword = authRecord.getPassword();
			isPushAuthIgnored = authRecord.isIgnored();
			push();
		}
	}

	@Override
	public void onGetPushAuthData(String username, String password, boolean isSaved, boolean isIgnored) {
		// set data
		this.isAuthSaved = isSaved;
		
		switch (currentOperation) {
			case PUSH:
				this.sPushUsername = username;
				this.sPushPassword = password;
				isPushAuthIgnored = isIgnored;
				push();
				break;
			case FETCH:
				this.sFetchUsername = username;
				this.sPushPassword = password;
				isFetchAuthIgnored = isIgnored;
				fetch();
				break;
			case PULL:
				sPullUsername = username;
				sPullPassword = password;
				isPullAuthIgnored = isIgnored;
				pull();
				break;
		}
	}

	@Override
	public void onLanguageReceive(String language) {
		GitignoreManager manager = new GitignoreManager(RepositoryActivity.this);
		File dest = new File(paramPath + File.separator + GitignoreManager.EXTENTION);
		
		if (dest.exists()) {
			Toast.makeText(RepositoryActivity.this, R.string.gitignore_exists, Toast.LENGTH_LONG).show();
			return;
		}
		
		manager.copyGitignoreTo(language, dest.toString());
		Toast.makeText(RepositoryActivity.this, R.string.gitignore_successfully_created, Toast.LENGTH_SHORT).show();
		Fragment fragment = adapter.getItem(0);
		if (fragment instanceof RepositoryFileFragment) {
			RepositoryFileFragment fileFragment = (RepositoryFileFragment) fragment;
			fileFragment.refreshFileList();
		}
	}

	@Override
	public void onFetchReceive(String remoteName) {
		sFetchName = remoteName;
		currentOperation = Operation.FETCH;
		
		AuthRecordDao authRecordDao = new AuthRecordDao(RepositoryActivity.this);
		AuthRecord authRecord = authRecordDao.get(paramName, sFetchName);
		authRecordDao.close();

		if (authRecord == null) {
			AuthDialog authDialog = new AuthDialog();
			authDialog.show(getSupportFragmentManager(), AuthDialog.TAG);
		} else {
			sFetchUsername = authRecord.getUserName();
			sFetchPassword = authRecord.getPassword();
			isFetchAuthIgnored = authRecord.isIgnored();
			fetch();
		}
	}

	@Override
	public void onPullReceive(String name, boolean isRebase) {
		sPullName = name;
		isPullRebase = isRebase;
		currentOperation = Operation.PULL;
		
		// check if fetch config exists
		String sFetch = RemoteUtils.getFetch(paramPath, sPullName);
		if (sFetch == null) {
			try {
				RemoteUtils.addFetch(paramPath, sPullName, "+refs/heads/*:refs/remotes/origin/*");
			} catch (IOException e) {
				ExceptionUtils.toastException(RepositoryActivity.this, e);
			}
		}
		
		AuthRecordDao authRecordDao = new AuthRecordDao(RepositoryActivity.this);
		AuthRecord authRecord = authRecordDao.get(paramName, sPullName);
		authRecordDao.close();

		if (authRecord == null) {
			AuthDialog authDialog = new AuthDialog();
			authDialog.show(getSupportFragmentManager(), AuthDialog.TAG);
		} else {
			sPullUsername = authRecord.getUserName();
			sPullPassword = authRecord.getPassword();
			isPullAuthIgnored = authRecord.isIgnored();
			pull();
		}
	}

	@Override
	public void onBranchCreateReceive(String name) {
		sBranchCreateName = name;
		BranchCreateAsyncTask branchCreateTask = new BranchCreateAsyncTask();
		branchCreateTask.setOnTaskFinishListener(onBranchCreateFinish);
		BranchCreateAsyncTask.Param param = new BranchCreateAsyncTask.Param(paramPath, name, null);
		branchCreateTask.execute(new BranchCreateAsyncTask.Param[]{param});
	}

	@Override
	public void onMergeBranchReceive(Ref branch, String commitMessage, boolean isFastForward) {
		MergeBranchAsyncTask mergeTask = new MergeBranchAsyncTask();
		MergeBranchAsyncTask.Param param = new MergeBranchAsyncTask.Param(paramPath, branch);
		if (commitMessage != null && commitMessage != "")
			param.setCommitMessage(commitMessage);
		param.setFastForwardEnabled(isFastForward);
		mergeTask.setOnTaskFinishListener(onMergeTaskFinish);
		mergeTask.execute(new MergeBranchAsyncTask.Param[]{param});
	}

	@Override
	public void onBranchDeleteReceive(String branchName) {
		BranchDeleteAsyncTask deleteTask = new BranchDeleteAsyncTask();
		BranchDeleteAsyncTask.Param param = new BranchDeleteAsyncTask.Param(paramPath, branchName);
		deleteTask.setOnTaskFinishListener(onBranchDeleteTaskFinish);
		deleteTask.execute(new BranchDeleteAsyncTask.Param[]{param});
	}

	@Override
	public void onTagCreateReceive(String name, String message, PersonIdent tagger, boolean isAnnotated, boolean isSigned) {
		
	}
	
	private View.OnClickListener onFabCommitClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			Intent it = new Intent();
			Bundle params = new Bundle();
			params.putString(RepositoryCommitActivity.PARAM_KEY_PATH, paramPath);
			it.setClass(RepositoryActivity.this, RepositoryCommitActivity.class);
			it.putExtras(params);
			startActivity(it);
		}
	};
	
	private View.OnClickListener onImgRemoteClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			PopupMenu popupMenu = new PopupMenu(RepositoryActivity.this, mImgRemote);
			popupMenu.inflate(R.menu.repository_cloud);
			popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item)
				{
					switch (item.getItemId()) {
						case R.id.push:
							PushDialog pushDialog = new PushDialog();
							Bundle args = new Bundle();
							args.putString(PushDialog.ARG_KEY_PATH, paramPath);
							pushDialog.setArguments(args);
							pushDialog.show(getSupportFragmentManager(), PushDialog.TAG);
							return true;
						case R.id.addRemote:
							AddRemoteDialog addRemoteDialog = new AddRemoteDialog();
							addRemoteDialog.show(getSupportFragmentManager(), AddRemoteDialog.TAG);
							return true;
						case R.id.fetch:
							FetchDialog fetchDialog = new FetchDialog();
							Bundle args2 = new Bundle();
							args2.putString(FetchDialog.ARG_KEY_PATH, paramPath);
							fetchDialog.setArguments(args2);
							fetchDialog.show(getSupportFragmentManager(), FetchDialog.TAG);
							return true;
						case R.id.pull:
							PullDialog pullDialog = new PullDialog();
							Bundle args3 = new Bundle();
							args3.putString(FetchDialog.ARG_KEY_PATH, paramPath);
							pullDialog.setArguments(args3);
							pullDialog.show(getSupportFragmentManager(), FetchDialog.TAG);
							return true;
					}
					return false;
				}
			});
			popupMenu.show();
		}
	};
	
	private View.OnClickListener onImgBranchClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			PopupMenu popupMenu = new PopupMenu(RepositoryActivity.this, mImgBranch);
			popupMenu.inflate(R.menu.repository_branch);
			popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item)
					{
						switch (item.getItemId()) {
							case R.id.createBranch:
								BranchCreateDialog dialog = new BranchCreateDialog();
								dialog.show(getSupportFragmentManager(), BranchCreateDialog.TAG);
								return true;
							case R.id.mergeBranch:
								MergeBranchDialog mergeDialog = new MergeBranchDialog();
								Bundle argsMerge = new Bundle();
								argsMerge.putString(MergeBranchDialog.ARG_KEY_PATH, paramPath);
								mergeDialog.setArguments(argsMerge);
								mergeDialog.show(getSupportFragmentManager(), MergeBranchDialog.TAG);
								return true;
							case R.id.deleteBranch:
								BranchDeleteDialog deleteDialog = new BranchDeleteDialog();
								Bundle argsDelete = new Bundle();
								argsDelete.putString(MergeBranchDialog.ARG_KEY_PATH, paramPath);
								deleteDialog.setArguments(argsDelete);
								deleteDialog.show(getSupportFragmentManager(), MergeBranchDialog.TAG);
								return true;
						}
						return false;
					}
				});
			popupMenu.show();
		}
	};
	
	private PushAsyncTask.onTaskFinishListener onPushFinish = new PushAsyncTask.onTaskFinishListener() {
		@Override
		public void onTaskFinish(PushAsyncTask.Result result){
			if (result.isSuccess() && isAuthSaved) {
				AuthRecordDao authRecordDao = new AuthRecordDao(RepositoryActivity.this);
				if (authRecordDao.get(paramName, sPushName) == null) {
					AuthRecord auth = new AuthRecord(paramName, sPushName, sPushUsername, sPushPassword, isPushAuthIgnored);
					auth = authRecordDao.insert(auth);
				}
				authRecordDao.close();
			}
		}
	};
	
	private FetchAsyncTask.onTaskFinishListener onFetchFinish = new FetchAsyncTask.onTaskFinishListener() {
		@Override
		public void onTaskFinish(FetchAsyncTask.Result result) {
			if (result.isSuccess() && isAuthSaved) {
				AuthRecordDao authRecordDao = new AuthRecordDao(RepositoryActivity.this);
				if (authRecordDao.get(paramName, sPushName) == null) {
					AuthRecord auth = new AuthRecord(paramName, sFetchName, sFetchUsername, sFetchPassword, isFetchAuthIgnored);
					auth = authRecordDao.insert(auth);
				}
				authRecordDao.close();
			}
		}
	};
	
	private PullAsyncTask.onTaskFinishListener onPullFinish = new PullAsyncTask.onTaskFinishListener() {
		@Override
		public void onTaskFinish(PullAsyncTask.Result result) {
			if (result.isSuccess() && isAuthSaved) {
				AuthRecordDao authRecordDao = new AuthRecordDao(RepositoryActivity.this);
				if (authRecordDao.get(paramName, sPushName) == null) {
					AuthRecord auth = new AuthRecord(paramName, sPullName, sPullUsername, sPullPassword, isPullAuthIgnored);
					auth = authRecordDao.insert(auth);
				}
				authRecordDao.close();
			}
		}
	};
	
	private BranchCreateAsyncTask.OnTaskFinishListener onBranchCreateFinish = new BranchCreateAsyncTask.OnTaskFinishListener() {
		@Override
		public void onTaskFinish(BranchCreateAsyncTask.Result result) {
			switch (result) {
				case SUCCESS:
					CheckoutAsyncTask checkoutTask = new CheckoutAsyncTask();
					CheckoutAsyncTask.Param param = new CheckoutAsyncTask.Param(paramPath, BranchUtils.getRawBranchName(sBranchCreateName));
					checkoutTask.setOnTaskFinishListener(onCheckoutAfterBranchCreateFinish);
					checkoutTask.execute(new CheckoutAsyncTask.Param[]{param});
					break;
			}
		}
	};
	
	private CheckoutAsyncTask.OnTaskFinishListener onCheckoutAfterBranchCreateFinish = new CheckoutAsyncTask.OnTaskFinishListener() {
		@Override
		public void onTaskFinish(boolean isSuccess) {
			if (isSuccess) {
				Fragment fragment = adapter.getItem(0);
				if (fragment instanceof RepositoryFileFragment) {
					RepositoryFileFragment fileFragment = (RepositoryFileFragment) fragment;
					fileFragment.notifyBranchChanged();
				}
			}
		}
	};
	
	private MergeBranchAsyncTask.OnTaskFinishListener onMergeTaskFinish = new MergeBranchAsyncTask.OnTaskFinishListener() {
		@Override
		public void onTaskFinish(MergeBranchAsyncTask.Result result) {
			switch(result.getResultType()) {
				case SUCCESS:
					List<MergeResult> mergeResultList = result.getResultList();
					for (MergeResult mergeResult: mergeResultList) {
						Log.i(RepositoryActivity.class.getName(), "mergeResult.getStatus() = " + mergeResult.getMergeStatus());
						Map<String, int[][]> allConflicts = mergeResult.getConflicts();
						if (allConflicts != null) {
							for (String path : allConflicts.keySet()) {
								int[][] c = allConflicts.get(path);
								Log.i(RepositoryActivity.class.getName(), "Conflicts in file " + path);
								for (int i = 0; i < c.length; ++i) {
									Log.i(RepositoryActivity.class.getName(), "  Conflict #" + i);
									for (int j = 0; j < (c[i].length) - 1; ++j) {
										if (c[i][j] >= 0)
											Log.i(RepositoryActivity.class.getName(), "    Chunk for "
											 	 + mergeResult.getMergedCommits()[j] + " starts on line #"
											 	 + c[i][j]);
									}
								}
							}
						}
					}
					break;
				case EXCEPTION:
					Toast.makeText(RepositoryActivity.this, R.string.err_message, Toast.LENGTH_LONG).show();
					break;
			}
		}
	};
	
	private BranchDeleteAsyncTask.OnTaskFinishListener onBranchDeleteTaskFinish = new BranchDeleteAsyncTask.OnTaskFinishListener() {
		@Override
		public void onTaskFinish(boolean isSuccess) {
			if (isSuccess)
				Toast.makeText(RepositoryActivity.this, R.string.branch_delete_success, Toast.LENGTH_SHORT).show();
		}
	};
	
	private TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {

		@Override
		public void onTabSelected(TabLayout.Tab tab) {
			// TODO: Implement this method
			mViewPager.setCurrentItem(tab.getPosition());
		}

		@Override
		public void onTabUnselected(TabLayout.Tab p1) {
			// TODO: Implement this method
		}

		@Override
		public void onTabReselected(TabLayout.Tab p1) {
			// TODO: Implement this method
		}
	};
	
	private ViewPager.OnPageChangeListener onPageChange = new ViewPager.OnPageChangeListener() {

		@Override
		public void onPageScrolled(int column, float columnOffSet, int columnOffSetPixels) {
			// TODO: Implement this method
		}

		@Override
		public void onPageSelected(int column) {
			switch (column) {
				case 0:
					mFabCommit.setVisibility(View.VISIBLE);
					break;
				case 1:
					mFabCommit.setVisibility(View.GONE);
					break;
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			// TODO: Implement this method
		}
	};
	
	private void push() {
		PushAsyncTask pushTask = new PushAsyncTask(RepositoryActivity.this);
		PushAsyncTask.Param param = new PushAsyncTask.Param(paramPath, sPushName, sPushUsername, sPushPassword, isPushAll, isPushAuthIgnored);
		pushTask.setProgressDialogEnabled(true);
		pushTask.setOnTaskFinishListener(onPushFinish);
		pushTask.execute(new PushAsyncTask.Param[]{param});
	}
	
	private void fetch() {
		FetchAsyncTask fetchTask = new FetchAsyncTask(RepositoryActivity.this);
		FetchAsyncTask.Param param = new FetchAsyncTask.Param(paramPath, sFetchName, sFetchUsername, sFetchPassword, isFetchAuthIgnored);
		fetchTask.setProgressDialogEnabled(true);
		fetchTask.setOnTaskFinishListener(onFetchFinish);
		fetchTask.execute(new FetchAsyncTask.Param[]{param});
	}
	
	private void pull() {
		PullAsyncTask pullTask = new PullAsyncTask(RepositoryActivity.this);
		PullAsyncTask.Param param = new PullAsyncTask.Param(paramPath, sFetchName, isPullRebase, sPullUsername, sPullPassword, isPullAuthIgnored);
		pullTask.setProgressDialogEnabled(true);
		pullTask.setOnTaskFinishListener(onPullFinish);
		pullTask.execute(new PullAsyncTask.Param[]{param});
	}
	
	private enum Operation {
		PUSH, FETCH, PULL;
	}
}
