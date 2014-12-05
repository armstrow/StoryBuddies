package courses.cmsc436.storybuddies;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class CoverPageActivity extends Activity {
	
	private final String TAG = "CoverPage";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.title_page);
		Log.i(TAG, "Entered Cover Page onCreate");
		
		StoryBook currStory = ChooseStoryActivity.stories.get(getIntent().getIntExtra("position",0));
		
		Log.i(TAG, "Got StoryBook Intent");
		
		TextView title = (TextView) findViewById(R.id.coverPageTitle);
		title.setText(currStory.getmTitle());
		
		//title.setText(currStory.getmTitle());
		Log.i(TAG, "Set textView to current Title");
		
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
