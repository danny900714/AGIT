package com.danny.agit.overview;
import android.os.*;
import android.app.*;
import android.content.*;
import android.content.res.*;
import com.danny.agit.*;
import org.eclipse.jgit.api.*;
import java.io.*;
import org.eclipse.jgit.api.errors.*;
import android.widget.*;
import org.eclipse.jgit.lib.*;
import android.util.*;
import java.text.*;
import android.net.*;
import com.danny.tools.data.repository.*;
import java.util.*;
import org.apache.commons.io.*;
import com.danny.tools.*;

public class CloneAsyncTask extends AsyncTask<CloneAsyncTask.Params, CloneAsyncTask.Progress, String>
{
	private static final String ERR_NO_INTERNET = "ERR_NO_INTERNET";
	private static final String ERR_NOT_DIRECTORY = "ERR_NOT_DIRECTORY";
	private static final String ERR_WHEN_CLONE = "ERR_WHEN_CLONE";
	private static final String ERR_FAIL_CREATE_DIRECTORY = "ERR_FAIL_CREATE_DIRECTORY";
	private static final String ERR_DIRECTORY_EXISTS = "ERR_DIRECTORY_EXISTS";
	private static final String SUCCESS = "SUCCESS";
	private static final int TOTAL_TASKS = 7;
	private int currentTask = 1;
	private ArrayList<Params> paramList = new ArrayList<>();
	
	private Activity activity;
	private Resources res;
	private ProgressDialog progressDialog;
	private RepositoryRecordDao recordDao;

	public CloneAsyncTask(Activity acvitity) {
		this.activity = acvitity;
	}

	@Override
	protected void onPreExecute() {
		// TODO: Implement this method
		super.onPreExecute();
		
		// init
		res = activity.getResources();
		recordDao = new RepositoryRecordDao(activity);
		
		progressDialog = new ProgressDialog(activity, R.style.AppTheme_ProgressDialog_ColorAccent);
		String sMessage = res.getString(R.string.message_clone_process);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setProgressPercentFormat(NumberFormat.getPercentInstance());
		progressDialog.setProgressNumberFormat(null);
		progressDialog.setMessage(sMessage);
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(true);
		progressDialog.show();
	}
	
	@Override
	protected String doInBackground(CloneAsyncTask.Params[] params) {
		if (!checkConnectivity())
			return ERR_NO_INTERNET;
		
		for (CloneAsyncTask.Params param: params) {
			File file = new File(param.path);
			
			if (!file.isDirectory())
				return ERR_NOT_DIRECTORY;
			
			// create new directory
			File targetDirectory = new File(param.path + "/" + param.name);
			if (targetDirectory.exists())
				return ERR_DIRECTORY_EXISTS;
			if (!targetDirectory.mkdir())
				return ERR_FAIL_CREATE_DIRECTORY;
			
			// update path
			param.path = targetDirectory.toString();
			
			// clone
			try {
				Git.cloneRepository().setURI(param.url).setDirectory(targetDirectory).setProgressMonitor(monitor).call();
			} catch (GitAPIException e) {
				e.printStackTrace();
				return ERR_WHEN_CLONE;
			} finally {
				paramList.add(param);
			}
		}
		return SUCCESS;
	}

	@Override
	protected void onProgressUpdate(CloneAsyncTask.Progress[] values) {
		// TODO: Implement this method
		super.onProgressUpdate(values);
		
		for (CloneAsyncTask.Progress progress: values) {
			switch (progress.type) {
				case Progress.PROGRESS_DEFAULT:
					progressDialog.setProgress(progress.totalCompleted);
					break;
				case Progress.PROGRESS_NEW_TASK:
					currentTask += 1;
					if (progress.totalWorks == 0) {
						progressDialog.setIndeterminate(true);
						progressDialog.setMax(100);
						progressDialog.setProgressNumberFormat(null);
					} else {
						progressDialog.setIndeterminate(false);
						progressDialog.setMax(progress.totalWorks);
						progressDialog.setProgressNumberFormat("%1d/%2d");
					}
					progressDialog.setMessage(progress.taskName + "... (" + Integer.toString(currentTask) + " of " + Integer.toString(TOTAL_TASKS) + ")");
					progressDialog.setProgress(0);
					progressDialog.setSecondaryProgress(currentTask * progressDialog.getMax() / TOTAL_TASKS);
					break;
			}
		}
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		
		// handle progressDialog
		progressDialog.dismiss();
		
		switch (result) {
			case SUCCESS:
				for (Params param: paramList) {
					RepositoryRecord record = new RepositoryRecord(param.name, param.path);
					recordDao.insert(record);
				}
				Toast.makeText(activity, R.string.message_clone_success, Toast.LENGTH_SHORT).show();
				activity.finish();
				break;
			case ERR_NO_INTERNET:
				Toast.makeText(activity, R.string.err_no_internet, Toast.LENGTH_LONG).show();
				break;
			case ERR_NOT_DIRECTORY:
				Toast.makeText(activity, R.string.err_not_directory, Toast.LENGTH_LONG).show();
				break;
			case ERR_DIRECTORY_EXISTS:
				Toast.makeText(activity, R.string.err_directory_exists, Toast.LENGTH_LONG).show();
				break;
			case ERR_FAIL_CREATE_DIRECTORY:
				Toast.makeText(activity, R.string.err_fail_create_directory, Toast.LENGTH_LONG).show();
				break;
			case ERR_WHEN_CLONE:
				Toast.makeText(activity, R.string.err_when_clone, Toast.LENGTH_LONG).show();
				for (Params param: paramList) {
					File createdFolder = new File(param.path);
					Log.i(CloneAsyncTask.class.getName(), "createdFolder = " + createdFolder.toString());
					try {
						FileUtils.deleteDirectory(createdFolder);
					}
					catch (IOException e) {
						ExceptionUtils.toastException(activity, e);
					}
				}
				break;
		}
	}
	
	private ProgressMonitor monitor = new ProgressMonitor() {
		private int totalCompleted = 0;
		
		@Override
		public void start(int totalTasks) {
		}

		@Override
		public void beginTask(String title, int totoalWorks) {
			publishProgress(new Progress(Progress.PROGRESS_NEW_TASK, title, totoalWorks));
			totalCompleted = 0;
		}

		@Override
		public void update(int completed) {
			totalCompleted += completed;
			publishProgress(new Progress(Progress.PROGRESS_DEFAULT, totalCompleted));
		}

		@Override
		public void endTask() {
		}

		@Override
		public boolean isCancelled() {
			return false;
		}
	};
	
	private boolean checkConnectivity() {
		ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
		return isConnected;
	}
	
	public static class Params {
		public String name;
		public String path;
		public String url;

		public Params() {

		}

		public Params(String name, String path, String url) {
			this.name = name;
			this.path = path;
			this.url = url;
		}
	}
	
	public static class Progress {
		public static final String PROGRESS_DEFAULT = "PROGRESS_DEFAULT";
		public static final String PROGRESS_NEW_TASK = "PROGRESS_NEW_TASK";
		
		public String type;
		public String taskName;
		public int totalWorks;
		public int totalCompleted;
		
		public Progress() {
			
		}
		
		public Progress(String type, String taskName) {
			this.type = type;
			this.taskName = taskName;
		}

		public Progress(String type, String taskName, int totalWorks) {
			this.type = type;
			this.taskName = taskName;
			this.totalWorks = totalWorks;
		}
		
		public Progress(String type, int totalCompleted) {
			this.type = type;
			this.totalCompleted = totalCompleted;
		}
	}
}
