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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseStoryActivity extends ListActivity {

	private final String TAG = "SB_ChooseStoryActivity";
	private SpeechEngine speech;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Create a new Adapter containing a list of colors
		// Set the adapter on this ListActivity's built-in ListView
		
		//I do this again in onResume to update when a new story is added
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, R.layout.story_list_item,toMyStringArray(StartScreenActivity.stories));
		
		setListAdapter(mAdapter);

		ListView lv = getListView();
		
		speech = SpeechEngine.getInstance(getApplicationContext());

		// Enable filtering when the user types in the virtual keyboard
		lv.setTextFilterEnabled(true);

		// Set an setOnItemClickListener on the ListView
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				Log.i(TAG,"Entered onItemClickListener for " + ((TextView) view).getText());
				Intent goToBookIntent = new Intent(ChooseStoryActivity.this,CoverPageActivity.class);
				goToBookIntent.putExtra("position", position);
				Log.i(TAG, "Saving "+position+" to extra");
				startActivity(goToBookIntent);
				
				// Display a Toast message indicting the selected item
				//Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
			}
		});
		
		// Put divider between ToDoItems and FooterView
		getListView().setFooterDividersEnabled(true);
		
		LinearLayout footerViewHolder = (LinearLayout) View.inflate(this, R.layout.footer_view, null);
		TextView footerView = (TextView) footerViewHolder.findViewById(R.id.footerView);
		
		
		footerView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.i(TAG,"Entered footerView.OnClickListener.onClick()");
				Intent cyosActivity = new Intent(ChooseStoryActivity.this,CYOS_Title_Screen.class);
				startActivity(cyosActivity);
			}
		});
		
		lv.addFooterView(footerViewHolder);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		StoryBuddiesUtils.hideSystemUI(this);
		speech.speak("Would you like to read a story!");
		setListAdapter(new ArrayAdapter<String>(this, R.layout.story_list_item,toMyStringArray(StartScreenActivity.stories)));
	}
	
	/*
	 * Takes a List<StoryBook> and converts it to a String Array of title names
	 */
	private String[] toMyStringArray(List<StoryBook> myList){
		int listSize = myList.size();
		String[] toReturn = new String[listSize];
		
		for(int i=0; i<listSize;i++){
			toReturn[i] = myList.get(i).toString();
		}
		
		return toReturn;
	}
}
