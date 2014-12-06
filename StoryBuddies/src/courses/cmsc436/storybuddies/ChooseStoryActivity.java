package courses.cmsc436.storybuddies;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ChooseStoryActivity extends ListActivity {

	private final String TAG = "SB_ChooseStoryActivity";
	private SpeechEngine speech;
	private StoryViewAdapter mAdapter;
	
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
		loadItems();
		//setListAdapter(new ArrayAdapter<String>(this, R.layout.story_list_item,toMyStringArray(StoryBuddiesBaseActivity.stories)));
	}
	
	/*
	 * Loads default items from base activity
	 */
	private void loadItems(){
		mAdapter.removeAllViews();
		ArrayList<StoryBook> toAdd = StoryBuddiesBaseActivity.stories;
		for (int i = 0; i < toAdd.size(); i++)
		{
			mAdapter.add(toAdd.get(i));
		}
	}
}
