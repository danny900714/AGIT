package com.danny.agit.setting;
import android.support.v4.app.*;
import android.os.*;
import android.app.Dialog;
import android.support.v7.app.AlertDialog;
import android.view.*;
import com.danny.agit.*;
import android.support.v7.widget.*;
import java.util.*;
import android.view.View.*;
import android.content.*;
import com.danny.tools.data.person.*;
import android.graphics.drawable.*;
import android.app.Activity;
import com.danny.tools.*;

public class ChoosePeopleDialog extends DialogFragment
{
	public static final String TAG = ChoosePeopleDialog.class.getName();
	public static final String ARG_KEY_PEOPLE_TYPE = "PEOPLE_TYPE";
	public static final int ARG_PEOPLE_AUTHOR = 1;
	public static final int ARG_PEOPLE_COMMITTER = 2;
	public static final int ARG_PEOPLE_TAGGER = 3;
	
	private int peopleType;
	private OnPersonChooseListener listener;
	
	private RecyclerView mRecyclerView;
	private ChoosePeopleDialogRecyclerAdapter adapter;
	
	public void setOnPersonChooseListener(OnPersonChooseListener listener) {
		this.listener = listener;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (listener != null)
			return;
		
		try {
			listener = (OnPersonChooseListener) activity;
		} catch (ClassCastException e) {
			ExceptionUtils.toastException(activity, e);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (getArguments() != null) {
			Bundle args = getArguments();
			peopleType = args.getInt(ARG_KEY_PEOPLE_TYPE, 0);
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_choose_people, null);
		
		// init views
		mRecyclerView = view.findViewById(R.id.dialogChoosePeopleRcl);
		
		// build dialog
		if (peopleType == ARG_PEOPLE_AUTHOR)
			builder.setTitle(R.string.pick_author).setView(view);
		else if (peopleType == ARG_PEOPLE_COMMITTER)
			builder.setTitle(R.string.pick_committer).setView(view);
		else if (peopleType == ARG_PEOPLE_TAGGER)
			builder.setTitle(R.string.pick_tagger).setView(view);
		
		return builder.create();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// get person data
		PersonDao personDao = new PersonDao(getContext());
		List<Person> personList = personDao.getAll();
		
		// init recycler adapter
		adapter = new ChoosePeopleDialogRecyclerAdapter();
		adapter.setOnItemClickListener(onRecyclerViewItemClick);
		ArrayList<ChoosePeopleDialogRecyclerAdapter.Data> dataList = new ArrayList<>();
		for (Person person: personList) {
			ChoosePeopleDialogRecyclerAdapter.Data data = new ChoosePeopleDialogRecyclerAdapter.Data();
			data.setName(person.getName());
			data.setEmail(person.getEmail());
			dataList.add(data);
		}
		adapter.updateAll(dataList);
		
		// init recycler view
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mRecyclerView.setAdapter(adapter);
	}
	
	private View.OnClickListener onRecyclerViewItemClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			int position = mRecyclerView.getChildLayoutPosition(view);
			
			if (position == 0) {
				Intent it = new Intent();
				Bundle params = new Bundle();
				params.putInt(AddPeopleActivity.PARAM_KEY_ACTION, AddPeopleActivity.PARAM_ACTION_ADD);
				it.putExtras(params);
				it.setClass(getContext(), AddPeopleActivity.class);
				startActivity(it);
			} else {
				ChoosePeopleDialogRecyclerAdapter.Data data = adapter.getData(position);
				listener.onPersonChoose(peopleType, data.getProfile(), data.getName(), data.getEmail());
				dismiss();
			}
		}
		
	};
	
	public interface OnPersonChooseListener {
		public void onPersonChoose(int peopleType, Drawable profile, String name, String email);
	}
}
