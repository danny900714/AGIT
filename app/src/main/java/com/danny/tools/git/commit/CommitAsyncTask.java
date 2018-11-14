package com.danny.tools.git.commit;
import android.os.*;
import android.content.*;
import org.eclipse.jgit.api.errors.*;
import android.widget.*;
import com.danny.agit.*;
import com.danny.tools.*;
import org.eclipse.jgit.lib.*;

public class CommitAsyncTask extends AsyncTask<CommitAsyncTask.Param, CommitAsyncTask.Progress, CommitAsyncTask.Result> 
{
	private Context context;
	private OnTaskFinishListener listener;

	public CommitAsyncTask(Context context) {
		this.context = context;
	}
	
	public void setOnTaskFinishListener(OnTaskFinishListener listener) {
		this.listener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected CommitAsyncTask.Result doInBackground(CommitAsyncTask.Param[] params) {
		boolean isSuccess = true;
		for (Param param: params) {
			if (param.isCommitAll) {
				try {
					CommitUtils.commitAll(param.path, param.author, param.committer, param.message);
				} catch (GitAPIException e) {
					isSuccess = false;
					Progress progress = new Progress(false, null, e);
					publishProgress(progress);
				}
			} else {
				try {
					CommitUtils.commit(param.path, param.author, param.committer, param.message);
				} catch (GitAPIException e) {
					isSuccess = false;
					Progress progress = new Progress(false, null, e);
					publishProgress(progress);
				}
			}
		}
		
		Result result = new Result(isSuccess);
		return result;
	}

	@Override
	protected void onProgressUpdate(CommitAsyncTask.Progress[] values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(CommitAsyncTask.Result result) {
		super.onPostExecute(result);
		
		if (result.isSuccess)
			Toast.makeText(context, R.string.commit_success, Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(context, R.string.commit_fail, Toast.LENGTH_SHORT).show();
		
		if (listener != null)
			listener.onTaskFinish(result);
	}
	
	public interface OnTaskFinishListener {
		public void onTaskFinish(Result result);
	}
	
	public static class Param {
		private boolean isCommitAll;
		private String path;
		private PersonIdent author;
		private PersonIdent committer;
		private String message;

		public Param(boolean isCommitAll, String path, PersonIdent author, PersonIdent committer, String message)
		{
			this.isCommitAll = isCommitAll;
			this.path = path;
			this.author = author;
			this.committer = committer;
			this.message = message;
		}
		
		public Param() {
			
		}

		public void setAuthor(PersonIdent author)
		{
			this.author = author;
		}

		public PersonIdent getAuthor()
		{
			return author;
		}

		public void setCommitter(PersonIdent committer)
		{
			this.committer = committer;
		}

		public PersonIdent getCommitter()
		{
			return committer;
		}

		public void setMessage(String message)
		{
			this.message = message;
		}

		public String getMessage()
		{
			return message;
		}

		public void setPath(String path)
		{
			this.path = path;
		}

		public String getPath()
		{
			return path;
		}

		public void setIsCommitAll(boolean isCommitAll)
		{
			this.isCommitAll = isCommitAll;
		}

		public boolean isCommitAll()
		{
			return isCommitAll;
		}
	}
	
	static class Progress {
		private boolean isSuccess;
		private String message;
		private Exception e;
		
		Progress() {
			
		}

		Progress(boolean isSuccess, String message, Exception e)
		{
			this.isSuccess = isSuccess;
			this.message = message;
			this.e = e;
		}
	}
	
	public static class Result {
		private boolean isSuccess;
		
		Result() {
			
		}

		Result(boolean isSuccess)
		{
			this.isSuccess = isSuccess;
		}
		
		public boolean isSuccess() {
			return isSuccess;
		}
	}
}
