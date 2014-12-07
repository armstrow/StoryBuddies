package courses.cmsc436.storybuddies;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;

public class StoryBuddiesUtils {
	
	private static final String TAG = "SB_Utils";
	
	// This snippet hides the system bars.
	public static void hideSystemUI(final Activity currentActivity) {
		
	    currentActivity.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {
		      @Override
		      public void onSystemUiVisibilityChange(int visibility) {
		    	  goFullScreen(currentActivity); // Needed to avoid exiting immersive_sticky when keyboard is displayed
		      }
		    });
	    goFullScreen(currentActivity);
	}

	private static void goFullScreen(Activity currentActivity) {
		// Set the IMMERSIVE flag.
	    // Set the content to appear under the system bars so that the content
	    // doesn't resize when the system bars hide and show.
		currentActivity.getWindow().getDecorView().setSystemUiVisibility(
	            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
	            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
	            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}	
	
	public static void playMusic(Activity currentActivity, String filename) {
	    MediaPlayer mPlayer = new MediaPlayer();
	    AssetManager assetManager = currentActivity.getAssets();
	    AssetFileDescriptor fd;
	    try {
	    	String s = ""; 
	    	String[] assets = assetManager.list("sounds");
	    	for (int i = 0; i < assets.length; i++)
	    		s += assets[i] + ", ";
	    	Log.i(TAG, s);
	        fd = assetManager.openFd(filename);
	        Log.i(TAG, "fd = " + fd);
	        mPlayer.setDataSource(fd.getFileDescriptor());
	        mPlayer.prepare();
	        Log.d(TAG, "start play music");
	        mPlayer.start();
	    } catch (IOException e) {
	        Log.e(TAG, "Error: " + e);
	    }
	}
	
	public static StoryBook readStoryFromDir(Activity currentActivity, String dir) throws IOException {
		File textFile = new File(dir, currentActivity.getString(R.string.text_file_name) + ".txt");
		FileInputStream is = new FileInputStream(textFile);
		InputStreamReader reader = new InputStreamReader(is);
		JsonReader r = new JsonReader(reader);
		StoryBook story = new StoryBook();
		r.beginObject();
		r.nextName();
		story.setmTitle(r.nextString());
		r.nextName();
		story.setmAuthor(r.nextString());
		r.nextName();
		r.beginArray();
		while (r.hasNext()) {
			StoryPage page = new StoryPage();
			r.beginObject();
			r.nextName();
			page.setmStoryText(r.nextString());
			if (r.hasNext()) {
				String type = r.nextName();
				if (type.equals("PICTURE")) {
					String fileName = r.nextString();
					Bitmap pic = BitmapFactory.decodeFile(dir + "/" + fileName);
					page.setmPicture(pic);
				}
			}
			r.endObject();
			story.addPage(page);
		}
		r.endArray();
		r.endObject();
		r.close();		
		File cover = new File(dir, currentActivity.getString(R.string.cover_file_name) + ".png");
		if (cover.exists()) {
			Bitmap pic = BitmapFactory.decodeFile(dir + "/" + currentActivity.getString(R.string.cover_file_name) + ".png");
			story.setmTitlePage(pic);
		}
		return story;
	}	
 
	//Method below copied from http://www.java2s.com/Tutorial/Java/0180__File/Removeadirectoryandallofitscontents.htm
		/**
		  Remove a directory and all of its contents.

		  The results of executing File.delete() on a File object
		  that represents a directory seems to be platform
		  dependent. This method removes the directory
		  and all of its contents.

		  @return true if the complete directory was removed, false if it could not be.
		  If false is returned then some of the files in the directory may have been removed.

		*/
		public static boolean removeDirectory(File directory) {

		  // System.out.println("removeDirectory " + directory);

		  if (directory == null)
		    return false;
		  if (!directory.exists())
		    return true;
		  if (!directory.isDirectory())
		    return false;

		  String[] list = directory.list();

		  // Some JVMs return null for File.list() when the
		  // directory is empty.
		  if (list != null) {
		    for (int i = 0; i < list.length; i++) {
		      File entry = new File(directory, list[i]);

		      //        System.out.println("\tremoving entry " + entry);

		      if (entry.isDirectory())
		      {
		        if (!removeDirectory(entry))
		          return false;
		      }
		      else
		      {
		        if (!entry.delete())
		          return false;
		      }
		    }
		  }

		  return directory.delete();
		}
	
}
