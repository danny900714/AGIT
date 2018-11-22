package com.danny.tools.git.checkout;
import android.os.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.lib.*;
import com.danny.tools.git.repository.*;
import java.io.*;
import java.util.*;
import com.danny.tools.git.branch.*;
import org.eclipse.jgit.api.*;

public class CheckoutAsyncTask extends AsyncTask<CheckoutAsyncTask.Param, Void, Boolean>
{
	private OnTaskFinishListener listener;
	
	public void setOnTaskFinishListener(OnTaskFinishListener listener) {
		this.listener = listener;
	}
	
	@Override
	protected Boolean doInBackground(Param[] params) {
		for (Param param: params) {
			try {
				int branchType = BranchUtils.getBranchType(param.name);
				Git git = new Git(RepositoryUtils.openRepository(param.path));
				
				if (branchType == BranchUtils.TYPE_LOCAL) {
					git.checkout().setName(param.name).call();
				} else if (branchType == BranchUtils.TYPE_REMOTE) {
					boolean isBranchCreate = true;
					List<Ref> localBranchList = BranchUtils.getLocalBranches(git.getRepository());
					
					// check if local branch exists
					String[] sRemoteBranchItem = param.name.split("/");
					String sPureRemoteBranch = "";
					for (int i = 3; i < sRemoteBranchItem.length; i++) {
						sPureRemoteBranch += sRemoteBranchItem[i];
						if (i != sRemoteBranchItem.length - 1)
							sPureRemoteBranch += "/";
					}
					for (Ref localBranch: localBranchList) {
						if (BranchUtils.getPureBranchName(localBranch.getName()).equals(sPureRemoteBranch)) {
							isBranchCreate = false;
							break;
						}
					}
					
					if (isBranchCreate) {
						git.checkout().setName(sPureRemoteBranch)
							.setCreateBranch(true)
							.setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM)
							.setStartPoint(BranchUtils.getPureBranchName(param.name))
							.call();
					} else {
						git.checkout().setName(BranchUtils.getPureBranchName(param.name))
							.setCreateBranch(false)
							.call();
					}
				}
			} catch (GitAPIException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
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
		private String name;
		
		public Param(String path, String branchName) {
			this.path = path;
			this.name = branchName;
		}
	}
} 
