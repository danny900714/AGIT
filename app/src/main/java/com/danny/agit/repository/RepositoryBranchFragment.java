package com.danny.agit.repository;
import android.support.v4.app.*;
import android.view.*;
import android.os.*;
import com.danny.agit.*;
import android.support.v7.widget.*;
import java.util.*;
import org.eclipse.jgit.lib.*;
import com.danny.tools.git.branch.*;
import org.eclipse.jgit.api.errors.*;
import com.danny.tools.*;
import android.view.View.*;
import com.danny.tools.git.tag.*;

public class RepositoryBranchFragment extends Fragment
{
	public static final String TAG = RepositoryBranchFragment.class.getName();
	public static final String ARG_KEY_PATH = "PATH";
	public static final String ARG_KEY_TYPE = "TYPE";
	public static final int ARG_TYPE_BRANCH = 1;
	public static final int ARG_TYPE_TAG = 2;
	public static final int RES_BRANCH_TITLE = R.string.branches;
	public static final int RES_TAG_TITLE = R.string.tags;
	
	private String argPath;
	private int argType;
	private RepositoryBranchRecyclerAdapter adapter;
	private OnReceiveListener listener;
	
	private RecyclerView mRecyclerView;
	
	public void setOnReceiveListener(OnReceiveListener listener) {
		this.listener = listener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			Bundle args = getArguments();
			argPath = args.getString(ARG_KEY_PATH);
			argType = args.getInt(ARG_KEY_TYPE);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_repository_branch, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// init views
		mRecyclerView = getView().findViewById(R.id.repositoryBranchRcl);
		
		// init adapter
		adapter = new RepositoryBranchRecyclerAdapter();
		adapter.setOnItemClickListener(onItemClick);
		if (argType == ARG_TYPE_BRANCH) {
			adapter.setType(RepositoryBranchRecyclerAdapter.TYPE_BRANCH);
			
			List<Ref> branchList = new ArrayList<>();
			int firstRemoteIndex = 0;
			try {
				branchList.addAll(BranchUtils.getLocalBranches(argPath));
				firstRemoteIndex = branchList.size();
				branchList.addAll(BranchUtils.getRemoteBranches(argPath));
			} catch (GitAPIException e) {
				ExceptionUtils.toastException(getContext(), e);
			}
			
			
			adapter.setRemoteIndex(firstRemoteIndex);
			adapter.updateAll(branchList);
		} else if (argType == ARG_TYPE_TAG) {
			adapter.setType(RepositoryBranchRecyclerAdapter.TYPE_TAG);
			
			List<Ref> tagList = new ArrayList<>();
			List<String> nameList = new ArrayList<>();
			try {
				tagList = TagUtils.getAllTags(argPath);
				nameList.addAll(TagUtils.getPureTagList(tagList));
			} catch (GitAPIException e) {
				ExceptionUtils.toastException(getContext(), e);
			}
			
			adapter.updateAll(tagList);
		}
		
		// init recycler view
		LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
		DividerItemDecoration divider = new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation());
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mRecyclerView.addItemDecoration(divider);
		mRecyclerView.setAdapter(adapter);
	}
	
	private View.OnClickListener onItemClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			int position = mRecyclerView.getChildLayoutPosition(view);
			int type = adapter.getType();
			if (type == RepositoryBranchRecyclerAdapter.TYPE_BRANCH) {
				Ref branch = adapter.getItem(position);
				if (listener != null)
					listener.onDataReceive(type, branch.getName());
			}
		}
	};
	
	public interface OnReceiveListener {
		public void onDataReceive(int type, String name);
	}
}
