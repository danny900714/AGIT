package com.danny.agit.repository;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.danny.agit.*;
import android.graphics.drawable.*;
import java.util.*;

public class RepositoryCommitRecyclerAdapter extends RecyclerView.Adapter<RepositoryCommitRecyclerAdapter.ViewHolder>
{
	private ArrayList<Data> dataList;
	
	public void updateDataList(ArrayList<Data> dataList) {
		if (dataList == null)
			this.dataList = new ArrayList<>();
		else
			this.dataList = dataList;
		notifyDataSetChanged();
	}

	@Override
	public RepositoryCommitRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_repository_commit, parent, false);
		ViewHolder viewGroup = new ViewHolder(view);
		return viewGroup;
	}

	@Override
	public void onBindViewHolder(RepositoryCommitRecyclerAdapter.ViewHolder viewHolder, int position) {
		Data data = dataList.get(position);
		if (data.profile != null)
			viewHolder.mImgProfile.setImageDrawable(data.profile);
		viewHolder.mTxtCommit.setText(data.commit);
		viewHolder.mTxtName.setText(data.name);
		viewHolder.mTxtDate.setText(data.date);
	}

	@Override
	public int getItemCount() {
		return dataList.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder {
		private ImageView mImgProfile;
		private TextView mTxtCommit, mTxtName, mTxtDate;
		
		public ViewHolder(View view) {
			super(view);
			mImgProfile = view.findViewById(R.id.repositoryCommitRclImgProfile);
			mTxtCommit = view.findViewById(R.id.repositoryCommitRclTxtCommit);
			mTxtName = view.findViewById(R.id.repositoryCommitRclTxtName);
			mTxtDate = view.findViewById(R.id.repositoryCommitRclTxtDate);
		}
	}
	
	public static class Data {
		private Drawable profile;
		private String commit;
		private String name;
		private String date;
		
		public Data() {
			
		}

		public Data(Drawable profile, String commit, String name, String date)
		{
			this.profile = profile;
			this.commit = commit;
			this.name = name;
			this.date = date;
		}

		public void setProfile(Drawable profile)
		{
			this.profile = profile;
		}

		public Drawable getProfile()
		{
			return profile;
		}

		public void setCommit(String commit)
		{
			this.commit = commit;
		}

		public String getCommit()
		{
			return commit;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public String getName()
		{
			return name;
		}

		public void setDate(String date)
		{
			this.date = date;
		}

		public String getDate()
		{
			return date;
		}
	}
}
