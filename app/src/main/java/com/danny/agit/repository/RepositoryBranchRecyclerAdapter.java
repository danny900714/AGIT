package com.danny.agit.repository;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import com.danny.agit.*;
import android.view.View.*;
import org.eclipse.jgit.lib.*;
import com.danny.tools.git.branch.*;
import com.danny.tools.git.tag.*;

public class RepositoryBranchRecyclerAdapter extends RecyclerView.Adapter<RepositoryBranchRecyclerAdapter.ViewHolder>
{
	public static final int TYPE_BRANCH = 1;
	public static final int TYPE_TAG = 2;
	
	private List<Ref> refList;
	private List<String> nameList;
	private int type;
	private int remoteIndex;
	private View.OnClickListener onItemClick;
	
	public void setType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	public void updateAll(List<Ref> refList) {
		this.refList = refList;
		if (type == TYPE_BRANCH)
			nameList = BranchUtils.getPureBranchList(refList);
		else if (type == TYPE_TAG)
			nameList = TagUtils.getPureTagList(refList);
		notifyDataSetChanged();
	}
	
	public Ref getItem(int position) {
		return refList.get(position);
	}
	
	public void setOnItemClickListener(View.OnClickListener onItemClick) {
		this.onItemClick = onItemClick;
	}
	
	public void setRemoteIndex(int index) {
		remoteIndex = index;
	}

	@Override
	public RepositoryBranchRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item_repository_branch, viewGroup, false);
		if (onItemClick != null)
			view.setOnClickListener(onItemClick);
		ViewHolder viewHolder = new ViewHolder(view);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(RepositoryBranchRecyclerAdapter.ViewHolder viewHolder, int position) {
		if (type == TYPE_BRANCH) {
			if (position >= remoteIndex)
				viewHolder.mImgIcon.setImageResource(R.drawable.ic_cloud_queue_black_24dp);
			else
				viewHolder.mImgIcon.setImageResource(R.drawable.ic_branch);
		}
		else if (type == TYPE_TAG)
			viewHolder.mImgIcon.setImageResource(R.drawable.ic_label_outline_black_24dp);
		viewHolder.mTxtName.setText(nameList.get(position));
	}

	@Override
	public int getItemCount() {
		return nameList.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder {
		private ImageView mImgIcon;
		private TextView mTxtName;
		
		public ViewHolder(View view) {
			super(view);
			mImgIcon = view.findViewById(R.id.repositoryBranchRclImgIcon);
			mTxtName = view.findViewById(R.id.repositoryBranchRclTxtName);
		}
	}
}
