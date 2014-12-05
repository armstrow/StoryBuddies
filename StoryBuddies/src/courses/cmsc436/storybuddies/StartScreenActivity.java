package courses.cmsc436.storybuddies;

import java.io.IOException;
import java.nio.charset.Charset;
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
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class StartScreenActivity extends Activity {

	private final String TAG = "SB_Main";
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothBroadcastReceiver mBluetooth;
	
	private final String MAC_ADDR="30:22:00:2E:BF:E9";
	
	private boolean bluetoothWasEnabled = true;
	
	// AudioManager
	private AudioManager mAudioManager;
	private SpeechEngine speech;
	
	//Static variable used throughout the app
	public static ArrayList<StoryBook> stories = new ArrayList<StoryBook>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		loadStories();
		
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
		
		speech = SpeechEngine.getInstance(getApplicationContext());
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter != null) {
			mBluetooth = new BluetoothBroadcastReceiver(mBluetoothAdapter, getBaseContext(), MAC_ADDR);
			
		    registerReceiver(mBluetooth, new IntentFilter(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED));
		    registerReceiver(mBluetooth, new IntentFilter(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED));
		    registerReceiver(mBluetooth, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
		    registerReceiver(mBluetooth, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
			
			if (!mBluetoothAdapter.isEnabled()) {
				bluetoothWasEnabled = false;
				if (!mBluetoothAdapter.enable()) {
					Log.e(TAG, "Could not enable bluetooth");
				}				
			    //Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			    //startActivityForResult(enableBtIntent, 0);
			}	
			else {
				mBluetooth.connect();
			}
		}
		else {
			Log.i(TAG, "bluetooth null");
		}
		
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		StoryBuddiesUtils.hideSystemUI(this);
		
		mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		mAudioManager.setSpeakerphoneOn(true);
		
	    if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(getIntent().getAction())) {
	    	Log.i(TAG, "Started because of NFC tag");
	    	Tag tag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
	    	byte[] a = tag.getId();
    	    StringBuilder sb = new StringBuilder(a.length * 2);
    	    for(byte b: a)
    	       sb.append(String.format("%02x", b & 0xff));
    	    //"224a3c13"
    	    Log.i(TAG, "nfc tag no: " + sb);
    	   
    	    //writeTag(tag, "HELLO");
    	    //readTag(tag);
    	    Toast.makeText(getApplicationContext(), sb, Toast.LENGTH_SHORT).show();
	    }
	}
	
	@Override
	public void onPause() {
		
		super.onPause();
		// Close proxy connection after use.
		//mBluetoothAdapter.closeProfileProxy(0, mBluetoothSpeaker);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mBluetooth.disconnect();
		if (!bluetoothWasEnabled) {
			Log.i(TAG, "Re-disabling bluetooth");
			mBluetoothAdapter.disable();
		}
	    unregisterReceiver(mBluetooth);
	    
	    deleteStories();
	}
	
	private void loadStories(){
		loadBuiltInStories();
		loadInternalStories();
	}
	
	private void loadBuiltInStories(){
		//Build test Story
		StoryBook testStory = new StoryBook("FirstTestStory");
		
		StoryPage page1 = new StoryPage(null, "the first pages text");
		StoryPage page2 = new StoryPage(null, "the second pages text");
		StoryPage page3 = new StoryPage(null, "The End");
		
		testStory.addPage(page1);
		testStory.addPage(page2);
		testStory.addPage(page3);
		
		//Build Turtle and the Hare
		StoryBook book1 = new StoryBook("The Tortoise and the Hare");
		book1.addPage(new StoryPage(null, "first text of tortoise and the Hare"));
		book1.addPage(new StoryPage(null, "second text of tortoise and the Hare"));
		book1.addPage(new StoryPage(null, "The end of tortoise and the Hare"));
		
		//Add stories to ArrayList
		stories.add(testStory);
		stories.add(book1);
		
		
		//TODO - Create all the built in stories for the particular animal we are connected to
		//	and add them to the list of StoryBooks
	}
	
	private void loadInternalStories(){
		//TODO - Load any previously created stories and add them to the List of StoryBooks
	}
	
	private void deleteStories(){
		stories.clear();
	}
	
	//adapted from http://developer.android.com/guide/topi cs/connectivity/nfc/advanced-nfc.html 
    /*public void writeTag(Tag tag, String tagText) {
        MifareClassic mifare = MifareClassic.get(tag);
        if (tagText.length() > 16)
        	Log.e(TAG, "Text length must be < 16");
        try {
        	mifare.connect();
        	mifare.writeBlock(0, tagText.getBytes());
        	Log.i(TAG, "Text: " + tagText + " written to tag");
        } catch (IOException e) {
            Log.e(TAG, "IOException while closing MifareUltralight...", e);
        } finally {
            try {
                mifare.close();
            } catch (IOException e) {
                Log.e(TAG, "IOException while closing MifareUltralight...", e);
            }
        }
    }
	
	
	public String readTag(Tag tag) {
        NfcA mifare = NfcA.get(tag);
        try {
        	String s = "";
            mifare.connect();
            Log.i(TAG, "Connected");
            for (int i = 0; i < mifare.getBlockCount(); i++) {
            	byte[] payload = mifare.readBlock(i);
            	s += new String(payload, Charset.forName("US-ASCII"));
            }         
            return s;
        } catch (IOException e) {
            Log.e(TAG, "IOException while writing MifareClassic message...", e);
        } finally {
            if (mifare != null) {
               try {
                   mifare.close();
               }
               catch (IOException e) {
                   Log.e(TAG, "Error closing tag...", e);
               }
            }
        }
        return null;
    }*/
	

}
