package com.example.becca13.cmsc434doodler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by becca13 on 3/5/16.
 */
public class DoodleView extends View {

    //private Paint _paintDoodle = new Paint();
    //private Path _path = new Path();
    private Paint _paintDoodle, canvasPaint;
    private Path _path;
    private View v;
    private Canvas drawCanvas;
    private Bitmap _bitmap;
    private int paintColor = 0xFFF50E89;
    private float brushSize, lastBrushSize;
    private int paintAlpha = 255;
    private ArrayList<PathObject> paths = new ArrayList<PathObject>();
    private ArrayList<PathObject> undoPaths = new ArrayList<PathObject>();
    private boolean drawPoint = true;

    private class PathObject{
        Paint paint;
        Path path;
        PathObject(Paint currPaint, Path currPath){
            this.paint=currPaint;
            this.path=currPath;
        }
    }
    public DoodleView(Context context) {
        super(context);
        setupDrawing();
        //init(null, 0);
    }

    public DoodleView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
        //init(attrs, 0);
        undoPaths = new ArrayList<PathObject>();
    }

    public DoodleView(Context context,AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        setupDrawing();
        //init(attrs, defStyle);
        undoPaths = new ArrayList<PathObject>();
    }

    private void setupDrawing(){
        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;

        _paintDoodle = new Paint();
        _path = new Path();

        _paintDoodle.setColor(paintColor);
        _paintDoodle.setStrokeWidth(brushSize);
        _paintDoodle.setAntiAlias(true);
        _paintDoodle.setStyle(Paint.Style.STROKE);
        _paintDoodle.setStrokeCap(Paint.Cap.ROUND);
        _paintDoodle.setStrokeJoin(Paint.Join.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        _bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(_bitmap);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(_bitmap, 0, 0, canvasPaint);
        //this.setBackgroundColor(Color.LTGRAY);
        for (PathObject p : paths) {
            canvas.drawPath(p.path,p.paint);
        }
        canvas.drawPath(_path, _paintDoodle);
    }

    public void setBrushSize(float newSize){
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize=pixelAmount;
        _paintDoodle.setStrokeWidth(brushSize);
    }

    public void setLastBrushSize(float lastSize){
        lastBrushSize=lastSize;
    }

    public float getLastBrushSize(){
        return lastBrushSize;
    }

    public void clearDrawing(){
        _path.rewind();
        canvasPaint.reset();
        drawCanvas.drawColor(Color.WHITE);
        paths.clear();
        undoPaths.clear();
        invalidate();
    }

    public int getPaintAlpha(){
        return Math.round((float)paintAlpha/255*100);
    }

    public void setPaintAlpha(int newAlpha){
        paintAlpha=Math.round((float)newAlpha/100 * 255);
        _paintDoodle.setColor(paintColor);
        _paintDoodle.setAlpha(paintAlpha);
    }


   public void undoDrawing(){
        if (paths.size()>0) {
            undoPaths.add(paths.remove(paths.size() - 1));
            invalidate();
        }
    }

    public void redoDrawing(){
        if (undoPaths.size()>0){
            paths.add(undoPaths.remove(undoPaths.size()-1));
            invalidate();
        }
    }

    public void setColor(String color){
        invalidate();
        paintColor = Color.parseColor(color);
        _paintDoodle.setColor(paintColor);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        float xPos = motionEvent.getX();
        float yPos = motionEvent.getY();

        switch(motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                _path.moveTo(xPos, yPos);
                break;
            case MotionEvent.ACTION_MOVE:
                _path.lineTo(xPos, yPos);
                break;
            case MotionEvent.ACTION_UP:
                //drawCanvas.drawPath(_path,_paintDoodle);
                RectF dot = new RectF();
                _path.computeBounds(dot,false);
                if(dot.isEmpty()) {
                    makeDot(xPos,yPos);
                }

                    Paint currPaint = new Paint();
                    currPaint.set(_paintDoodle);
                paths.add(new PathObject(currPaint, _path));
                    _path = new Path();

                //_path.reset();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    private void makeDot(float x, float y) {
        _path.moveTo(x,y);
        _path.lineTo(x+1,y+1);
    }
}
