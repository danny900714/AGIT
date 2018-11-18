package com.danny.tools.git.fetch;
import com.danny.agit.*;
import android.app.*;
import android.os.*;
import android.net.*;
import android.content.*;
import android.widget.*;
import com.danny.tools.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.api.*;
import com.danny.tools.git.repository.*;
import java.io.*;
import com.danny.tools.git.remote.*;
import org.apache.http.auth.*;
import org.eclipse.jgit.transport.*;
import java.util.*;
import org.eclipse.jgit.api.errors.*;

public class FetchAsyncTask extends AsyncTask<FetchAsyncTask.Param, FetchAsyncTask.Progress, FetchAsyncTask.Result>
{
	private Activity activity;
	private boolean isProgressDialogEnabled;
	private ProgressDialog mProgressDialog;
	private onTaskFinishListener listener;

	public FetchAsyncTask(Activity activity) {
		this.activity = activity;
	}
	
	public void setProgressDialogEnabled (boolean enabled) {
		isProgressDialogEnabled = enabled;
	}

	public void setOnTaskFinishListener(onTaskFinishListener listener) {
		this.listener = listener;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		if (!isProgressDialogEnabled)
			return;

		mProgressDialog = new ProgressDialog(activity, R.style.AppTheme_ProgressDialog_ColorAccent);

		mProgressDialog.setTitle(R.string.fetch);
		mProgressDialog.setMessage(activity.getString(R.string.fetch) + "...");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}

	@Override
	protected FetchAsyncTask.Result doInBackground(FetchAsyncTask.Param[] params) {
		Result successResult = new Result(true);

		if (!checkConnectivity())
			return new Result(false, Result.ERR_RES_NO_CONNECTION);
		
		for (Param param: params) {
			try {
				Git git = new Git(RepositoryUtils.openRepository(param.path));
				RefSpec refSpec = new RefSpec("refs/heads/*:refs/remotes/" + param.name + "/*");
				FetchCommand fetch = git.fetch().setRemote(param.name).setRefSpecs(new RefSpec[]{refSpec}).setProgressMonitor(monitor);
				
				if (!param.isAuthIgnored)
					fetch = fetch.setCredentialsProvider(new UsernamePasswordCredentialsProvider(param.username, param.password));
				
				FetchResult result = fetch.call();
				
				successResult.updateCollection = result.getTrackingRefUpdates();
			} catch (IOException e) {
				e.printStackTrace();
				return new Result(false, e);
			} catch (GitAPIException e) {
				e.printStackTrace();
				return new Result(false, e);
			}
		}
		
		return successResult;
	}

	@Override
	protected void onProgressUpdate(FetchAsyncTask.Progress[] progresses) {
		super.onProgressUpdate(progresses);
		
		if (!isProgressDialogEnabled)
			return;

		for (Progress progress: progresses) {
			switch (progress.type) {
				case Progress.PROGRESS_DEFAULT:
					mProgressDialog.setProgress(progress.completed);
					break;
				case Progress.PROGRESS_NEW_TASK:
					if (progress.totalWork == 0) {
						mProgressDialog.setIndeterminate(true);
						mProgressDialog.setMessage(progress.taskName);
						mProgressDialog.setMax(0);
					} else {
						mProgressDialog.setIndeterminate(false);
						mProgressDialog.setMessage(progress.taskName);
						mProgressDialog.setMax(progress.totalWork);
					}
					break;
			}
		}
	}

	@Override
	protected void onPostExecute(FetchAsyncTask.Result result) {
		super.onPostExecute(result);
		
		if (isProgressDialogEnabled)
			mProgressDialog.dismiss();

		if (listener != null)
			listener.onTaskFinish(result);

		if (result.isSuccess) {
			StringBuilder builder = new StringBuilder();
			for (TrackingRefUpdate refUpdate: result.updateCollection)
				builder.append(parseTrackingRefUpdate(refUpdate) + "\n");
			showResultDialog(builder.toString());
		}
		else {
			if (result.stringRes != 0)
				Toast.makeText(activity, result.message, Toast.LENGTH_LONG).show();
			else if (result.e != null)
				ExceptionUtils.toastException(activity, result.e);
			else if (result.message != null)
				Toast.makeText(activity, result.message, Toast.LENGTH_LONG).show();
			else
				ExceptionUtils.toastException(activity, NullPointerException.class);
		}
	}
	
	private ProgressMonitor monitor = new ProgressMonitor() {
		private int totalCompleted = 0;

		@Override
		public void start(int totalTasks) {

		}

		@Override
		public void beginTask(String title, int totalWorks) {
			publishProgress(new Progress(title, totalWorks));
			totalCompleted = 0;
		}

		@Override
		public void update(int completed) {
			totalCompleted += completed;
			publishProgress(new Progress(totalCompleted));
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
	
	private void showResultDialog(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AppTheme_AlertDialog_ColorAccent);
		AlertDialog dialog = builder.setTitle(R.string.push)
			.setMessage(message)
			.setPositiveButton(android.R.string.ok, null)
			.create();
		dialog.show();
	}
	
	private String parseTrackingRefUpdate(TrackingRefUpdate refUpdate) {
		switch (refUpdate.getResult()) {
			case FAST_FORWARD:
				return String.format(activity.getString(R.string.fetch_result_fast_forward), refUpdate.getRemoteName());
			case FORCED:
				return String.format(activity.getString(R.string.fetch_result_forced), refUpdate.getRemoteName());
			case IO_FAILURE:
				return String.format(activity.getString(R.string.fetch_result_io_failure), refUpdate.getRemoteName());
			case LOCK_FAILURE:
				return String.format(activity.getString(R.string.fetch_result_lock_failure), refUpdate.getRemoteName());
			case NEW:
				return String.format(activity.getString(R.string.fetch_result_new), refUpdate.getRemoteName());
			case NO_CHANGE:
				return String.format(activity.getString(R.string.fetch_result_no_change), refUpdate.getRemoteName());
			case NOT_ATTEMPTED:
				return String.format(activity.getString(R.string.fetch_result_not_attempted), refUpdate.getRemoteName());
			case REJECTED:
				return String.format(activity.getString(R.string.fetch_result_rejected), refUpdate.getRemoteName());
			case REJECTED_CURRENT_BRANCH:
				return String.format(activity.getString(R.string.fetch_result_rejected_current_branch), refUpdate.getRemoteName());
			case RENAMED:
				return String.format(activity.getString(R.string.fetch_result_renamed), refUpdate.getRemoteName());
			default:
				return "";
		}
	}
	
	public interface onTaskFinishListener {
		public void onTaskFinish(Result result);
	}
	
	public static class Param {
		private String path;
		private String name;
		private String username;
		private String password;
		private boolean isAuthIgnored;

		public Param() {

		}

		public Param(String path, String url, String username, String password, boolean isAuthIgnored)
		{
			this.path = path;
			this.name = url;
			this.username = username;
			this.password = password;
			this.isAuthIgnored = isAuthIgnored;
		}

		public void setIsAuthIgnored(boolean isAuthIgnored)
		{
			this.isAuthIgnored = isAuthIgnored;
		}

		public boolean isAuthIgnored()
		{
			return isAuthIgnored;
		}

		public void setUrl(String url)
		{
			this.name = url;
		}

		public String getUrl()
		{
			return name;
		}

		public void setPath(String path)
		{
			this.path = path;
		}

		public String getPath()
		{
			return path;
		}

		public void setUsername(String username)
		{
			this.username = username;
		}

		public String getUsername()
		{
			return username;
		}

		public void setPassword(String password)
		{
			this.password = password;
		}

		public String getPassword()
		{
			return password;
		}
	}

	static class Progress {
		static final int PROGRESS_DEFAULT = 1;
		static final int PROGRESS_NEW_TASK = 2;

		int type;
		String taskName;
		int totalWork;
		int completed;

		Progress() {

		}

		Progress(int completed) {
			this.type = PROGRESS_DEFAULT;
			this.completed = completed;
		}

		Progress(String taskName, int totalWork) {
			this.type = PROGRESS_NEW_TASK;
			this.taskName = taskName;
			this.totalWork = totalWork;
		}
	}

	public class Result {
		public static final int ERR_RES_NO_CONNECTION = R.string.err_no_internet;

		private boolean isSuccess;
		private String message;
		private int stringRes;
		private Exception e;
		private Collection<TrackingRefUpdate> updateCollection;

		Result(boolean isSuccess) {
			this.isSuccess = isSuccess;
		}

		Result(boolean isSuccess, String message) {
			this.isSuccess = isSuccess;
			this.message = message;
		}

		Result (boolean isSuccess, int stringRes) {
			this.isSuccess = isSuccess;
			this.stringRes = stringRes;
			this.message = activity.getString(stringRes);
		}

		Result (boolean isSuccess, Exception e) {
			this.isSuccess = isSuccess;
			this.e = e;
			this.message = e.getClass().getName();
		}

		public boolean isSuccess() {
			return isSuccess;
		}

		public String getMessage() {
			return message;
		}
	}
}
