package com.danny.agit.repository;
import android.support.v4.app.*;
import android.app.Dialog;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import com.danny.agit.*;
import android.content.*;
import android.support.v7.widget.*;
import android.widget.SearchView.*;
import java.util.*;
import com.danny.tools.git.gitignore.*;
import android.app.Activity;
import com.danny.tools.*;

public class AddLanguageDialog extends DialogFragment
{
	public static final String TAG = AddLanguageDialog.class.getName();
	
	private GitignoreManager manager;
	private List<String> languageList;
	private OnReceiveListener listener;
	
	private SearchView mSearchView;
	private RecyclerView mRecyclerView;
	private AddLanguageRecyclerAdapter adapter;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listener = (OnReceiveListener) activity;
		} catch (ClassCastException e) {
			ExceptionUtils.toastException(activity, e);
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppTheme_AlertDialog_ColorPrimary);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_add_language, null);
		
		// init views
		mSearchView = view.findViewById(R.id.dialogAddLanguageSrh);
		mRecyclerView = view.findViewById(R.id.dialogAddLanguageRcl);
		
		builder.setTitle(R.string.choose_language)
			.setView(view);
		
		return builder.create();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// get language list
		manager = new GitignoreManager(getContext());
		languageList = manager.getLanguageList();
		
		// init recycler adapter
		adapter = new AddLanguageRecyclerAdapter(onItemClick);
		adapter.updateAll(languageList);
		
		// init recyclerview
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mRecyclerView.setAdapter(adapter);
		
		// init listeners
		mSearchView.setOnQueryTextListener(onQuery);
	}
	
	private SearchView.OnQueryTextListener onQuery = new SearchView.OnQueryTextListener() {

		@Override
		public boolean onQueryTextSubmit(String query) {
			List<String> result = query(query);
			adapter.updateAll(result);
			return true;
		}

		@Override
		public boolean onQueryTextChange(String newText) {
			List<String> result = query(newText);
			adapter.updateAll(result);
			return true;
		}
	};
	
	private View.OnClickListener onItemClick = new View.OnClickListener() {
		@Override
		public void onClick(View view){
			int position = mRecyclerView.getChildLayoutPosition(view);
			
			listener.onLanguageReceive(adapter.get(position));
			dismiss();
		}
	};
	
	private List<String> query(String query) {
		List<String> result = new ArrayList<String>();
		
		for (String language: languageList) {
			if (language.toLowerCase().contains(query.toLowerCase()))
				result.add(language);
		}
		
		return result;
	}
	
	public interface OnReceiveListener {
		public void onLanguageReceive(String language);
	}
}
