package com.danny.tools.git.commit;
import android.os.*;
import com.danny.tools.git.repository.*;
import org.eclipse.jgit.api.errors.*;
import android.content.*;
import android.widget.*;
import com.danny.agit.*;

public class InitAsyncTask extends AsyncTask<String, Void, String>
{
	private static final String RESULT_SUCCESS = "SUCCESS";
	
	private Context context;
	private onTaskFinishListener listener;
	
	public InitAsyncTask(Context context) {
		this.context = context;
	}

	public void setOnTaskFinishListener(onTaskFinishListener listener) {
		this.listener = listener;
	}
	
	@Override
	protected String doInBackground(String[] params) {
		boolean success = true;
		StringBuilder builder = new StringBuilder();
		for (String param: params) {
			try {
				RepositoryUtils.createNewRepositoryWithCommit(param);
			} catch (IllegalStateException e) {
				builder.append(e.toString());
				success = false;
			} catch (GitAPIException e){
				builder.append(e.toString());
				success = false;
			}
		}
		if (success)
			return RESULT_SUCCESS;
		return builder.toString();
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (result.equals(RESULT_SUCCESS)) {
			Toast.makeText(context, R.string.create_repository_success, Toast.LENGTH_SHORT).show();
			listener.onTaskFinish(true);
		}
		else {
			Toast.makeText(context, result, Toast.LENGTH_LONG).show();
			listener.onTaskFinish(false);
		}
	}
	
	public interface onTaskFinishListener {
		public void onTaskFinish(boolean isSuccess);
	}
}
