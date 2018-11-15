package com.danny.tools.view.search;
import android.support.v7.app.*;
import android.os.*;
import android.content.*;
import android.app.*;

public class LanguageSearchableActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get the intent, verify the action and get the query
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			
		}
	}
}
