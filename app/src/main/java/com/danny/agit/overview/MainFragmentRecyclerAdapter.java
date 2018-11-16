package com.danny.agit.overview;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.danny.agit.*;
import com.danny.tools.data.repository.*;
import android.content.*;
import java.util.*;
import android.view.View.*;
import com.danny.tools.git.commit.*;
import org.eclipse.jgit.api.errors.*;
import com.danny.tools.*;
import android.util.*;
import org.eclipse.jgit.revwalk.*;
import android.text.format.*;
import android.os.*;
import com.danny.agit.repository.*;

public class MainFragmentRecyclerAdapter extends RecyclerView.Adapter<MainFragmentRecyclerAdapter.ViewHolder>
{
	private Context context;
	private View.OnClickListener onItemClick;
	
	private ArrayList<Adapter> adapterList = new ArrayList<>();

	public MainFragmentRecyclerAdapter(Context context, View.OnClickListener onItemClick) {
		this.context = context;
		RepositoryRecordDao recordDao = new RepositoryRecordDao(context);
		this.onItemClick = onItemClick;
		
		ArrayList<RepositoryRecord> recordList = (ArrayList<RepositoryRecord>) recordDao.getAllInverse();
		for (RepositoryRecord record: recordList) {
			Adapter adapter = new Adapter();
			
			// set record
			adapter.setRecord(record);
			
			// set git log
			try {
				List<RevCommit> logList = LogUtils.getAllLogsInverse(record.getPath());
				adapter.setCommit(logList.get(0));
				
				this.adapterList.add(adapter);
			} catch (GitAPIException e) {
				ExceptionUtils.toastException(context, e);
			}
		}
		
		recordDao.close();
	}
	
	@Override
	public MainFragmentRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// TODO: Implement this method
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_main_fragment, parent, false);
		view.setOnClickListener(onItemClick);
		ViewHolder viewHolder = new ViewHolder(view);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(MainFragmentRecyclerAdapter.ViewHolder holder, int position) {
		// TODO: Implement this method
		holder.mTxtTitle.setText(adapterList.get(position).getRecord().getName());
		holder.mTxtPath.setText(adapterList.get(position).getRecord().getPath());
		
		RevCommit commit = adapterList.get(position).getCommit();
		long lastCommitTime = commit.getCommitTime();
		lastCommitTime *= 1000;
		Date lastCommitDate = new Date(lastCommitTime);
		String sDate = DateFormat.format("yyyy/MM/dd", lastCommitDate).toString();
		holder.mTxtCommit.setText(commit.getFullMessage());
		holder.mTxtDate.setText(sDate);
		holder.mTxtAuthor.setText(commit.getAuthorIdent().getName());
		
		
		// get commit info
		/*try {
			List<RevCommit> logList = CommitUtils.getAllLogsInverse(recordList.get(position).getPath());
			RevCommit lastCommit = logList.get(0);
			long lastCommitTime = lastCommit.getCommitTime();
			lastCommitTime *= 1000;
			Date lastCommitDate = new Date(lastCommitTime);
			String sDate = DateFormat.format("yyyy/MM/dd", lastCommitDate).toString();
			holder.mTxtCommit.setText(lastCommit.getFullMessage());
			holder.mTxtDate.setText(sDate);
		} catch (GitAPIException e) {
			ExceptionUtils.toastException(context, e);
		}*/
	}

	@Override
	public int getItemCount() {
		// TODO: Implement this method
		return adapterList.size();
	}
	
	public void updateData() {
		ArrayList<Adapter> adapterList = new ArrayList<>();
		RepositoryRecordDao recordDao = new RepositoryRecordDao(context);
		
		ArrayList<RepositoryRecord> recordList = (ArrayList<RepositoryRecord>) recordDao.getAllInverse();
		for (RepositoryRecord record: recordList) {
			Adapter adapter = new Adapter();

			// set record
			adapter.setRecord(record);

			// set git log
			try {
				List<RevCommit> logList = LogUtils.getAllLogsInverse(record.getPath());
				adapter.setCommit(logList.get(0));

				adapterList.add(adapter);
			} catch (GitAPIException e) {
				ExceptionUtils.toastException(context, e);
			}
		}
		
		this.adapterList = adapterList;
		notifyDataSetChanged();
		
		recordDao.close();
	}
	
	public Adapter getChildData(int position) {
		return adapterList.get(position);
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder {
		private TextView mTxtTitle, mTxtPath, mTxtAuthor, mTxtDate, mTxtCommit;
		private ImageView mImgProfile;
		
		public ViewHolder(View view) {
			super(view);
			
			mTxtTitle = view.findViewById(R.id.mainFragmentRclItemTxtTitle);
			mTxtPath = view.findViewById(R.id.mainFragmentRclItemTxtPath);
			mTxtAuthor = view.findViewById(R.id.mainFragmentRclItemTxtAuthor);
			mTxtDate = view.findViewById(R.id.mainFragmentRclItemTxtDate);
			mTxtCommit = view.findViewById(R.id.mainFragmentRclItemTxtCommit);
			mImgProfile = view.findViewById(R.id.mainFragmentRclItemImg);
		}
	}
	
	public static class Adapter {
		private RepositoryRecord record;
		private RevCommit commit;

		public Adapter() {
			
		}
		
		public Adapter(RepositoryRecord record, RevCommit commit) {
			this.record = record;
			this.commit = commit;
		}

		public void setRecord(RepositoryRecord record) {
			this.record = record;
		}

		public RepositoryRecord getRecord() {
			return record;
		}

		public void setCommit(RevCommit commit) {
			this.commit = commit;
		}

		public RevCommit getCommit() {
			return commit;
		}
	}
}
