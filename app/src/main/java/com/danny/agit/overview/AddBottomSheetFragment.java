package com.danny.agit.overview;
import com.danny.agit.*;
import android.support.design.widget.*;
import android.view.*;
import android.os.*;
import android.support.v7.widget.*;
import android.util.*;
import android.support.v4.app.*;
import android.content.*;
import android.app.*;
import android.widget.*;

public class AddBottomSheetFragment extends BottomSheetDialogFragment
{
	public static final String FRAGMENT_ADD_REPOSITORY_DIALOG = "add repository dialog";
	
	private RecyclerView recyclerView;

	@Override
	public void onAttach(Activity activity) {
		// TODO: Implement this method
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_add_bottom_sheet, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onActivityCreated(savedInstanceState);
		
		// init views
		recyclerView = getView().findViewById(R.id.rclAddBottomSheet);
		
		// init recycler view
		AddButtomSheetRecyclerAdapter adapter = new AddButtomSheetRecyclerAdapter(onItemClick);
		GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.setAdapter(adapter);
	}
	
	private AddButtomSheetRecyclerAdapter.ItemClickListener onItemClick = new AddButtomSheetRecyclerAdapter.ItemClickListener(){
		@Override
		public void onClick(View view) {
			// TODO: Implement this method
			int position = recyclerView.getChildLayoutPosition(view);
			
			Intent it = new Intent();
			it.setClass(getActivity(), AddRepositoryActivity.class);
			
			switch(position) {
				case 0:
					it.putExtra("action", R.string.clone_repository);
					break;
				case 1:
					it.putExtra("action", R.string.create_repository);
					break;
				case 2:
					it.putExtra("action", R.string.import_repository);
					break;
			}
			startActivity(it);
			dismiss();
		}
	};
}
