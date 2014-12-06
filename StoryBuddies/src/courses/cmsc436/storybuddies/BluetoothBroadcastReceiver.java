package courses.cmsc436.storybuddies;

import java.lang.reflect.Method;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.speech.tts.TextToSpeech;
import android.util.Log;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {
	private boolean bluetoothConnected = false;
	private boolean bluetoothPlaying = false;
	private BluetoothA2dp mBluetoothSpeaker;
	private BluetoothDevice mBluetoothDev;
	private BluetoothAdapter mBluetoothAdapter;
	private String mMacAddr = null;
	private final String TAG = "SB_BluetoothBroadcastReceiver";
	private BluetoothProfile.ServiceListener mProfileListener;
	private Context mContext;
	private static BluetoothBroadcastReceiver instance;
	private boolean isInitialized = false;
	
	private void BluetoothBroadcastReceiver() {
		
	}
	
	public void initialize(BluetoothAdapter adapter, Context context, String macAddr) {
		mBluetoothAdapter = adapter;
		mMacAddr = macAddr;
		mBluetoothDev = mBluetoothAdapter.getRemoteDevice(mMacAddr);
		mContext = context;
			 
		mProfileListener = new BluetoothProfile.ServiceListener() {
		    public void onServiceConnected(int profile, BluetoothProfile proxy) {
		        if (profile == BluetoothProfile.A2DP) {
		        	mBluetoothSpeaker = (BluetoothA2dp) proxy; 
		        	if (mBluetoothSpeaker.getConnectionState(mBluetoothDev) == BluetoothA2dp.STATE_DISCONNECTED) {
		        		connect();
		        	}
		        }
		    }
		    public void onServiceDisconnected(int profile) {
		        if (profile == BluetoothProfile.A2DP) {
		        	mBluetoothSpeaker = null;
		        }
		    }
		};
		
		// Establish connection to the proxy.
		mBluetoothAdapter.getProfileProxy(context, mProfileListener, BluetoothProfile.A2DP);
		isInitialized = true;
	}
	
	public boolean isInitialized() {
		return isInitialized;
	}
	
	public static BluetoothBroadcastReceiver getInstance() {
		if (instance == null) {
			return new BluetoothBroadcastReceiver();
		}
		else {
			return instance;
		}
	}
	
    @Override
    public void onReceive(Context ctx, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "receive intent for action : " + action);
        if (action.equals(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)) {
            int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_DISCONNECTED);
            if (state == BluetoothA2dp.STATE_CONNECTED) {
            	Log.d(TAG, "Bluetooth connected");
                setBluetoothConnected(true);
                connect();
            } else if (state == BluetoothA2dp.STATE_DISCONNECTED) {
            	Log.d(TAG, "Bluetooth disconnected");
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
        	Log.d(TAG, "Speaker bond state changed");
        	connect();
    
        } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
        	int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
        	if (state == BluetoothAdapter.STATE_ON) {
	        	Log.d(TAG, "Bluetooth Enabled");
	        	connect();
        	}
        }
    }

	private  void connectToSpeaker() {
		Log.i(TAG, "Connecting to speaker");
		Method connect;
		try {
			connect = BluetoothA2dp.class.getDeclaredMethod("connect", BluetoothDevice.class);
			connect.invoke(mBluetoothSpeaker, mBluetoothDev);
		} catch (Exception e) {
			Log.e(TAG, "Error: " + e);
		}
	}
	
	public void connect() {
		if (mBluetoothDev == null || mBluetoothSpeaker == null || !mBluetoothAdapter.isEnabled()) {
			//Do nothing
		}
		else if (mBluetoothDev.getBondState() == BluetoothDevice.BOND_NONE) {
			if (!mBluetoothDev.createBond()) {
				Log.e(TAG, "Could not start bonding!");
			}
		}
		else if (mBluetoothDev.getBondState() == BluetoothDevice.BOND_BONDED && 
				mBluetoothSpeaker.getConnectionState(mBluetoothDev) == BluetoothA2dp.STATE_DISCONNECTED) {
			connectToSpeaker();
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

	public void disconnect() {
    	if (mBluetoothSpeaker != null && mBluetoothSpeaker.getConnectionState(mBluetoothDev) == BluetoothA2dp.STATE_CONNECTED) {
        	Method disconnect;
			try {
				Log.i(TAG, "Disconnecting bluetooth");
		        Method method = mBluetoothDev.getClass().getMethod("removeBond", (Class[]) null);
	            method.invoke(mBluetoothDev, (Object[]) null);
				//disconnect = BluetoothA2dp.class.getDeclaredMethod("disconnect", BluetoothDevice.class);
				//disconnect.invoke(mBluetoothSpeaker, mBluetoothDev);
			} catch (Exception e) {
				Log.e(TAG, "Error: " + e);
			}
			mBluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP, mBluetoothSpeaker);
    	}
    	else {
    		Log.i(TAG, "Bluetooth already disconnected");
    	}
    	//mContext.unbindService((ServiceConnection) mProfileListener); TODO: Service listener leak
	}			
}
