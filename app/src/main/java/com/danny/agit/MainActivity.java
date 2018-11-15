package com.danny.agit;

import android.app.*;
import android.os.*;
import org.eclipse.jgit.api.*;
import java.io.*;
import org.eclipse.jgit.api.errors.*;
import android.support.design.widget.*;
import android.support.v7.widget.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.view.*;
import android.support.v4.view.*;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.*;
import android.view.View.*;
import com.danny.agit.overview.*;
import android.widget.Toast;
import android.util.*;
import android.support.v4.content.*;
import android.content.pm.*;
import android.*;
import java.util.*;
import android.support.v4.app.ActivityCompat;
import com.danny.tools.git.gitignore.*;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    
	private static final String FRAGMENT_MAIN = "main";
	private static final int REQUEST_PERMISSION = 500;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
		// handle permision request
		handlePermission();
		
		// init toolbar
		Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
		
		// init drawer toggle
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
		
		// init navigation drawer item selected liatener
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
		
		// set main fragment to frame layout
		FragmentManager frmMgr = getSupportFragmentManager();
		FragmentTransaction frmTrans = frmMgr.beginTransaction();
		MainFragment mainFragment = new MainFragment();
		frmTrans.replace(R.id.mainFrameLay, mainFragment, FRAGMENT_MAIN);
		frmTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		frmTrans.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
		else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
	{
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent it = new Intent();
        switch (id) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        //drawer.closeDrawer(GravityCompat.START);
        drawer.closeDrawers();
        return true;
    }
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case REQUEST_PERMISSION:
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// permission was granted, yay! Do the
					// contacts-related task you need to do.
				} else {
					// permission denied, boo! Disable the
					// functionality that depends on this permission.
					handlePermission();
				}
				return;
				// other 'case' lines to check for other
				// permissions this app might request.
		}
	}
	
	private void handlePermission() {
		ArrayList<String> permissionList = new ArrayList<>();
		ArrayList<String> requestingPermissionList = new ArrayList<>();
		
		permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		
		for (int i = 0; i < permissionList.size(); i++) {
			if (ContextCompat.checkSelfPermission(MainActivity.this, permissionList.get(i)) != PackageManager.PERMISSION_GRANTED)
				requestingPermissionList.add(permissionList.get(i));
		}
		
		if (requestingPermissionList.size() == 0)
			return;
		
		String[] requestingPermissionArray = new String[requestingPermissionList.size()];
		requestingPermissionArray = requestingPermissionList.toArray(requestingPermissionArray);
		
		ActivityCompat.requestPermissions(MainActivity.this, requestingPermissionArray, REQUEST_PERMISSION);
	}
}
