package edu.ucsb.cs.cs185.carammelkamp.touchgestures;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

public class TouchView extends ImageView {
	
	boolean debug = true;
	private float x=0;
    private float y=0;
    
    private float scaleFactor = 1.0f;
    private ScaleGestureDetector mScaleDetector;

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    static final int ROTATE = 3;
    
    int mode = NONE;
    int modeR = NONE;
    private PointF mid = null;
    private PointF start = null;
    private Matrix matrix = null;
    private Matrix savedMatrix = null;
    float oldDistance = 1f;
    float oldSlope = 1f;
    
	public TouchView(Context context) {
		 super(context);
		 mScaleDetector = new ScaleGestureDetector(context,
			        new ScaleListener());
		 
		//for scaling later
		mid = new PointF();
		start = new PointF();
		matrix = new Matrix();
		savedMatrix = new Matrix();
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas){
	super.onDraw(canvas);

	canvas.save();
    
    if(mode != DRAG || modeR != ROTATE)
    {
	Paint mPaint = new Paint();
    mPaint.setColor(0xffff0000);
    canvas.drawCircle(x, y, 5, mPaint);
    }
    
    this.setImageMatrix(matrix);
    canvas.restore();
    
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
	    int action = event.getAction();
	    mScaleDetector.onTouchEvent(event);

		switch(action & MotionEvent.ACTION_MASK){ //action mask??? not sure
			case MotionEvent.ACTION_DOWN: 
				x = event.getX();
				y = event.getY();
				savedMatrix.set(matrix);
		        start.set(event.getX(), event.getY());
		        mode = DRAG;
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				if(debug) System.out.println("in ActionPointerDown");
				oldDistance = spacing(event);
				
				oldSlope = slope(event);
				
				if(debug) System.out.println("in ActionPointerDown2");
				if (oldDistance > 10f) {
					if(debug) System.out.println("in if ActionPointerDown");
				savedMatrix.set(matrix);
				midPoint(mid, event);
				if(debug) System.out.println("set the matrix");
				if(mid == null)
					if(debug) System.out.println("midpoint is null");
				else
					if(debug) System.out.println("midpoint: "+mid.x + mid.y);
				//mode=ZOOM;
				}
				break;
			 case MotionEvent.ACTION_UP:
		     case MotionEvent.ACTION_POINTER_UP:
		         mode = NONE;
		         break;
		     case MotionEvent.ACTION_MOVE:
		    	 float newSlope = slope(event);
		    	 double angle = 0;

		    	 if(oldSlope > newSlope || oldSlope < newSlope)
		    	 {
		    		 modeR = ROTATE;
		    		 angle = Math.toDegrees(Math.atan((newSlope - oldSlope)/(1+(oldSlope*newSlope))));
		    		 if(debug) System.out.println("oldslope: " + oldSlope +" newslope:"+newSlope +" angle: "+angle);
		    	 }
				 if (mode == DRAG) {
					 	if(debug) System.out.println("IN DRAG :)");
			            matrix.set(savedMatrix);
			            matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
			            x = event.getX();
						y = event.getY();
			         }
				 else 
					if (mode == ZOOM) {
						float newDistance = spacing(event);
						if(debug) System.out.println("**********newDistance: "+newDistance+" oldDest: "+oldDistance);
						if (newDistance > 10f) {
						matrix.set(savedMatrix);
						float scale = newDistance / oldDistance;
						if(debug) System.out.println("scale is"+scale);
						matrix.postScale(scale, scale, mid.x, mid.y);
					}
					if(modeR == ROTATE)
					{
						matrix.postRotate((float)angle, mid.x, mid.y);
					}
					}
				break;
			default:
				this.setImageMatrix(matrix);
				return true;
		}
		invalidate();
		return true;
	}
	

	@SuppressLint("NewApi")
	private class ScaleListener extends
    ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		  public boolean onScale(ScaleGestureDetector detector) {
			mode=ZOOM;
		    return true;
		  }
	}
	
	
	@SuppressLint({ "NewApi", "FloatMath" })
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1); //event.getX(0)
		float y = event.getY(0) - event.getY(1); //event.getY(0) 
		return FloatMath.sqrt(x * x + y * y);
		}
	
	private float slope(MotionEvent event)
	{
		return ((event.getY(1) - event.getY(0))/(event.getX(1) - event.getX(0)));
	}
	
	@SuppressLint("NewApi")
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
		}
}


