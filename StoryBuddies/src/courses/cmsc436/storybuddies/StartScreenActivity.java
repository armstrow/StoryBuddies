package courses.cmsc436.storybuddies;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.AdapterView.OnItemClickListener;

public class StartScreenActivity extends Activity {

	private final String TAG = "SB_Main";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	
		ImageButton startButton = (ImageButton) this.findViewById(R.id.imageButton1);
		startButton.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.i(TAG, "Entered startButton.OnItemClickListener.onItemClick()");
				
				Intent intent = new Intent(StartScreenActivity.this, ChooseStoryActivity.class);
				startActivity(intent);
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		hideSystemUI();
	}
	
	// This snippet hides the system bars.
	private void hideSystemUI() {
	    // Set the IMMERSIVE flag.
	    // Set the content to appear under the system bars so that the content
	    // doesn't resize when the system bars hide and show.
        this.getWindow().getDecorView().setSystemUiVisibility(
	            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
	            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
	            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}

}
