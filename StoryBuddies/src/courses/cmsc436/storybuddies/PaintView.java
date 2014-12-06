package courses.cmsc436.storybuddies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class PaintView extends ImageView {
	private Paint brush = new Paint();
	private Path path = new Path();
	public Button btnEraseAll;
	public LayoutParams params;
	private Canvas mCanvas;
	private final Paint mPainter = new Paint();
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
		brush.setColor(Color.BLUE);
		brush.setStyle(Paint.Style.STROKE);
		brush.setStrokeJoin(Paint.Join.ROUND);
		brush.setStrokeWidth(10f);
		
		btnEraseAll=new Button(getContext());
		
		btnEraseAll.setText("Erease All");
		
		params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		btnEraseAll.setLayoutParams(params);
		
		btnEraseAll.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				path.reset();
				postInvalidate();
			}
		});
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		float pointX = event.getX();
		float pointY = event.getY();
		
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			path.moveTo(pointX, pointY);
			return true;
		case MotionEvent.ACTION_MOVE:
			path.lineTo(pointX, pointY);
			break;
		default:
			return false;
		}
		
		postInvalidate();
		
		return false;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		mCanvas = canvas;
		canvas.drawPath(path, brush);
	}
	
	public Bitmap getBitmap(){
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
	
	//Don't use this method
	public void setBitmap(Bitmap bitmap){
		if(bitmap == null){
			return;
		}
		//mCanvas.drawBitmap(bitmap,10,10,mPainter);
	}
	
	public void clear(){
		path.reset();
		postInvalidate();
		/*Bitmap image = Bitmap.createBitmap(this.getWidth(),this.getHeight(),Bitmap.Config.ARGB_8888);
		Canvas newCanvas = new Canvas(image);
		mCanvas = newCanvas;*/
	}
	
	public Path getPaths(){
		return path;
	}
	
	public void setPaths(Path newPath){
		path = newPath;
		postInvalidate();
	}
}