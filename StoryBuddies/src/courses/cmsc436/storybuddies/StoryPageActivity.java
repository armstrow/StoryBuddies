package courses.cmsc436.storybuddies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class StoryPageActivity extends Activity{
	private final String TAG = "StoryPage";
	private int currPage = 0;
	private SpeechEngine speech;
	private EditText storyText;
	
	private StoryBook currStory;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.story_page);
		storyText = (EditText) findViewById(R.id.text);
		speech = SpeechEngine.getInstance(getApplicationContext());
		
		final int currStoryPos = getIntent().getIntExtra("position",0);
		currStory = StartScreenActivity.stories.get(currStoryPos);
		
		updatePage();
		
		ImageButton prevButton = (ImageButton) findViewById(R.id.lastPage);
		ImageButton nextButton = (ImageButton) findViewById(R.id.nextPage);
		
		prevButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered prevButton OnClickListener");
				if(currPage > 0){
					currPage--;
					updatePage();
				} else {
					Intent goToCoverIntent = new Intent(StoryPageActivity.this,CoverPageActivity.class);
					goToCoverIntent.putExtra("position", currStoryPos);
					startActivity(goToCoverIntent);
					finish();
				}
				
			}
		});
		
		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered nextButton OnClickListener");
				if(currPage < currStory.getmPages().size()-1){
					currPage++;
					updatePage();	
				} else {
					finish();
				}			
			}
		});
	}
	
	//TODO - configure back button to respond the same as prevButton's OnClickListener
	
	private void updatePage(){
		String currText = currStory.getmPages().get(currPage).getmStoryText();
		storyText.setText(currText);
		speech.speak(currText);
		//SetBitmap
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
