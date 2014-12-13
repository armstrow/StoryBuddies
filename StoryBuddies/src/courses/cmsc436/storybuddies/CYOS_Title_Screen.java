package courses.cmsc436.storybuddies;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class CYOS_Title_Screen extends Activity {
	
	protected static final int REQUEST_CODE = 7;
	private SpeechEngine speech;
	private EditText mTitle;
	private EditText mName;
	private byte[] audio = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cyos_title_screen);
		
		speech = SpeechEngine.getInstance(getApplicationContext());
		
		speech.listen("What would you like to call your story?", this);
		
		//TODO - title size needs to be widened. Story names are not fitting nicely
		mTitle = (EditText) findViewById(R.id.CYOS_Title);
		mName = (EditText) findViewById(R.id.CYOS_Name);
		final Button CYOS_Continue_Button = (Button) findViewById(R.id.CYOS_submit_button);
		
		final ImageView micButton = (ImageView) findViewById(R.id.micImage);
		micButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				speech.listen("What would you like to call your story?", CYOS_Title_Screen.this);
			}
		});
		
		CYOS_Continue_Button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String currTitle = mTitle.getText().toString();
				String currName = mName.getText().toString();
				
				if(currTitle.equals("")){
					//TODO: Create an automatic title and leave title page blank
					Toast.makeText(getApplicationContext(), "Please enter your title!", Toast.LENGTH_LONG).show();
				} else if (storyExists(currTitle)) {
					speech.listen("I'm sorry, I already know that story, can you tell me a different name", CYOS_Title_Screen.this);
				} else {
					Intent creationActivity = new Intent(CYOS_Title_Screen.this,CYOS_Creation_Page.class);
					creationActivity.putExtra("currTitle", currTitle);
					creationActivity.putExtra("currName", currName);
					creationActivity.putExtra("titleAudio", audio);
					startActivityForResult(creationActivity, REQUEST_CODE);
				}
			}
		});
		
		speech.listen("What would you like to call your story?", this);
	}
	
	protected boolean storyExists(String currTitle) {
		for (int i = 0; i < StoryBuddiesBaseActivity.stories.size(); i++) {
			if (StoryBuddiesBaseActivity.stories.get(i).getmTitle().equals(currTitle))
				return true;
		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("CYOS_TitleScreen", "In Title_Screen ActivityResult rqCode: " + requestCode + ", result: " + resultCode);
		if (requestCode == REQUEST_CODE) {
			setResult(resultCode, data); //Pass info back to ChooseStoryActivity
			finish();
		}
		
		if (resultCode == RESULT_OK && requestCode == SpeechEngine.AUDIO_INPUT_ACTIVITY) {
			// the resulting text is in the getExtras:
		    Bundle bundle = data.getExtras();
		    ArrayList<String> matches = bundle.getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
		    mTitle.setText(matches.get(0));
		    // the recording url is in getData:
		    Uri audioUri = data.getData();
		    ContentResolver contentResolver = getContentResolver();
		    try {
				InputStream filestream = contentResolver.openInputStream(audioUri);
				
				//from http://stackoverflow.com/questions/1264709/convert-inputstream-to-byte-array-in-java
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	
				int nRead;
				byte[] bytes = new byte[16384];
	
				while ((nRead = filestream.read(bytes, 0, bytes.length)) != -1) {
				  buffer.write(bytes, 0, nRead);
				}
	
				buffer.flush();
	
				audio = buffer.toByteArray();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}


	@Override
	public void onResume() {
		super.onResume();
		StoryBuddiesUtils.hideSystemUI(this);
	}
}
