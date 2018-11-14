package com.danny.agit.overview;
import com.danny.agit.*;
import android.support.v4.app.*;
import android.view.*;
import android.os.*;
import android.support.design.widget.*;
import android.content.*;
import android.util.*;
import android.widget.*;
import android.support.v7.widget.*;
import org.eclipse.jgit.api.errors.*;
import com.danny.tools.git.commit.*;
import org.eclipse.jgit.api.*;
import com.danny.tools.git.repository.*;
import java.io.*;
import org.eclipse.jgit.revwalk.*;
import java.util.*;
import android.view.View.*;
import com.danny.agit.repository.*;

public class MainFragment extends Fragment
{
	private static final String FRAGMENT_ADD_BOTTOM_SHEET = "add bottom sheet";
	
	private FloatingActionButton fabAdd;
	private RecyclerView mRecyclerView;
	private MainFragmentRecyclerAdapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO: Implement this method
		return inflater.inflate(R.layout.fragment_main, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onActivityCreated(savedInstanceState);
		
		// init views
		fabAdd = getView().findViewById(R.id.fabMainFragment);
		mRecyclerView = getView().findViewById(R.id.mainFragmentRcl);
		
		// init recycler view
		adapter = new MainFragmentRecyclerAdapter(getContext(), onItemClick);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
		DividerItemDecoration divider = new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation());
		mRecyclerView.setLayoutManager(layoutManager);
		mRecyclerView.setAdapter(adapter);
		mRecyclerView.addItemDecoration(divider);
		
		// init listeners
		fabAdd.setOnClickListener(onFabAddClick);
	}

	@Override
	public void onResume() {
		super.onResume();
		adapter.updateData();
	}
	
	private View.OnClickListener onFabAddClick = new View.OnClickListener() {
		@Override
		public void onClick(View p1) {
			// show add buttomsheet
			new AddBottomSheetFragment().show(getActivity().getSupportFragmentManager(), FRAGMENT_ADD_BOTTOM_SHEET);
		}
	};
	
	private View.OnClickListener onItemClick = new View.OnClickListener() {
		@Override
		public void onClick(View view)
		{
			int position = mRecyclerView.getChildLayoutPosition(view);
			MainFragmentRecyclerAdapter recycleAdapter = (MainFragmentRecyclerAdapter) mRecyclerView.getAdapter();
			MainFragmentRecyclerAdapter.Adapter childAdapter = recycleAdapter.getChildData(position);
			
			Intent it = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString(RepositoryActivity.PARAM_NAME, childAdapter.getRecord().getName());
			bundle.putString(RepositoryActivity.PARAM_PATH, childAdapter.getRecord().getPath());
			it.setClass(getContext(), RepositoryActivity.class);
			it.putExtras(bundle);
			startActivity(it);
		}
	};
}
