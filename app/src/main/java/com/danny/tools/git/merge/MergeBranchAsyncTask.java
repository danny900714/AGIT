package com.danny.tools.git.merge;
import org.eclipse.jgit.lib.*;
import java.util.*;
import org.eclipse.jgit.api.*;
import android.os.*;
import java.io.*;
import com.danny.tools.git.repository.*;
import org.eclipse.jgit.api.errors.*;

public class MergeBranchAsyncTask extends AsyncTask<MergeBranchAsyncTask.Param, Void, MergeBranchAsyncTask.Result>
{
	private OnTaskFinishListener listener;
	
	public void setOnTaskFinishListener(OnTaskFinishListener listener) {
		this.listener = listener;
	}

	@Override
	protected MergeBranchAsyncTask.Result doInBackground(MergeBranchAsyncTask.Param[] params) {
		Result result = new Result();
		for (Param param: params) {
			try {
				Git git = new Git(param.repository);
				MergeCommand merge = git.merge().include(param.ref);
				
				if (param.isFastForward)
					merge = merge.setFastForward(MergeCommand.FastForwardMode.FF);
				else
					merge = merge.setFastForward(MergeCommand.FastForwardMode.NO_FF);
				
				if (param.commitMessage == null)
					merge = merge.setCommit(false);
				else
					merge = merge.setCommit(true).setMessage(param.commitMessage);
			
				MergeResult mergeResult = merge.call();
				result.resultList.add(mergeResult);
			} catch (GitAPIException e) {
				e.printStackTrace();
				result.resultType = Result.Type.EXCEPTION;
				return result;
			}
		}
		
		result.resultType = Result.Type.SUCCESS;
		return result;
	}

	@Override
	protected void onPostExecute(MergeBranchAsyncTask.Result result) {
		super.onPostExecute(result);
		
		if (listener != null)
			listener.onTaskFinish(result);
	}
	
	public interface OnTaskFinishListener {
		public void onTaskFinish(Result result);
	}
	
	public static class Param {
		private Repository repository;
		private Ref ref;
		private boolean isFastForward = false;
		private String commitMessage = null;
		
		public Param(Repository repository, Ref ref) {
			this.repository = repository;
			this.ref = ref;
		}
		
		public Param(File directory, Ref ref) {
			try {
				this.repository = RepositoryUtils.openRepository(directory);
				this.ref = ref;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public Param(String path, Ref ref) {
			try {
				this.repository = RepositoryUtils.openRepository(path);
				this.ref = ref;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void setFastForwardEnabled(boolean isFastForward) {
			this.isFastForward = isFastForward;
		}
		
		public void setCommitMessage(String message) {
			this.commitMessage = message;
		}
	}
	
	public static class Result {
		private Type resultType;
		private List<MergeResult> resultList = new ArrayList<>();
		
		private Result() {}
		
		public Type getResultType() {
			return resultType;
		}
		
		public List<MergeResult> getResultList() {
			return resultList;
		}
		
		public enum Type {
			SUCCESS, EXCEPTION;
		}
	}
}
