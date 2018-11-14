package com.danny.agit.repository;
import android.support.v4.app.*;
import android.view.*;
import android.os.*;
import com.danny.agit.*;
import java.io.*;
import android.support.v7.widget.*;
import java.util.*;
import android.content.res.*;
import org.eclipse.jgit.revwalk.*;
import com.danny.tools.git.commit.*;
import org.eclipse.jgit.api.errors.*;
import com.danny.tools.*;
import android.text.format.*;

public class RepositoryCommitFragment extends Fragment
{
	public static final String ARG_PATH = "PATH";
	
	private File repositoryFolder;
	private Resources res;
	
	private RecyclerView mRecyclerView;
	private RepositoryCommitRecyclerAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			Bundle args = getArguments();
			String path = args.getString(ARG_PATH);
			repositoryFolder = new File(path);
        }
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_repository_commit, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		// init views
		mRecyclerView = getView().findViewById(R.id.repositoryCommitRcl);
		
		// init resources
		res = getResources();
		
		// init adapter
		adapter = new RepositoryCommitRecyclerAdapter();
		ArrayList<RepositoryCommitRecyclerAdapter.Data> dataList = new ArrayList<>();
		
		// get commit log
		try {
			List<RevCommit> commitList = LogUtils.getAllLogsInverse(repositoryFolder.getPath());
			for (RevCommit commit: commitList) {
				RepositoryCommitRecyclerAdapter.Data data = new RepositoryCommitRecyclerAdapter.Data();
				data.setCommit(commit.getFullMessage());
				data.setName(commit.getAuthorIdent().getName());
				
				// get date format
				long commitTime = commit.getCommitTime();
				commitTime *= 1000;
				Date commitDate = new Date(commitTime);
				String sDate = DateFormat.format("yyyy/MM/dd", commitDate).toString();
				data.setDate(sDate);
				
				dataList.add(data);
			}
		} catch (GitAPIException e) {
			ExceptionUtils.toastException(getContext(), e);
		}
		
		adapter.updateDataList(dataList);
		
		// init recycler view
		LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
		mRecyclerView.setLayoutManager(layoutManager);
		mRecyclerView.setAdapter(adapter);
		mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation()));
	}
}
