package courses.cmsc436.storybuddies;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class GameFindTheRabbitActivity extends Activity {
	private final String TAG = "GameFindTheRabbitActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.title_page);
		//TODO - make new xml layout for game activity
		Log.i(TAG, "Entered Game Activity");
		Log.i(TAG, "Leaving Game Activity");
		//TODO - Create Game Mechanics
		finish();
	}
}
