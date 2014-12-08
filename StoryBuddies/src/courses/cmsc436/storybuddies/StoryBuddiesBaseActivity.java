package courses.cmsc436.storybuddies;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ndeftools.Message;
import org.ndeftools.Record;
import org.ndeftools.wellknown.TextRecord;
import org.ndeftools.externaltype.AndroidApplicationRecord;

import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.AudioManager;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class StoryBuddiesBaseActivity extends Activity {
	
	public static ArrayList<StoryBook> stories = new ArrayList<StoryBook>();
	private final String TAG = "Story_Buddies";
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothBroadcastReceiver mBluetooth;
	private final int IMAGE_MAX_SIZE = 600;
	
	private String myMacAddr = null;
	private String myAnimal = null;
		
	// AudioManager
	private AudioManager mAudioManager;
	private SpeechEngine speech;
	
	//Static variable used throughout the app
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//nfcOnCreate(savedInstanceState);
		Log.i(TAG, "Entered StoryBuddiesBaseActivity: onCreate");
		loadStories();
			
		speech = SpeechEngine.getInstance(getApplicationContext());
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter != null) {
			mBluetooth = BluetoothBroadcastReceiver.getInstance();
			if (!mBluetooth.isInitialized()) {
				setUpBluetooth();
			}
		}
		else {
			Log.i(TAG, "bluetooth null");
		}	
		
		Intent intent = new Intent(this, StartScreenActivity.class);
		startActivityForResult(intent, 0);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "Entered StoryBuddiesBaseActivity: onActivityResult");
		finish();
	}

	private void setUpBluetooth() {
		Log.i(TAG, "Entered StoryBuddiesBaseActivity: setUpBluetooth");
		mBluetooth.initialize(mBluetoothAdapter, getBaseContext());
		
		registerReceiver(mBluetooth, new IntentFilter(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED));
		registerReceiver(mBluetooth, new IntentFilter(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED));
		registerReceiver(mBluetooth, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
		registerReceiver(mBluetooth, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
		
		if (!mBluetoothAdapter.isEnabled()) {
			//bluetoothWasEnabled = false;
			//if (!mBluetoothAdapter.enable()) {
			//	Log.e(TAG, "Could not enable bluetooth");
			//}				
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, 0); 
		}	
		else {
			Log.i(TAG, "Bluetooth already enabled, connecting...");
			mBluetooth.connect();
		}
	}
	
	
	@Override
	public void onResume() {
		Log.i(TAG, "Entered StoryBuddiesBaseActivity: onResume");
		super.onResume();
		//nfcResume();
		StoryBuddiesUtils.hideSystemUI(this);
		
		mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		mAudioManager.setSpeakerphoneOn(true);
		
		Intent intent = getIntent();
	    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
	    	handleNFCIntent(intent);   	
	    }
	    
	    if (stories.isEmpty()) { //Shouldn't happen because will always have a built-in story
	    	loadStories();
	    }
	}

	@Override
	public void onPause() {
		Log.i(TAG, "Entered StoryBuddiesBaseActivity: onPause");		
		super.onPause();
		//nfcPause();
		// Close proxy connection after use.
		//mBluetoothAdapter.closeProfileProxy(0, mBluetoothSpeaker);
	}
	
	@Override
	public void onDestroy() {
		Log.i(TAG, "Entered StoryBuddiesBaseActivity: onDestroy");
		super.onDestroy();
		if (mBluetoothAdapter != null) {
			if (mBluetooth != null) {
				Log.i(TAG, "Disconnecting Bluetooth");
				mBluetooth.disconnect();
				unregisterReceiver(mBluetooth); //TODO Better way to detect
			}
			/*if (!bluetoothWasEnabled && mBluetoothAdapter.isEnabled()) {
				Log.i(TAG, "Re-disabling bluetooth");
				mBluetoothAdapter.disable();
			}*/
		}
	    
	    deleteStories();
	}
	
	private void loadStories(){
		Log.i(TAG, "Entered StoryBuddiesBaseActivity: loadStories");
		List<StoryBook> internal = getBuiltInStories();
		List<StoryBook> saved = getInternalStories();
				
		for (StoryBook story : internal) {
			addStory(story);
		}
		for (StoryBook story : saved) {
			addStory(story);
		}
	}

	private void addStory(StoryBook story) {
		boolean exists = false;
		for (StoryBook story2 : stories) {
			if (story.getmTitle().equals(story2.getmTitle())) {
				exists = true;
				break;
			}
		}
		if (!exists) {
			stories.add(story);
		}
	}
	
	private List<StoryBook> getBuiltInStories(){
		//Build test Story
		Log.i(TAG, "Entered StoryBuddiesBaseActivity: loadBuiltInStories");
		StoryBook testStory = new StoryBook("FirstTestStory");
		
		StoryPage page1 = new StoryPage(-1, "the first pages text");
		StoryPage page2 = new StoryPage(-1, "the second pages text");
		StoryPage page3 = new StoryPage(-1, "The End");
		
		testStory.addPage(page1);
		testStory.addPage(page2);
		testStory.addPage(page3);
		
		//Build Turtle and the Hare
		StoryBook book1 = new StoryBook(getString(R.string.tortoise_title));
		
		book1.setmTitlePage(R.drawable.th1);
		String[] text = getResources().getStringArray(R.array.tortoise_and_hare);
		book1.addPage(new StoryPage(R.drawable.th1, text[0])); //BitmapFactory.decodeResource(getResources(), R.drawable.th1), text[0]));
		book1.addPage(new StoryPage(R.drawable.th2, text[1]));
		book1.addPage(new StoryPage((R.drawable.th3), text[2]));
		book1.addPage(new StoryPage((R.drawable.th4), text[3]));
		book1.addPage(new StoryPage((R.drawable.th5), text[4]));
		book1.addPage(new StoryPage((R.drawable.th6), text[5]));
		book1.addPage(new StoryPage((R.drawable.th7), text[6]));
		book1.addPage(new StoryPage((R.drawable.th8), text[7]));
		book1.addPage(new StoryPage((R.drawable.th9), text[8]));
		book1.addPage(new StoryPage((R.drawable.th10), text[9]));
		book1.addPage(new StoryPage((R.drawable.th11), text[10]));
		book1.addPage(new StoryPage((R.drawable.th12), text[11]));
		book1.addPage(new StoryPage((R.drawable.th13), text[12]));
		book1.addPage(new StoryPage((R.drawable.th14), text[13]));
//		book1.addPage(new StoryPage(BitmapFactory.decodeResource(getResources(), R.drawable.th2), text[1]));
//		book1.addPage(new StoryPage(BitmapFactory.decodeResource(getResources(), R.drawable.th3), text[2]));
//		book1.addPage(new StoryPage(BitmapFactory.decodeResource(getResources(), R.drawable.th4), text[3]));
//		book1.addPage(new StoryPage(BitmapFactory.decodeResource(getResources(), R.drawable.th5), text[4]));
//		book1.addPage(new StoryPage(BitmapFactory.decodeResource(getResources(), R.drawable.th6), text[5]));
//		book1.addPage(new StoryPage(BitmapFactory.decodeResource(getResources(), R.drawable.th7), text[6]));
//		book1.addPage(new StoryPage(BitmapFactory.decodeResource(getResources(), R.drawable.th8), text[7]));
//		book1.addPage(new StoryPage(BitmapFactory.decodeResource(getResources(), R.drawable.th9), text[8]));
//		book1.addPage(new StoryPage(BitmapFactory.decodeResource(getResources(), R.drawable.th10), text[9]));
//		book1.addPage(new StoryPage(BitmapFactory.decodeResource(getResources(), R.drawable.th11), text[10]));
//		book1.addPage(new StoryPage(BitmapFactory.decodeResource(getResources(), R.drawable.th12), text[11]));
//		book1.addPage(new StoryPage(BitmapFactory.decodeResource(getResources(), R.drawable.th13), text[12]));
//		book1.addPage(new StoryPage(BitmapFactory.decodeResource(getResources(), R.drawable.th14), text[13]));
		
		
		//Add stories to ArrayList
		ArrayList<StoryBook> result = new ArrayList<StoryBook>();
		result.add(testStory);
		result.add(book1);
		
		return result;
	}
	
	private List<StoryBook> getInternalStories(){
		Log.i(TAG, "Entered StoryBuddiesBaseActivity: loadInternalStories");
		ArrayList<StoryBook> result = new ArrayList<StoryBook>();
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) { 
			File root_dir = new File(Environment.getExternalStorageDirectory() + "/" + getString(R.string.story_dir));
			if (root_dir.exists() && root_dir.listFiles() == null) {
				for (File f : root_dir.listFiles()) {
					if (f.isDirectory()) {
						Log.i(TAG,"Loaded "+ f.toString());
						try {
							StoryBook newStory = StoryBuddiesUtils.readStoryFromDir(this, f.getAbsolutePath());
							result.add(newStory);
						} catch (IOException e) {
							Log.e(TAG, "Error reading story " + f.getName() + ": " + e);
							Toast.makeText(getApplicationContext(), "Error loading story from file: " + f.getName(), Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
		}	
		return result;
	}
	
	private Bitmap getScaledBitmap(int pointer){
		Bitmap tempBitmap = BitmapFactory.decodeResource(getResources(),pointer);// R.drawable.tortoise_title);
		tempBitmap = Bitmap.createScaledBitmap(tempBitmap, 200,225,true);//1200, 1350, true);
		return tempBitmap;
	}
	
	private Bitmap decodeFromFile(File f){
		Bitmap bitmap = null;
		try{
			//Decode image size
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			
			FileInputStream fileIS = new FileInputStream(f);
			BitmapFactory.decodeStream(fileIS, null, options);
			fileIS.close();
			
			int scale = 1;
			if(options.outHeight > IMAGE_MAX_SIZE || options.outWidth > IMAGE_MAX_SIZE){
				scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
			}
			
			//Decode with inSampleSize
			BitmapFactory.Options options2 = new BitmapFactory.Options();
			options2.inSampleSize = scale;
			fileIS = new FileInputStream(f);
			bitmap = BitmapFactory.decodeStream(fileIS, null, options2);
			fileIS.close();
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitmap;
	}
	
	private void deleteStories(){
		stories.clear();
	}
	

	
	/********************************************************************
	 * The following code for reading NFC tags based on code from 
	 * https://code.google.com/p/ndef-tools-for-android/wiki/AndroidTutorial
	 *************************************************************************/
	/*protected NfcAdapter nfcAdapter;
	protected PendingIntent nfcPendingIntent;

	
	public void nfcOnCreate(Bundle savedInstanceState) {
		// initialize NFC
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
	}

	public void enableForegroundMode() {
		Log.d(TAG, "enableForegroundMode");

		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for all
		IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
		nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
	}

	public void disableForegroundMode() {
		Log.d(TAG, "disableForegroundMode");

		nfcAdapter.disableForegroundDispatch(this);
	}

	@Override
	public void onNewIntent(Intent intent) {
		Log.d(TAG, "onNewIntent");

		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {			
			handleNFCIntent(intent);
		} else {
			// ignore
		}
	}*/

	private void handleNFCIntent(Intent intent) {
		Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		if (messages != null) {
	
			Log.d(TAG, "Found " + messages.length + " NDEF messages"); // is almost always just one
	
			// parse to records
			for (int i = 0; i < messages.length; i++) {
				try {
					List<Record> records = new Message((NdefMessage)messages[i]);
					
					Log.d(TAG, "Found " + records.size() + " records in message " + i);
					
					for(int k = 0; k < records.size(); k++) {
						Log.d(TAG, " Record #" + k + " is of class " + records.get(k).getClass().getSimpleName());
						
						Record record = records.get(k);
						
		                if(record instanceof AndroidApplicationRecord) {
							AndroidApplicationRecord aar = (AndroidApplicationRecord)record;
							Log.d(TAG, "Package is " + aar.getDomain() + " " + aar.getType());
						}
		                else if (record instanceof TextRecord) {
		                	TextRecord tr = (TextRecord) record;
		                	String key = new String(tr.getId());
							String value = tr.getText();
							Log.d(TAG, "Text Record " + key + ": \"" + value + "\"");
							if (key.equals("animal")) {
								myAnimal = value;
							}
							else if (key.equals("speaker") && myMacAddr != value) {
								myMacAddr = value.trim();
								mBluetooth.setBluetoothDev(myMacAddr);
							}
		                }
	
					}
				} catch (Exception e) {
					Log.e(TAG, "Problem parsing message", e);
				}
	
			}
		}
	}

	/*protected void nfcResume() {
		enableForegroundMode();
	}

	protected void nfcPause() {
		disableForegroundMode();
	}*/
	
	

}
