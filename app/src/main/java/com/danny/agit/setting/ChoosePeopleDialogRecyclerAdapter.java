package com.danny.agit.setting;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.danny.agit.*;
import android.graphics.drawable.*;
import java.util.*;
import android.support.v4.content.*;
import android.graphics.*;
import android.support.v4.widget.*;
import android.content.res.*;
import android.view.View.*;

public class ChoosePeopleDialogRecyclerAdapter extends RecyclerView.Adapter<ChoosePeopleDialogRecyclerAdapter.ViewHolder>
{
	private View.OnClickListener onItemClick;
	
	private ArrayList<Data> dataList;
	
	public void updateAll(ArrayList<Data> dataList) {
		this.dataList = dataList;
		notifyDataSetChanged();
	}
	
	public Data getData(int position) {
		return dataList.get(position - 1);
	}
	
	public void setOnItemClickListener(View.OnClickListener onClickListener) {
		this.onItemClick = onClickListener;
	}

	@Override
	public ChoosePeopleDialogRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup container, int viewType) {
		View view = LayoutInflater.from(container.getContext()).inflate(R.layout.recycler_item_choose_people, container, false);
		if (onItemClick != null)
			view.setOnClickListener(onItemClick);
		ViewHolder viewHolder = new ViewHolder(view);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(ChoosePeopleDialogRecyclerAdapter.ViewHolder viewHolder, int position) {
		if (position == 0) {
			viewHolder.mImgProfile.setImageResource(R.drawable.ic_add_black_24dp);
			//ImageViewCompat.setImageTintList(viewHolder.mImgProfile, ColorStateList.valueOf(R.color.colorAccent));
			//ImageViewCompat.setImageTintMode(viewHolder.mImgProfile, PorterDuff.Mode.SRC_IN);
			//viewHolder.mImgProfile.setColorFilter(R.color.colorAccent, PorterDuff.Mode.SRC_IN);
			viewHolder.mTxtName.setText(R.string.add_new_people);
			viewHolder.mImgMore.setVisibility(View.GONE);
		} else {
			Data data = dataList.get(position - 1);
			if (data.profile == null) {
				viewHolder.mImgProfile.setImageResource(R.drawable.ic_person_black_24dp);
				// viewHolder.mImgProfile.setColorFilter(R.color.colorAccent, PorterDuff.Mode.SRC_IN);
			} else {
				viewHolder.mImgProfile.setImageDrawable(data.profile);
				// viewHolder.mImgProfile.setColorFilter(null);
			}
			viewHolder.mTxtName.setText(data.name);
			viewHolder.mImgMore.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public int getItemCount() {
		return dataList.size() + 1;
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder {
		private ImageView mImgProfile;
		private ImageView mImgMore;
		private TextView mTxtName;
		
		public ViewHolder(View view) {
			super(view);
			mImgProfile = view.findViewById(R.id.dialogChoosePeopleRclImgProfile);
			mImgMore = view.findViewById(R.id.dialogChoosePeopleRclImgMore);
			mTxtName = view.findViewById(R.id.dialogChoosePeopleRclTxtName);
		}
	}
	
	public static class Data {
		private Drawable profile;
		private String name;
		private String email;
		
		public Data() {
			
		}

		public Data(Drawable profile, String name, String email)
		{
			this.profile = profile;
			this.name = name;
			this.email = email;
		}

		public void setEmail(String email)
		{
			this.email = email;
		}

		public String getEmail()
		{
			return email;
		}

		public void setProfile(Drawable profile)
		{
			this.profile = profile;
		}

		public Drawable getProfile()
		{
			return profile;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public String getName()
		{
			return name;
		}
	}
}
