package com.danny.agit.overview;
import com.danny.agit.*;
import android.support.v7.widget.*;
import android.widget.*;
import android.view.*;
import java.util.*;
import android.view.View.*;

public class AddButtomSheetRecyclerAdapter extends RecyclerView.Adapter<AddButtomSheetRecyclerAdapter.ViewHolder>
{
	private List<Item> itemList;
	private int[] drawableArray = new int[]{R.drawable.ic_file_download_black_48dp, R.drawable.ic_add_black_48dp, R.drawable.ic_folder_open_black_48dp};
	private int[] stringArray = new int[]{R.string.clone_repository, R.string.create_repository, R.string.import_repository};
	
	private ItemClickListener itemClickListener;
	
	public AddButtomSheetRecyclerAdapter(ItemClickListener itemClickListener) {
		// init list
		List<Item> itemList = new ArrayList<Item>();
		for (int i = 0; i < 3; i++) {
			Item item = new Item(drawableArray[i], stringArray[i]);
			itemList.add(item);
		}
		this.itemList = itemList;
		
		this.itemClickListener = itemClickListener;
	}

	public AddButtomSheetRecyclerAdapter(List<Item> itemList, ItemClickListener itemClickListener) {
		this.itemList = itemList;
		this.itemClickListener = itemClickListener;
	}

	@Override
	public AddButtomSheetRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_add_buttom_sheet, parent, false);
		view.setOnClickListener(itemClickListener);
		ViewHolder viewHolder = new ViewHolder(view);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(AddButtomSheetRecyclerAdapter.ViewHolder holder, int position) {
		// handle init things
		holder.img.setImageResource(itemList.get(position).drawable);
		holder.txt.setText(itemList.get(position).string);
	}

	@Override
	public int getItemCount() {
		return itemList.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder {
		private ImageView img;
		private TextView txt;
		
		public ViewHolder(View view) {
			super(view);
			img = view.findViewById(R.id.imgAddButtomSheetRclItem);
			txt = view.findViewById(R.id.txtAddButtomSheetRclItem);
		}
	}
	
	public class Item {
		public int drawable;
		public int string;

		public Item(int drawable, int string) {
			this.drawable = drawable;
			this.string = string;
		}
	}
	
	public interface ItemClickListener extends View.OnClickListener {
		@Override
		public void onClick(View view);
	}
}
