package com.danny.agit.repository;
import android.support.v4.app.*;
import java.util.*;
import android.content.res.*;

public class RepositoryPagerAdapter extends FragmentPagerAdapter
{
	private ArrayList<Fragment> fragmentList;
	private ArrayList<String> titleList;
	
	public RepositoryPagerAdapter(FragmentManager manager, ArrayList<Fragment> fragmentList) {
		super(manager);
		this.fragmentList = fragmentList;
	}
	
	@Override
	public int getCount()
	{
		return fragmentList.size();
	}

	@Override
	public Fragment getItem(int position)
	{
		return fragmentList.get(position);
	}

	@Override
	public CharSequence getPageTitle(int position)
	{
		if (titleList == null)
			return super.getPageTitle(position);
		if (position >= titleList.size())
			return super.getPageTitle(position);
		
		return titleList.get(position);
	}
	
	public void setTitleList(ArrayList<String> titleList) {
		this.titleList = titleList;
	}
}
