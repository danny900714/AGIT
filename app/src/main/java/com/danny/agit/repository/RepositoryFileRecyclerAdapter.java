package com.danny.agit.repository;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.danny.agit.*;
import java.util.*;
import android.view.View.*;

public class RepositoryFileRecyclerAdapter extends RecyclerView.Adapter<RepositoryFileRecyclerAdapter.ViewHolder>
{
	private ArrayList<Data> dataList;
	private View.OnClickListener onItemClickListener;

	public RepositoryFileRecyclerAdapter(View.OnClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}
	
	public Data getData(int position) {
		return dataList.get(position);
	}

	public void updateDataList(ArrayList<Data> dataList) {
		if (dataList == null)
			this.dataList = new ArrayList<Data>();
		else
			this.dataList = dataList;
		notifyDataSetChanged();
	}

	@Override
	public RepositoryFileRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_repository_file, parent, false);
		if (onItemClickListener != null)
			view.setOnClickListener(onItemClickListener);
		ViewHolder viewHolder = new ViewHolder(view);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(RepositoryFileRecyclerAdapter.ViewHolder viewHolder, int position) {
		Data data = dataList.get(position);
		if (data.isFile) {
			viewHolder.mImgLogo.setBackgroundResource(R.drawable.ic_description_black_24dp);
			viewHolder.mTxtSize.setVisibility(View.VISIBLE);
			viewHolder.mTxtSize.setText(data.size);
		}
		else {
			viewHolder.mImgLogo.setBackgroundResource(R.drawable.ic_folder_black_24dp);
			viewHolder.mTxtSize.setVisibility(View.GONE);
		}
		viewHolder.mTxtName.setText(data.name);
	}

	@Override
	public int getItemCount() {
		return dataList.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder {
		private ImageView mImgLogo;
		private TextView mTxtName, mTxtSize;
		
		public ViewHolder(View view) {
			super(view);
			mImgLogo = view.findViewById(R.id.repositoryFileRclImg);
			mTxtName = view.findViewById(R.id.repositoryFileRclTxtName);
			mTxtSize = view.findViewById(R.id.repositoryFileRclTxtSize);
		}
	}
	
	public static class Data {
		private String name;
		private String size;
		private String path;
		private boolean isFile;
		
		public Data() {

		}

		public Data(String name, String size, String path, boolean isFile)
		{
			this.name = name;
			this.size = size;
			this.path = path;
			this.isFile = isFile;
		}

		public void setPath(String path)
		{
			this.path = path;
		}

		public String getPath()
		{
			return path;
		}

		public void setSize(String size)
		{
			this.size = size;
		}

		public String getSize()
		{
			return size;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public String getName()
		{
			return name;
		}

		public void setIsFile(boolean isFile)
		{
			this.isFile = isFile;
		}

		public boolean isFile()
		{
			return isFile;
		}
	}
}
