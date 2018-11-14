package com.danny.agit.repository;
import android.support.v4.app.*;
import android.view.*;
import android.os.*;
import com.danny.agit.*;
import android.support.v7.widget.*;
import java.util.ArrayList;
import android.text.format.Formatter;
import java.io.*;
import android.content.*;
import android.util.*;
import java.util.*;
import android.view.View.*;
import android.app.Activity;
import android.net.*;
import android.webkit.*;
import com.danny.tools.*;

public class RepositoryFileFragment extends Fragment
{
	public static final String ARG_PATH = "PATH";
	
	private String argPath;
	private String argPathParent;
	private File parent;
	
	private RepositoryFileRecyclerAdapter adapter;
	private RecyclerView mRecyclerView;
	
	public static RepositoryFileFragment newInstance(String param1, String param2) {
        RepositoryFileFragment fragment = new RepositoryFileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

	/*@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		try {
			mListener = (InteractionListener) activity;
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
	}*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
			Bundle args = getArguments();
			argPath = args.getString(ARG_PATH);
			File file = new File(argPath);
			argPathParent = file.getParent();
        }
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_repository_file, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onActivityCreated(savedInstanceState);
		
		// init views
		mRecyclerView = getView().findViewById(R.id.repositoryFileRcl);
		
		// init recycler view
		adapter = new RepositoryFileRecyclerAdapter(onItemClick);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mRecyclerView.setAdapter(adapter);
		// set dataList
		ArrayList<RepositoryFileRecyclerAdapter.Data> dataList = getDataList(argPath);
		adapter.updateDataList(dataList);
	}
	
	boolean onBackPressed() {
		if (parent == null || parent.getPath().equals(argPathParent))
			return false;
		
		ArrayList<RepositoryFileRecyclerAdapter.Data> dataList = getDataList(parent.getPath());
		adapter.updateDataList(dataList);
		parent = parent.getParentFile();
		return true;
	}
	
	private ArrayList<RepositoryFileRecyclerAdapter.Data> getDataList(String folderPath) {
		ArrayList<RepositoryFileRecyclerAdapter.Data> rawList = new ArrayList<>();
		File folder = new File(folderPath);
		if (!folder.isDirectory())
			return null;
		
		File[] folderChildArray = folder.listFiles();
		for (File file: folderChildArray) {
			String sSize = Formatter.formatShortFileSize(getContext(), file.length());
			RepositoryFileRecyclerAdapter.Data data = new RepositoryFileRecyclerAdapter.Data(file.getName(), sSize, file.getPath(), file.isFile());
			rawList.add(data);
		}
		return getSortedDataList(rawList);
	}
	
	private ArrayList<RepositoryFileRecyclerAdapter.Data> getSortedDataList(ArrayList<RepositoryFileRecyclerAdapter.Data> dataList) {
		ArrayList<RepositoryFileRecyclerAdapter.Data> folderList = new ArrayList<>();
		ArrayList<RepositoryFileRecyclerAdapter.Data> fileList = new ArrayList<>();
		
		// classify to two list
		for (RepositoryFileRecyclerAdapter.Data data: dataList) {
			if (data.getName().equals(".git"))
				continue;
			
			if (data.isFile())
				fileList.add(data);
			else {
				data.setSize(null);
				folderList.add(data);
			}
		}
		
		// sort list by name
		Collections.sort(folderList, new DataComparator());
		Collections.sort(fileList, new DataComparator());
		
		folderList.addAll(fileList);
		return folderList;
	}
	
	private View.OnClickListener onItemClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			int position = mRecyclerView.getChildLayoutPosition(view);
			RepositoryFileRecyclerAdapter.Data data = adapter.getData(position);
			File file = new File(data.getPath());
			
			if (file.isFile()) {
				String mimeType = AndroidFileUtils.getMimeType(file.getPath());
				Intent it = new Intent(Intent.ACTION_VIEW);
				if (mimeType == null)
					it.setDataAndType(Uri.fromFile(file), "*/*");
				else
					it.setDataAndType(Uri.fromFile(file), mimeType);
				startActivity(it);
			} else {
				ArrayList<RepositoryFileRecyclerAdapter.Data> dataList = getDataList(data.getPath());
				adapter.updateDataList(dataList);
				parent = file.getParentFile();
			}
		}
	};
	
	private class DataComparator implements Comparator<RepositoryFileRecyclerAdapter.Data> {
		@Override
		public int compare(RepositoryFileRecyclerAdapter.Data data1, RepositoryFileRecyclerAdapter.Data data2) {
			return data1.getName().compareTo(data2.getName());
		}
	}
	
	/*public interface InteractionListener {
		
	}*/
}
