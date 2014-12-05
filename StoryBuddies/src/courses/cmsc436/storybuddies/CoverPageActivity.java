package courses.cmsc436.storybuddies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class CoverPageActivity extends Activity {
	
	private final String TAG = "CoverPage";
	private SpeechEngine speech;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.title_page);
		
		speech = SpeechEngine.getInstance(getApplicationContext()); 
		
		//Get currentStory from static variable
		final int currStoryPos = getIntent().getIntExtra("position",0);
		StoryBook currStory = StartScreenActivity.stories.get(currStoryPos);
		
		speech.speak(currStory.toString());
		
		//Set title view to appropriate text
		TextView title = (TextView) findViewById(R.id.coverPageTitle);
		title.setText(currStory.getmTitle());
		
		//TODO - set author to appropriate text
		
		ImageView coverView = (ImageView) findViewById(R.id.bookCover);
		
		coverView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG,"Entered coverView OnClickListener");
				Intent storyActivity = new Intent(CoverPageActivity.this,StoryPageActivity.class);
				storyActivity.putExtra("position", currStoryPos);
				startActivity(storyActivity);
				finish();
			}
		});
		
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
