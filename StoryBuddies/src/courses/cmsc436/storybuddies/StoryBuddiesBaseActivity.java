package courses.cmsc436.storybuddies;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.AudioManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class StoryBuddiesBaseActivity extends Activity {
	
	public static ArrayList<StoryBook> stories = new ArrayList<StoryBook>();
	private final String TAG = "Story_Buddies";
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothBroadcastReceiver mBluetooth;
	private final int IMAGE_MAX_SIZE = 600;
	
	private final String MAC_ADDR="30:22:00:2E:BF:E9";
	
	private boolean bluetoothWasEnabled = true;
	
	// AudioManager
	private AudioManager mAudioManager;
	private SpeechEngine speech;
	
	//Static variable used throughout the app
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.i(TAG, "Entered StoryBuddiesBaseActivity: onCreate");
		loadStories();
			
		speech = SpeechEngine.getInstance(getApplicationContext());
				
		if (savedInstanceState != null)
		{
			bluetoothWasEnabled = savedInstanceState.getBoolean("bluetoothWasEnabled");
		}
		
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
		mBluetooth.initialize(mBluetoothAdapter, getBaseContext(), MAC_ADDR);
		
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
			Log.i(TAG, "Bluetooth already enabled, connecting...");
			mBluetooth.connect();
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		Log.i(TAG, "Entered StoryBuddiesBaseActivity: onSaveInstanceState");
	    savedInstanceState.putBoolean("bluetoothWasEnabled", bluetoothWasEnabled);
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	
	@Override
	public void onResume() {
		Log.i(TAG, "Entered StoryBuddiesBaseActivity: onResume");
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
		Log.i(TAG, "Entered StoryBuddiesBaseActivity: onPause");		
		super.onPause();
		// Close proxy connection after use.
		//mBluetoothAdapter.closeProfileProxy(0, mBluetoothSpeaker);
	}
	
	@Override
	public void onDestroy() {
		Log.i(TAG, "Entered StoryBuddiesBaseActivity: onDestroy");
		super.onDestroy();
		mBluetooth.disconnect();
		if (!bluetoothWasEnabled && mBluetoothAdapter.isEnabled()) {
			Log.i(TAG, "Re-disabling bluetooth");
			mBluetoothAdapter.disable();
		}
	    unregisterReceiver(mBluetooth);
	    
	    deleteStories();
	}
	
	private void loadStories(){
		Log.i(TAG, "Entered StoryBuddiesBaseActivity: loadStories");
		loadBuiltInStories();
		loadInternalStories();
	}
	
	private void loadBuiltInStories(){
		//Build test Story
		Log.i(TAG, "Entered StoryBuddiesBaseActivity: loadBuiltInStories");
		StoryBook testStory = new StoryBook("FirstTestStory");
		
		StoryPage page1 = new StoryPage(null, "the first pages text");
		StoryPage page2 = new StoryPage(null, "the second pages text");
		StoryPage page3 = new StoryPage(null, "The End");
		
		testStory.addPage(page1);
		testStory.addPage(page2);
		testStory.addPage(page3);
		
		//Build Turtle and the Hare
		StoryBook book1 = new StoryBook(getString(R.string.tortoise_title));
		
		book1.setmTitlePage(getScaledBitmap(R.drawable.th1));
		String[] text = getResources().getStringArray(R.array.tortoise_and_hare);
		book1.addPage(new StoryPage(getScaledBitmap(R.drawable.th1), text[0])); //BitmapFactory.decodeResource(getResources(), R.drawable.th1), text[0]));
		book1.addPage(new StoryPage(getScaledBitmap(R.drawable.th2), text[1]));
		book1.addPage(new StoryPage(getScaledBitmap(R.drawable.th3), text[2]));
		book1.addPage(new StoryPage(getScaledBitmap(R.drawable.th4), text[3]));
		book1.addPage(new StoryPage(getScaledBitmap(R.drawable.th5), text[4]));
		book1.addPage(new StoryPage(getScaledBitmap(R.drawable.th6), text[5]));
		book1.addPage(new StoryPage(getScaledBitmap(R.drawable.th7), text[6]));
		book1.addPage(new StoryPage(getScaledBitmap(R.drawable.th8), text[7]));
		book1.addPage(new StoryPage(getScaledBitmap(R.drawable.th9), text[8]));
		book1.addPage(new StoryPage(getScaledBitmap(R.drawable.th10), text[9]));
		book1.addPage(new StoryPage(getScaledBitmap(R.drawable.th11), text[10]));
		book1.addPage(new StoryPage(getScaledBitmap(R.drawable.th12), text[11]));
		book1.addPage(new StoryPage(getScaledBitmap(R.drawable.th13), text[12]));
		book1.addPage(new StoryPage(getScaledBitmap(R.drawable.th14), text[13]));
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
		stories.add(testStory);
		stories.add(book1);
		
		
		//TODO - Create all the built in stories for the particular animal we are connected to
		//	and add them to the list of StoryBooks
	}
	
	private void loadInternalStories(){
		Log.i(TAG, "Entered StoryBuddiesBaseActivity: loadInternalStories");
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) { 
			File root_dir = new File(Environment.getExternalStorageDirectory() + "/" + getString(R.string.story_dir));
			if (!root_dir.exists() || root_dir.listFiles() == null) {
				return;
			}
			for (File f : root_dir.listFiles()) {
				if (f.isDirectory()) {
					Log.i(TAG,"Loaded "+ f.toString());
					try {
						StoryBook newStory = StoryBuddiesUtils.readStoryFromDir(this, f.getAbsolutePath());
						stories.add(newStory);
					} catch (IOException e) {
						Log.e(TAG, "Error reading story " + f.getName() + ": " + e);
						Toast.makeText(getApplicationContext(), "Error loading story from file: " + f.getName(), Toast.LENGTH_SHORT).show();
					}
				}
			}
		}			
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
