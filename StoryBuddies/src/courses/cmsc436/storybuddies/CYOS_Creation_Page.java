package courses.cmsc436.storybuddies;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class CYOS_Creation_Page extends Activity {
	
	public final String TAG = "CYOS_Creation_Activity";
	
	StoryBook newStory = new StoryBook();
	Button prevButton;
	Button nextButton;
	Button submitButton;
	EditText storyText;
	
	int currPageNumber = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cyos_creation_screen);
		
		//Get String extras from intent
		String currTitle = getIntent().getStringExtra("currTitle");
		String currName = getIntent().getStringExtra("currName");
		
		newStory.setmTitle(currTitle);
		newStory.setmAuthor(currName);
		
		//Set views to their appropriate id's
		prevButton = (Button) findViewById(R.id.cyosPrevButton);
		nextButton = (Button) findViewById(R.id.cyosNextButton);
		submitButton = (Button) findViewById(R.id.cyosSubmitButton);
		storyText = (EditText) findViewById(R.id.cyosStoryText);
		//TODO - make a view for bitmap
		
		//Set up the first page for editing
		newStory.addPage(new StoryPage());
		
		prevButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered prevButton OnClickListener");
				if(currPageNumber > 0){
					updatePage(-1);
				} else {
					Toast.makeText(getApplicationContext(),"TODO - Go to a title screen clone activity",Toast.LENGTH_LONG).show();
					//TODO - Create a new activity that looks like title screen so user can change title and author
					//	if they wish. Will need to use startActivityforResult
					/*Intent goToCoverIntent = new Intent(StoryPageActivity.this,CoverPageActivity.class);
					goToCoverIntent.putExtra("position", currStoryPos);
					startActivity(goToCoverIntent);
					finish();*/
				}
				
			}
		});
		
		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered nextButton OnClickListener");
				if(currPageNumber < newStory.getmPages().size()-1){
					Log.i(TAG,"going to the next page");
					updatePage(1);	
				} else {
					Log.i(TAG,"Creating a new page");
					newStory.addPage(new StoryPage());
					updatePage(1);
				}			
			}
		});
		
		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered submitButton OnClickListener");
				//TODO - a popup asking if the user wants to continue should be implemented here
				updatePage(0);
				StartScreenActivity.stories.add(newStory);
				finish();
			}
		});
	}

	/*
	 * updates the page to save changes to views in the correct page
	 * updates the page if page is changing
	 * updates views if page has changed
	 * delta is the change in page number -1 for previous 0 for same and 1 for next
	 */
	public void updatePage(int delta){
		List<StoryPage> pageList = newStory.getmPages();
		
		//Save changes to newStory
		pageList.get(currPageNumber).setmStoryText(storyText.getText().toString());
		//TODO - save Bitmap as well
		
		currPageNumber += delta;
		
		//Upload info for newPage
		storyText.setText(pageList.get(currPageNumber).getmStoryText());
		//TODO - update Bitmap as well
		
		return;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
