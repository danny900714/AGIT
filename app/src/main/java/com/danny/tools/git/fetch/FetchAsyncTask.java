package com.danny.tools.git.fetch;
import com.danny.agit.*;
import android.app.*;
import android.os.*;

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
	protected FetchAsyncTask.Result doInBackground(FetchAsyncTask.Param[] params) {
		return null;
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
