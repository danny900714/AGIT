package com.danny.agit.repository;
import android.support.v4.app.*;
import java.util.*;
import android.os.*;

public class SwitchBranchPagerAdapter extends FragmentPagerAdapter
{
	private List<Fragment> fragmentList;
	private List<String> titleList;
	
	public SwitchBranchPagerAdapter(FragmentManager manager, List<Fragment>fragmentList) {
		super(manager);
		this.fragmentList = fragmentList;
	}
	
	public void updateAll(List<Fragment> fragmentList) {
		this.fragmentList = fragmentList;
		notifyDataSetChanged();
	}
	
	public void setTitleList(List<String> titleList) {
		this.titleList = titleList;
	}

	@Override
	public int getCount() {
		return fragmentList.size();
	}

	@Override
	public Fragment getItem(int position) {
		return fragmentList.get(position);
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		if (titleList == null)
			return super.getPageTitle(position);
		if (position >= titleList.size())
			return super.getPageTitle(position);

		return titleList.get(position);
	}
	
}
