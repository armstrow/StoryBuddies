package courses.cmsc436.storybuddies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class PaintView extends ImageView {
	private Paint brush = new Paint();
	private Path path = new Path();
	private Canvas mCanvas;
	private Bitmap canvasBitmap;
	private final String TAG = "PaintView";

	
	public PaintView(Context context) {
		super(context);
		init();		
	}
	
	public PaintView(Context context, AttributeSet attrs){  
		super(context,attrs);
		init();
	}
	
	public void init(){
		brush.setAntiAlias(true);
		brush.setColor(Color.BLACK);
		brush.setStyle(Paint.Style.STROKE);
		brush.setStrokeJoin(Paint.Join.ROUND);
		brush.setStrokeWidth(10f);
	}
	
	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Log.i(TAG, "Entered PaintView: onSizeChanged");
        super.onSizeChanged(w, h, oldw, oldh);
        this.canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        this.mCanvas = new Canvas(this.canvasBitmap);
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		Log.i(TAG, "Entered PaintView: onTouchEvent");
		float pointX = event.getX();
		float pointY = event.getY();
		this.requestFocus();
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			path.moveTo(pointX, pointY);
			return true;
		case MotionEvent.ACTION_MOVE:
			path.lineTo(pointX, pointY);
			break;
		case MotionEvent.ACTION_UP:
			this.mCanvas.drawPath(this.path,  this.brush);
			this.path.reset();
			break;
		default:
			return false;
		}
		
		postInvalidate();
		
		return true;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		Log.i(TAG, "Entered PaintView: onDraw");
		if(canvasBitmap != null){
			canvas.drawBitmap(this.canvasBitmap, 0, 0, this.brush);
		}
		canvas.drawPath(this.path, this.brush);
	}
	
	public Bitmap getBitmap(){
		Log.i(TAG, "Entered PaintView: getBitmap");
		if(mCanvas == null){
			return null;
		}
		this.setDrawingCacheEnabled(true);
		this.buildDrawingCache();
		Bitmap bmp = Bitmap.createBitmap(this.getDrawingCache());
		this.setDrawingCacheEnabled(false);
		
		Log.i(TAG,"returning "+bmp.toString()+" in get Bitmap");
		
		return bmp;
	}
	
	public void setBitmap(Bitmap bitmap){
		Log.i(TAG, "Entered PaintView: setBitmap");
		if(bitmap == null){
			return;
		}
		clear();
		this.mCanvas.drawBitmap(bitmap,0,0, this.brush);
		postInvalidate();
	}
	
	public void clear(){
		Log.i(TAG, "Entered PaintView: clear");
		if(mCanvas != null){
			this.mCanvas.drawColor(0, Mode.CLEAR);
			postInvalidate();
		}
	}
	
	public Path getPaths(){
		return path;
	}
	
	public void setPaths(Path newPath){
		path = newPath;
		postInvalidate();
	}
	
	public void setColor(int color){
		Log.i(TAG, "Entered PaintView: setColor");
		brush.setColor(color);
	}
}