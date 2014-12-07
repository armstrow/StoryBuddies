package courses.cmsc436.storybuddies;

import java.io.ObjectOutputStream.PutField;

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
import android.widget.Toast;

public class CYOS_Title_Screen extends Activity {
	
	protected static final int REQUEST_CODE = 7;
	private SpeechEngine speech;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cyos_title_screen);
		
		speech = SpeechEngine.getInstance(getApplicationContext());
		
		speech.speak("Lets make a new story");
		
		//TODO - title size needs to be widened. Story names are not fitting nicely
		final EditText mTitle = (EditText) findViewById(R.id.CYOS_Title);
		final EditText mName = (EditText) findViewById(R.id.CYOS_Name);
		final Button CYOS_Continue_Button = (Button) findViewById(R.id.CYOS_submit_button);
		
		CYOS_Continue_Button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String currTitle = mTitle.getText().toString();
				String currName = mName.getText().toString();
				
				if(currTitle.equals("") || currName.equals("")){
					Toast.makeText(getApplicationContext(), "Please enter your title and author!", Toast.LENGTH_LONG).show();
				} else {
					Intent creationActivity = new Intent(CYOS_Title_Screen.this,CYOS_Creation_Page.class);
					creationActivity.putExtra("currTitle", currTitle);
					creationActivity.putExtra("currName", currName);
					startActivityForResult(creationActivity, REQUEST_CODE);
				}
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("CYOS_TitleScreen", "In Title_Screen ActivityResult rqCode: " + requestCode + ", result: " + resultCode);
		if (requestCode == REQUEST_CODE) {
			setResult(resultCode, data); //Pass info back to ChooseStoryActivity
			finish();
		}
	}


	@Override
	public void onResume() {
		super.onResume();
		StoryBuddiesUtils.hideSystemUI(this);
	}
}
