package courses.cmsc436.storybuddies;

import java.io.ObjectOutputStream.PutField;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CYOS_Title_Screen extends Activity {
	
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
					startActivity(creationActivity);
					finish();
				}
			}
		});
	}


	@Override
	public void onResume() {
		super.onResume();
		StoryBuddiesUtils.hideSystemUI(this);
	}
}
