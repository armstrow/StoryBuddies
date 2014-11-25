package courses.cmsc436.storybuddies;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseStoryActivity extends ListActivity {

	private final String TAG = "SB_ChooseStoryActivity";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

			
		// Create a new Adapter containing a list of colors
		// Set the adapter on this ListActivity's built-in ListView
		setListAdapter(new ArrayAdapter<String>(this, R.layout.story_list_item,
				getResources().getStringArray(R.array.stories)));

		ListView lv = getListView();

		// Enable filtering when the user types in the virtual keyboard
		lv.setTextFilterEnabled(true);

		// Set an setOnItemClickListener on the ListView
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// Display a Toast message indicting the selected item
				Toast.makeText(getApplicationContext(),
						((TextView) view).getText(), Toast.LENGTH_SHORT).show();
			}
		});
		
		// Put divider between ToDoItems and FooterView
		getListView().setFooterDividersEnabled(true);
		
		TextView footerView = (TextView) View.inflate(this, R.layout.footer_view, null);
		
		getListView().addFooterView(footerView);
		
		footerView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.i(TAG,"Entered footerView.OnClickListener.onClick()");

				Toast.makeText(getApplicationContext(), "Not Implemented", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		hideSystemUI();
	}
	
	// This snippet hides the system bars.
	private void hideSystemUI() {
	    // Set the IMMERSIVE flag.
	    // Set the content to appear under the system bars so that the content
	    // doesn't resize when the system bars hide and show.
        this.getWindow().getDecorView().setSystemUiVisibility(
	            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
	            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
	            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}
}
