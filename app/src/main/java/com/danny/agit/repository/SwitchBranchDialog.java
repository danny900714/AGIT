package com.danny.agit.repository;
import android.support.v4.app.*;
import android.os.*;
import android.app.Dialog;
import android.support.v7.app.*;
import com.danny.agit.*;
import android.view.*;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.*;
import android.support.v4.view.*;
import android.widget.*;
import java.util.*;
import android.view.View.*;

public class SwitchBranchDialog extends DialogFragment
{
	public static final String TAG = SwitchBranchDialog.class.getName();
	public static final String ARG_KEY_PATH = "PATH";
	
	private String argPath;
	private SwitchBranchPagerAdapter adapter;
	private OnReceiveListener listener;
	
	private TabLayout mTabLayout;
	private ViewPager mViewPager;
	private ImageView mImgClose;
	
	public void setOnReceiveListener(OnReceiveListener listener) {
		this.listener = listener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			Bundle args = getArguments();
			argPath = args.getString(ARG_KEY_PATH);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		return inflater.inflate(R.layout.dialog_switch_branch, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// init views
		mTabLayout = getView().findViewById(R.id.tabLayout);
		mViewPager = getView().findViewById(R.id.dialogSwitchBranchViewPager);
		mImgClose = getView().findViewById(R.id.dialogSwitchBranchImgClose);
		
		// init branch fragment
		RepositoryBranchFragment branchFragment = new RepositoryBranchFragment();
		Bundle argsBranch = new Bundle();
		argsBranch.putString(RepositoryBranchFragment.ARG_KEY_PATH, argPath);
		argsBranch.putInt(RepositoryBranchFragment.ARG_KEY_TYPE, RepositoryBranchFragment.ARG_TYPE_BRANCH);
		branchFragment.setArguments(argsBranch);
		branchFragment.setOnReceiveListener(onReceive);
		
		// init tag fragment
		RepositoryBranchFragment tagFragment = new RepositoryBranchFragment();
		Bundle argsTag = new Bundle();
		argsTag.putString(RepositoryBranchFragment.ARG_KEY_PATH, argPath);
		argsTag.putInt(RepositoryBranchFragment.ARG_KEY_TYPE, RepositoryBranchFragment.ARG_TYPE_TAG);
		tagFragment.setArguments(argsTag);
		branchFragment.setOnReceiveListener(onReceive);

		// init adapter
		List<Fragment> pageList = new ArrayList<>();
		pageList.add(branchFragment);
		pageList.add(tagFragment);
		List<String> titleList = new ArrayList<>();
		titleList.add(getContext().getString(RepositoryBranchFragment.RES_BRANCH_TITLE));
		titleList.add(getContext().getString(RepositoryBranchFragment.RES_TAG_TITLE));
		adapter = new SwitchBranchPagerAdapter(getChildFragmentManager(), pageList);
		adapter.setTitleList(titleList);

		// init view pager
		mViewPager.setAdapter(adapter);

		// init tab layout
		mTabLayout.setupWithViewPager(mViewPager);

		// init tab listener
		mTabLayout.addOnTabSelectedListener(onTabSelectedListener);
		mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
		
		// init listeners
		mImgClose.setOnClickListener(onImgCloseClick);
	}

	@Override
	public void onStart() {
		super.onStart();
		getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	}
	
	private RepositoryBranchFragment.OnReceiveListener onReceive = new RepositoryBranchFragment.OnReceiveListener() {
		@Override
		public void onDataReceive(int type, String name) {
			if (listener != null) {
				if (type == RepositoryBranchFragment.ARG_TYPE_BRANCH)
					listener.onBranchReceive(name);
			}
			dismiss();
		}
	};
	
	private View.OnClickListener onImgCloseClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			dismiss();
		}
	};

	private TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
		@Override
		public void onTabSelected(TabLayout.Tab tab) {
			// TODO: Implement this method
			mViewPager.setCurrentItem(tab.getPosition());
		}

		@Override
		public void onTabUnselected(TabLayout.Tab p1) {
			// TODO: Implement this method
		}

		@Override
		public void onTabReselected(TabLayout.Tab p1) {
			// TODO: Implement this method
		}
	};
	
	public interface OnReceiveListener {
		public void onBranchReceive(String name);
	}
}
