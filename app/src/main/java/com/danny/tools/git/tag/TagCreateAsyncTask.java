package com.danny.tools.git.tag;
import org.eclipse.jgit.lib.*;
import android.os.*;
import org.eclipse.jgit.api.*;
import com.danny.tools.git.repository.*;
import java.io.*;
import org.eclipse.jgit.api.errors.*;

public class TagCreateAsyncTask extends AsyncTask<TagCreateAsyncTask.Param, Void, Boolean> {
	
	private OnTaskFinishListener listener;

	public void setOnTaskFinishListener(OnTaskFinishListener listener) {
		this.listener = listener;
	}
	
	@Override
	protected Boolean doInBackground(TagCreateAsyncTask.Param[] params) {
		for (Param param: params) {
			try {
				Git git = new Git(RepositoryUtils.openRepository(param.path));
				
				git.tag()
					.setName(param.name)
					.setMessage(param.message)
					.setAnnotated(param.isAnnotated)
					.setSigned(param.isSigned)
					.setTagger(param.tagger)
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
		private String name;
		private String message = null;
		private PersonIdent tagger = null;
		private boolean isAnnotated = false;
		private boolean isSigned = false;

		public Param(String path, String name) {
			this.path = path;
			this.name = name;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public void setTagger(PersonIdent tagger) {
			this.tagger = tagger;
		}

		public void setAnnotated(boolean isAnnotated) {
			this.isAnnotated = isAnnotated;
		}

		public void setSigned(boolean isSigned) {
			this.isSigned = isSigned;
		}
	}
}
