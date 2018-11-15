package com.danny.agit.repository;
import android.support.v7.widget.*;
import android.view.*;
import java.util.*;
import com.danny.agit.*;
import android.widget.*;
import android.view.View.*;

public class AddLanguageRecyclerAdapter extends RecyclerView.Adapter<AddLanguageRecyclerAdapter.ViewHolder>
{
	private List<String> languageList;
	private View.OnClickListener onItemClick;
	
	public AddLanguageRecyclerAdapter(View.OnClickListener onItemClick) {
		this.onItemClick = onItemClick;
	}
	
	public void updateAll(List<String> languageList) {
		this.languageList = languageList;
		notifyDataSetChanged();
	}
	
	public String get(int position) {
		return languageList.get(position);
	}

	@Override
	public AddLanguageRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item_add_language, viewGroup, false);
		if (onItemClick != null)
			view.setOnClickListener(onItemClick);
		ViewHolder viewHolder = new ViewHolder(view);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(AddLanguageRecyclerAdapter.ViewHolder viewHolder, int position) {
		viewHolder.mTxtLanguage.setText(languageList.get(position));
	}

	@Override
	public int getItemCount() {
		return languageList.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder {
		private TextView mTxtLanguage;
		
		public ViewHolder(View view) {
			super(view);
			mTxtLanguage = view.findViewById(R.id.dialogAddLanguageRclTxt);
		}
	}
}
