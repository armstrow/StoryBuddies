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
		StoryBook currStory = StoryBuddiesBaseActivity.stories.get(currStoryPos);
		
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
	public void onResume() {
		super.onResume();
		StoryBuddiesUtils.hideSystemUI(this);
	}
}
