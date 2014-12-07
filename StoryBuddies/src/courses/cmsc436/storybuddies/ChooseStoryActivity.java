package courses.cmsc436.storybuddies;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseStoryActivity extends ListActivity {

	private final String TAG = "SB_ChooseStoryActivity";
	private SpeechEngine speech;
	private StoryViewAdapter mAdapter;
	
	private int deleteClicked = -1;
	
	private static final int REQUEST_CODE = 1;
	public static final String STORY_FILE_LOC = "STORY_FILE";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mAdapter = new StoryViewAdapter(getApplicationContext());
		setListAdapter(mAdapter);

		ListView lv = getListView();
		
		speech = SpeechEngine.getInstance(getApplicationContext());

		// Enable filtering when the user types in the virtual keyboard
		lv.setTextFilterEnabled(true);

		// Set an setOnItemClickListener on the ListView
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				Intent goToBookIntent = new Intent(ChooseStoryActivity.this,CoverPageActivity.class);
				goToBookIntent.putExtra("position", position);
				Log.i(TAG, "Saving "+position+" to extra");
				startActivity(goToBookIntent);
				
				// Display a Toast message indicting the selected item
				//Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
			}
		});
		
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if (deleteClicked != position) {
					Toast.makeText(getApplicationContext(), "Long click story again to delete", Toast.LENGTH_SHORT).show();
					deleteClicked = position;
				} else if (deleteClicked == position) {
					deleteItem(position);
					Toast.makeText(getApplicationContext(), "Story has been deleted", Toast.LENGTH_SHORT).show();
					deleteClicked = -1;
				}
				return true;
			}});
		
		// Put divider between ToDoItems and FooterView
		getListView().setFooterDividersEnabled(true);
		
		LinearLayout footerViewHolder = (LinearLayout) View.inflate(this, R.layout.footer_view, null);
		TextView footerView = (TextView) footerViewHolder.findViewById(R.id.footerView);
		
		
		footerView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.i(TAG,"Entered footerView.OnClickListener.onClick()");
				Intent cyosActivity = new Intent(ChooseStoryActivity.this,CYOS_Title_Screen.class);
				startActivityForResult(cyosActivity, REQUEST_CODE ); //TODO: startForResult
			}
		});
		
		lv.addFooterView(footerViewHolder);
		
		//loadItems();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG, "In ActivityResult rqCode: " + requestCode + ", result: " + resultCode);
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			Log.i(TAG, "here");
			try {
				String newStory = data.getStringExtra(STORY_FILE_LOC);	
				Log.i(TAG, "Got new story from creation" + newStory);
				String dirToRead = getRootDir() + "/" + newStory.replace(" ", "_");
				StoryBook story = StoryBuddiesUtils.readStoryFromDir(this, dirToRead);
				mAdapter.add(story);
			} catch (IOException e) {
				Log.e(TAG, "Error: " + e);
				Toast.makeText(getApplicationContext(), "Error loading new story", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	protected void deleteItem(int position) {
		String root_dir = getRootDir();
		File to_del = new File(root_dir, ((StoryBook)mAdapter.getItem(position)).getmTitle().replace(" ",  "_"));
		if (!StoryBuddiesUtils.removeDirectory(to_del)){
			Toast.makeText(getApplicationContext(), "Error deleting story", Toast.LENGTH_LONG).show();
		}	
		mAdapter.remove(position);	
	}

	private String getRootDir() {
		String root_dir = Environment.getExternalStorageDirectory() + "/" + getString(R.string.story_dir);
		return root_dir;
	}

	@Override
	public void onResume() {
		super.onResume();
		StoryBuddiesUtils.hideSystemUI(this);
		speech.speak("Would you like to read a story!");
		//loadItems();
		//setListAdapter(new ArrayAdapter<String>(this, R.layout.story_list_item,toMyStringArray(StoryBuddiesBaseActivity.stories)));
	}
	
	/*
	 * Loads default items from base activity
	 */
	private void loadItems(){
		//mAdapter.removeAllViews();
		/*ArrayList<StoryBook> toAdd = getSavedStories();
		
		for (int i = 0; i < toAdd.size(); i++)
		{
			StoryBook cur = toAdd.get(i);
			if (!mAdapter.contains(cur)) {
				mAdapter.add(cur);
			}
		}*/
	}



}
