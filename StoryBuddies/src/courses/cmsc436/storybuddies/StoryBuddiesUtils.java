package courses.cmsc436.storybuddies;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.widget.ImageView;

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
					page.setmPictureFromFile(dir + "/" + fileName);
				}
			}
			r.endObject();
			story.addPage(page);
		}
		r.endArray();
		r.endObject();
		r.close();		
		/*File cover = new File(dir, currentActivity.getString(R.string.cover_file_name) + ".png");
		if (cover.exists()) {
			Bitmap pic = BitmapFactory.decodeFile(dir + "/" + currentActivity.getString(R.string.cover_file_name) + ".png");
			story.setmTitlePage(pic);
		}*/ //TODO: Fix this
		return story;
	}	
	
	//Method below from http://developer.android.com/training/displaying-bitmaps/load-bitmap.html#decodeSampledBitmapFromResource
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;
	
	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }
	
	    return inSampleSize;
	}
	
	
	
	//Code from http://developer.android.com/training/displaying-bitmaps/process-bitmap.html
	public static class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
	    private final WeakReference<ImageView> imageViewReference;
	    private int data = 0;
	    private Resources resources;

	    public BitmapWorkerTask(ImageView imageView, Resources res) {
	        // Use a WeakReference to ensure the ImageView can be garbage collected
	        imageViewReference = new WeakReference<ImageView>(imageView);
	        resources = res;
	    }

	    // Decode image in background.
	    @Override
	    protected Bitmap doInBackground(Integer... params) {
	        data = params[0];
	        return decodeSampledBitmapFromResource(resources, data, 
	        		imageViewReference.get().getWidth(), imageViewReference.get().getHeight());
	    }

	    // Once complete, see if ImageView is still around and set bitmap.
	    @Override
	    protected void onPostExecute(Bitmap bitmap) {
	        if (imageViewReference != null && bitmap != null) {
	            final ImageView imageView = imageViewReference.get();
	            if (imageView != null) {
	                imageView.setImageBitmap(bitmap);
	            }
	        }
	    }
	}

	//Method below from http://developer.android.com/training/displaying-bitmaps/load-bitmap.html#decodeSampledBitmapFromResource
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(res, resId, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeResource(res, resId, options);
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
