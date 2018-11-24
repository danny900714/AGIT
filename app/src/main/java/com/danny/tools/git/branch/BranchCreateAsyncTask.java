package com.danny.tools.git.branch;
import org.eclipse.jgit.lib.*;
import java.io.*;
import com.danny.tools.git.repository.*;
import android.os.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import java.util.*;

public class BranchCreateAsyncTask extends AsyncTask<BranchCreateAsyncTask.Param, Void, BranchCreateAsyncTask.Result>
{
	private OnTaskFinishListener listener;
	
	public void setOnTaskFinishListener(OnTaskFinishListener listener) {
		this.listener = listener;
	}

	@Override
	protected Result doInBackground(BranchCreateAsyncTask.Param[] params) {
		for (Param param: params) {
			Git git = new Git(param.repository);
			
			try {
				// check if branch exists
				List<String> localBranchNameList = BranchUtils.getPureBranchList(BranchUtils.getAllBranches(git.getRepository()));
				for (String branchName: localBranchNameList) {
					if (param.branchName.equals(branchName))
						return Result.BRANCH_EXISTED;
				}
				
				// check if branch name legal
				if (!BranchUtils.isBranchNameLegal(param.branchName))
					return Result.INVALID_NANE;
			
				git.branchCreate()
					.setName(param.branchName)
					.setStartPoint(param.startPoint)
					.setForce(true)
					.call();
			} catch (GitAPIException e) {
				return Result.EXCEPTION;
			}
		}
		return Result.SUCCESS;
	}

	@Override
	protected void onPostExecute(Result result) {
		super.onPostExecute(result);
		
		if (listener != null)
			listener.onTaskFinish(result);
	}
	
	public interface OnTaskFinishListener {
		public void onTaskFinish(Result result);
	}
	
	public static class Param {
		private Repository repository;
		private String branchName;
		private String startPoint;

		public Param(Repository repository, String branchName, String startPoint) {
			this.repository = repository;
			this.branchName = branchName;
			this.startPoint = startPoint;
		}
		
		public Param(File directory, String branchName, String startPoint) {
			try {
				this.repository = RepositoryUtils.openRepository(directory);
				this.branchName = branchName;
				this.startPoint = startPoint;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public Param(String path, String branchName, String startPoint) {
			try {
				this.repository = RepositoryUtils.openRepository(path);
				this.branchName = branchName;
				this.startPoint = startPoint;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public enum Result {
		SUCCESS, BRANCH_EXISTED, INVALID_NANE, EXCEPTION;
	}
}
