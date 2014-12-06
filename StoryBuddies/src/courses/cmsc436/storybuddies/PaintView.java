package courses.cmsc436.storybuddies;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;

public class PaintView extends RelativeLayout {
	private Paint brush = new Paint();
	private Path path = new Path();
	public Button btnEraseAll;
	public LayoutParams params;
	
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
		canvas.drawPath(path, brush);
	}
}