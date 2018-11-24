package com.danny.tools.git.branch;
import android.os.*;
import org.eclipse.jgit.api.*;
import com.danny.tools.git.repository.*;
import java.io.*;
import org.eclipse.jgit.api.errors.*;

public class BranchDeleteAsyncTask extends AsyncTask<BranchDeleteAsyncTask.Param, Void, Boolean>
{
	private OnTaskFinishListener listener;
	
	public void setOnTaskFinishListener(OnTaskFinishListener listener) {
		this.listener = listener;
	}

	@Override
	protected Boolean doInBackground(BranchDeleteAsyncTask.Param[] params) {
		for (Param param: params) {
			try {
				Git git = new Git(RepositoryUtils.openRepository(param.path));
				git.branchDelete()
					.setBranchNames(new String[]{param.branchName})
					.setForce(true)
					.call();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} catch (GitAPIException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (listener != null)
			listener.onTaskFinish(result);
	}
	
	public interface OnTaskFinishListener {
		public void onTaskFinish(boolean isSuccess);
	}
	
	public static class Param {
		private String path;
		private String branchName;

		public Param(String path, String branchName) {
			this.path = path;
			this.branchName = branchName;
		}
	}
}
