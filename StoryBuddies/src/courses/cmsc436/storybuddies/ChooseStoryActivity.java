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
					Toast.makeText(getApplicationContext(), "Long click story again to delete", Toast.LENGTH_LONG).show();
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
				startActivity(cyosActivity);
			}
		});
		
		lv.addFooterView(footerViewHolder);
	}
	
	protected void deleteItem(int position) {
		String root_dir = Environment.getExternalStorageDirectory() + "/" + getString(R.string.story_dir);
		File to_del = new File(root_dir, ((StoryBook)mAdapter.getItem(position)).getmTitle());
		if (!StoryBuddiesUtils.removeDirectory(to_del)){
			Toast.makeText(getApplicationContext(), "Error deleting story", Toast.LENGTH_LONG).show();
		}	
		StoryBuddiesBaseActivity.stories.remove(position);
		mAdapter.remove(position);	
	}

	@Override
	public void onResume() {
		super.onResume();
		StoryBuddiesUtils.hideSystemUI(this);
		speech.speak("Would you like to read a story!");
		loadItems();
		//setListAdapter(new ArrayAdapter<String>(this, R.layout.story_list_item,toMyStringArray(StoryBuddiesBaseActivity.stories)));
	}
	
	/*
	 * Loads default items from base activity
	 */
	private void loadItems(){
		mAdapter.removeAllViews();
		ArrayList<StoryBook> toAdd = StoryBuddiesBaseActivity.stories;
		
		toAdd.addAll(getSavedStories());
		
		for (int i = 0; i < toAdd.size(); i++)
		{
			StoryBook cur = toAdd.get(i);
			if (!mAdapter.contains(cur)) {
				mAdapter.add(cur);
			}
		}
	}

	private Collection<? extends StoryBook> getSavedStories() {
		ArrayList<StoryBook> savedStories = new ArrayList<StoryBook>();
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) { 
			File root_dir = new File(Environment.getExternalStorageDirectory() + "/" + getString(R.string.story_dir));
			if (!root_dir.exists() || root_dir.listFiles() == null) {
				return savedStories;
			}
			for (File f : root_dir.listFiles()) {
				if (f.isDirectory()) {
					try {
						savedStories.add(readStory(f.getAbsolutePath()));
					} catch (IOException e) {
						Log.e(TAG, "Error reading story " + f.getName() + ": " + e);
						Toast.makeText(getApplicationContext(), "Error loading story from file: " + f.getName(), Toast.LENGTH_SHORT).show();
					}
				}
			}
		}			
		return savedStories;
	}
	
	private StoryBook readStory(String dir) throws IOException {
		File textFile = new File(dir, getString(R.string.text_file_name) + ".txt");
		FileInputStream is = new FileInputStream(textFile);
		InputStreamReader reader = new InputStreamReader(is);
		JsonReader r = new JsonReader(reader);
		StoryBook story = new StoryBook();
		r.beginObject();
		r.nextName();
		r.nextString();
		story.setmTitle(dir.substring(dir.lastIndexOf("/")+1));//r.nextString());
		r.nextName();
		story.setmAuthor(r.nextString());
		r.nextName();
		r.beginArray();
		while (r.hasNext()) {
			StoryPage page = new StoryPage();
			r.beginObject();
			r.nextName();
			page.setmStoryText(r.nextString());
			if (r.hasNext()) {
				String type = r.nextName();
				if (type.equals("PICTURE")) {
					String fileName = r.nextString();
					Bitmap pic = BitmapFactory.decodeFile(dir + "/" + fileName);
					page.setmPicture(pic);
				}
			}
			r.endObject();
			story.addPage(page);
		}
		r.endArray();
		r.endObject();
		r.close();		
		File cover = new File(dir, getString(R.string.cover_file_name) + ".png");
		if (cover.exists()) {
			Bitmap pic = BitmapFactory.decodeFile(dir + "/" + getString(R.string.cover_file_name) + ".png");
			story.setmTitlePage(pic);
		}
		return story;
	}	

}
