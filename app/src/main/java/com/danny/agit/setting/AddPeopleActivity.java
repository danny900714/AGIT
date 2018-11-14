package com.danny.agit.setting;
import android.support.v7.app.*;
import android.os.*;
import com.danny.agit.*;
import android.support.v7.widget.Toolbar;
import android.content.*;
import android.widget.*;
import android.support.design.widget.*;
import android.view.View.*;
import android.view.*;
import com.danny.tools.view.*;
import com.danny.tools.data.person.*;

public class AddPeopleActivity extends AppCompatActivity
{
	public static final String PARAM_KEY_ACTION = "ACTION";
	public static final int PARAM_ACTION_ADD = 1;
	public static final int PARAM_ACTION_EDIT = 2;
	
	private int paramAction;
	
	private Toolbar toolbar;
	private ImageView mImgOk;
	private TextInputLayout mEdtLayName, mEdtLayEmail;
	private EditText mEdtName, mEdtEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_people);
		
		// get param
		Intent it = getIntent();
		paramAction = it.getExtras().getInt(PARAM_KEY_ACTION, 0);
		
		// init views
		toolbar = findViewById(R.id.toolbar);
		mImgOk = findViewById(R.id.addPeopleImgOk);
		mEdtLayName = findViewById(R.id.addPeopleEdtLayName);
		mEdtLayEmail = findViewById(R.id.addPeopleEdtLayEmail);
		mEdtName = findViewById(R.id.addPeopleEdtName);
		mEdtEmail = findViewById(R.id.addPeopleEdtEmail);
		
		// init toolbar
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		switch (paramAction) {
			case PARAM_ACTION_ADD:
				actionBar.setTitle(R.string.add_people);
				break;
			case PARAM_ACTION_EDIT:
				actionBar.setTitle(R.string.edit_people);
				break;
		}
		
		// init listeners
		mImgOk.setOnClickListener(onImgOkClick);
	}

	@Override
	public boolean onNavigateUp() {
		finish();
		return false;
	}
	
	private View.OnClickListener onImgOkClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			EditTextUtils edtUtil = new EditTextUtils(AddPeopleActivity.this);
			if (edtUtil.emptyChecker(mEdtLayName) & edtUtil.emptyChecker(mEdtLayEmail)) {
				String sName = mEdtName.getText().toString();
				String sEmail = mEdtEmail.getText().toString();
				PersonDao personDao = new PersonDao(AddPeopleActivity.this);
				Person person = new Person(sName, sEmail);
				personDao.insert(person);
				finish();
			}
		}
	};
}
