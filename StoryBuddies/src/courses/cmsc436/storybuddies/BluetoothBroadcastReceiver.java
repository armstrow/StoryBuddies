package courses.cmsc436.storybuddies;

import java.lang.reflect.Method;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {
	private boolean bluetoothConnected = false;
	private boolean bluetoothPlaying = false;
	private BluetoothA2dp mBluetoothSpeaker;
	private BluetoothDevice mBluetoothDev;
	private BluetoothAdapter mBluetoothAdapter;
	private String mMacAddr = null;
	private final String TAG = "SB_BluetoothBroadcastReceiver";
	
	public BluetoothBroadcastReceiver(BluetoothAdapter adapter, Context context, String macAddr) {
		mBluetoothAdapter = adapter;
		mMacAddr = macAddr;			
			 
		BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
		    public void onServiceConnected(int profile, BluetoothProfile proxy) {
		        if (profile == BluetoothProfile.A2DP) {
		        	mBluetoothSpeaker = (BluetoothA2dp) proxy;  	   	
		        }
		    }
		    public void onServiceDisconnected(int profile) {
		        if (profile == BluetoothProfile.HEADSET) {
		        	mBluetoothSpeaker = null;
		        }
		    }
		};
		
		// Establish connection to the proxy.
		mBluetoothAdapter.getProfileProxy(context, mProfileListener, BluetoothProfile.A2DP);
		
	}
	
    @Override
    public void onReceive(Context ctx, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "receive intent for action : " + action);
        if (action.equals(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)) {
            int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_DISCONNECTED);
            if (state == BluetoothA2dp.STATE_CONNECTED) {
            	Log.d(TAG, "state connected");
                setBluetoothConnected(true);
            } else if (state == BluetoothA2dp.STATE_DISCONNECTED) {
                setBluetoothConnected(false);
            }
        } else if (action.equals(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED)) {
            int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_NOT_PLAYING);
            if (state == BluetoothA2dp.STATE_PLAYING) {
            	setBluetoothPlaying(true);
                Log.d(TAG, "A2DP start playing");
            } else {
            	setBluetoothPlaying(false);
                Log.d(TAG, "A2DP stop playing");
            }
        } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
        	Log.d(TAG, "Speaker Bonded, connecting...");
        	if (mBluetoothDev.getBondState() != BluetoothDevice.BOND_BONDED) {
        		return;
        	}
        	Method connect;
			try {
				connect = BluetoothA2dp.class.getDeclaredMethod("connect", BluetoothDevice.class);
				connect.invoke(mBluetoothSpeaker, mBluetoothDev);
			} catch (Exception e) {
				Log.e(TAG, "Error: " + e);
			}
        } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
        	Log.d(TAG, "Bluetooth Enabled");
        	pairDevice();
        }
    }

    public void pairDevice() {
		mBluetoothDev = mBluetoothAdapter.getRemoteDevice(mMacAddr);
		if (mBluetoothDev.getBondState() == BluetoothDevice.BOND_NONE) {
			Log.i(TAG, "Bonding bluetooth device...");
			mBluetoothDev.setPin(new byte[]{(byte)0,(byte)0,(byte)0,(byte)0});
			if (!mBluetoothDev.createBond()) {
				Log.e(TAG, "Could not start bonding!");
			}
		}
		else {
			Log.i(TAG, "Device already Paired");// + mBluetoothDev.getAddress());
		}
    }
    
	public boolean isBluetoothConnected() {
		return bluetoothConnected;
	}

	public void setBluetoothConnected(boolean bluetoothConnected) {
		this.bluetoothConnected = bluetoothConnected;
	}

	public boolean isBluetoothPlaying() {
		return bluetoothPlaying;
	}

	public void setBluetoothPlaying(boolean bluetoothPlaying) {
		this.bluetoothPlaying = bluetoothPlaying;
	}			
}
