package com.danny.tools.git.tag;
import org.eclipse.jgit.lib.*;
import android.os.*;
import org.eclipse.jgit.api.*;
import com.danny.tools.git.repository.*;
import java.io.*;
import org.eclipse.jgit.api.errors.*;
import android.content.*;
import android.widget.*;
import com.danny.agit.*;
import com.danny.tools.*;

public class TagCreateAsyncTask extends AsyncTask<TagCreateAsyncTask.Param, Void, Boolean> {
	
	private Context context;
	private OnTaskFinishListener listener;
	private Exception e;
	
	public TagCreateAsyncTask(Context context) {
		this.context = context;
	}

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
					.setTagger(param.tagger)
					.call();
			} catch (IOException e) {
				this.e = e;
				e.printStackTrace();
				return false;
			} catch (GitAPIException e) {
				this.e = e;
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		
		if (result)
			Toast.makeText(context, R.string.tag_created_success, Toast.LENGTH_SHORT).show();
		else
			ExceptionUtils.toastException(context, e);
		
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
	}
}
