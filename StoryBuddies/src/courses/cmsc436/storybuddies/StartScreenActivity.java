package courses.cmsc436.storybuddies;

import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class StartScreenActivity extends Activity {
	
	private final String TAG = "SB_StartScreen";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.activity_main);
		
		ImageButton startButton = (ImageButton) this.findViewById(R.id.imageButton1);
		startButton.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.i(TAG, "Entered startButton.OnItemClickListener.onItemClick()");
				
				//StoryBuddiesUtils.playMusic(StartScreenActivity.this, "Radioactive.mp3");			
				//speech.speak("Would you like to read a story?");
				
				Intent intent = new Intent(StartScreenActivity.this, ChooseStoryActivity.class);
				startActivity(intent);
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		StoryBuddiesUtils.hideSystemUI(this);
	}
}
