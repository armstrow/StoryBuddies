package courses.cmsc436.storybuddies;

import java.io.File;
import java.io.IOException;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseStoryActivity extends ListActivity {

	private final String TAG = "SB_ChooseStoryActivity";
	private SpeechEngine speech;
	private StoryViewAdapter mAdapter;
	
	private int deleteClicked = -1;
	
	private static final int REQUEST_CODE = 1;
	public static final String STORY_FILE_LOC = "STORY_FILE";
	
	private boolean cyosClicked = false;
	private View footerViewHolder = null;
	private ImageButton cyosButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mAdapter = new StoryViewAdapter(getApplicationContext());
		setListAdapter(mAdapter);

		ListView lv = getListView();
		
		speech = SpeechEngine.getInstance(getApplicationContext());

		// Set an setOnItemClickListener on the ListView
		//lv.setOnItemClickListener(getOnItemClickListener());
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		lv.setOnItemLongClickListener(getOnItemLongClickListener());
		
		// Put divider between Items and FooterView
		getListView().setFooterDividersEnabled(true);
		
		footerViewHolder = (RelativeLayout) View.inflate(this, R.layout.footer_view, null);
		TextView footerView = (TextView) footerViewHolder.findViewById(R.id.footerView);
		
		cyosButton = (ImageButton) footerViewHolder.findViewById(R.id.footerButton);
		cyosButton.setOnClickListener(getFooterOnClickListener());
		

		footerView.setOnClickListener(getFooterOnClickListener());
		
		lv.addFooterView(footerViewHolder);
		//loadItems();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		cyosClicked = false;
		//footerViewHolder.setBackgroundColor(getResources().getColor(R.color.deselected));
		cyosButton.getBackground().setColorFilter(getResources().getColor(R.color.deselected), Mode.MULTIPLY);
		if (mAdapter.getSelectedItem() != position) {
			String storyTitle = ((StoryBook)mAdapter.getItem(position)).getmTitle();
			Log.i(TAG, "Selected story: " + storyTitle);
			
			String titleAudio = ((StoryBook)mAdapter.getItem(position)).getmTitleAudio();
			if (titleAudio != null) {
				speech.playMusic(titleAudio);
			} else {
				speech.speak(storyTitle);
			}
			
			speech.pauseThenSpeak(4000, "Touch again to start reading");
			
			mAdapter.setSelectedItem(position);
			mAdapter.notifyDataSetChanged();
		}
		else {
			mAdapter.setSelectedItem(-1);
			Intent goToBookIntent = new Intent(ChooseStoryActivity.this,CoverPageActivity.class);
			goToBookIntent.putExtra("position", position);
			Log.i(TAG, "Saving "+position+" to extra");
			startActivity(goToBookIntent);
		}
	}

	private OnClickListener getFooterOnClickListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered footerView.OnClickListener.onClick()");
				if (cyosClicked) {					
					cyosClicked = false;
					cyosButton.getBackground().setColorFilter(getResources().getColor(R.color.deselected), Mode.MULTIPLY);
					//footerViewHolder.setBackgroundColor(getResources().getColor(R.color.deselected));
					Intent cyosActivity = new Intent(ChooseStoryActivity.this,CYOS_Title_Screen.class);
					startActivityForResult(cyosActivity, REQUEST_CODE ); 
				} else {
					cyosClicked = true;
					speech.speak("Let's make a new story!");
					speech.pauseThenSpeak(1000, "Touch again to start");
					cyosButton.getBackground().setColorFilter(getResources().getColor(R.color.selected), Mode.MULTIPLY);
					//footerViewHolder.setBackgroundColor(getResources().getColor(R.color.selected));
					mAdapter.setSelectedItem(-1);
					mAdapter.notifyDataSetChanged();
				}
			}
		};
	}


	private OnItemLongClickListener getOnItemLongClickListener() {
		return new OnItemLongClickListener() {

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
			}};
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
				story.setmTitlePage(R.drawable.cyos_icon);
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
			Toast.makeText(getApplicationContext(), getString(R.string.error_delete), Toast.LENGTH_LONG).show();
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
		speech.speak(getString(R.string.read_story));
		mAdapter.setSelectedItem(-1);
		mAdapter.notifyDataSetChanged();
		cyosClicked = false;
		cyosButton.getBackground().setColorFilter(getResources().getColor(R.color.deselected), Mode.MULTIPLY);
		//footerViewHolder.setBackgroundColor(getResources().getColor(R.color.deselected));
		//loadItems();
		//setListAdapter(new ArrayAdapter<String>(this, R.layout.story_list_item,toMyStringArray(StoryBuddiesBaseActivity.stories)));
	}
	
}
