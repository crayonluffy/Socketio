package comp4342.polyu.socketio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ming on 10/4/2017.
 */
public class DrawingView extends View {

    public Paint paint;
    public Canvas canvas;
    public Path path;
    public Bitmap canvasBitmap;
    public ArrayList<Path> paths = new ArrayList<Path>();
    public ArrayList<Path> undonePaths = new ArrayList<Path>();
    public Map<Integer, ArrayList<Path>> pathsMap = new HashMap<Integer, ArrayList<Path>>();
    public Map<Integer, ArrayList<Path>> undonePathsMap = new HashMap<Integer, ArrayList<Path>>();
    public float xPos,yPos;
    public int paintColor = Color.BLACK;
    public int brushSize = 10;
    public BlankFragment fragment = new BlankFragment();
    public int count = 0;
    public float mX, mY;
    public static final float TOUCH_TOLERANCE = 4;
    private int page = 1;

    public DrawingView(Context ctx)
    {
        super(ctx);
        setupDrawing();
        fragment.connect();

    }

    public void setupDrawing()
    {
        paint = new Paint();
        path = new Path();
        canvas = new Canvas();
        canvas.drawColor(Color.TRANSPARENT);
        paint.setAntiAlias(true);
        paint.setColor(paintColor);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(brushSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (pathsMap.get(page)!=null) {
            for (Path p : pathsMap.get(page)) {
                canvas.drawPath(p, paint);
            }
        }
        else
        {
            canvas.drawColor(Color.TRANSPARENT);
        }
        canvas.drawPath(path, paint);
    }

    public boolean onTouchEvent(MotionEvent event)
    {
        xPos = event.getX();
        yPos = event.getY();

        fragment.sendMessage(sendPath(event.getAction()));

        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(xPos,yPos);
                mX = xPos;
                mY = yPos;
                return true;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(xPos - mX);
                float dy = Math.abs(yPos - mY);
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    path.quadTo(mX, mY, (xPos + mX)/2, (yPos + mY)/2);
                    mX = xPos;
                    mY = yPos;
                }
                break;
            case MotionEvent.ACTION_UP:
                path.lineTo(mX,mY);
                //canvas.drawPath(path, paint);
                paths.add(path);
                pathsMap.put(page,paths);
                path = new Path();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public JSONObject sendPath(int action)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("page",page);
            json.put("count",count);
            json.put("action", action);
            json.put("size", brushSize);
            json.put("x", xPos);
            json.put("y", yPos);
        } catch (Exception e) {
            Log.e("json", e.toString());
        }
        count++;
        return json;
    }

    public void PaintPath(int action,float x,float y)
    {
        switch(action)
        {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x,y);
                mX = x;
                mY = y;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - mX);
                float dy = Math.abs(y - mY);
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    path.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                    mX = x;
                    mY = y;
                }
                break;
            case MotionEvent.ACTION_UP:
                path.lineTo(mX,mY);
                canvas.drawPath(path, paint);
                paths.add(path);
                path = new Path();
                break;
            default:
                break;
        }
        invalidate();
    }

    public JSONObject sendUnReDo(String doing)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("doing",doing);
        } catch (Exception e) {
            Log.e("json", e.toString());
        }
        return json;
    }

    public void onClickUndo () {
        onUndo();
        fragment.sendDoing(sendUnReDo("undo"));
    }

    public void onClickRedo (){
        onRedo();
        fragment.sendDoing(sendUnReDo("redo"));
    }

    public void onUndo () {
        if (paths.size()>0)
        {
            undonePaths.add(paths.remove(paths.size()-1));
            undonePathsMap.put(page,undonePaths);
            invalidate();
        }
        else
        {
            // I think nothing will be added here now
        }
    }

    public void onRedo (){
        if (undonePaths.size()>0)
        {
            paths.add(undonePaths.remove(undonePaths.size()-1));
            undonePathsMap.put(page,undonePaths);
            invalidate();
        }
        else
        {
            // I think nothing will be added here now
        }
    }

    public void onNextPage(int currentPage,int nextPage)
    {
        pathsMap.put(currentPage,paths);
        if(pathsMap.get(nextPage)==null) {
            paths = new ArrayList<Path>();
        }
        else {
            paths = pathsMap.get(nextPage);
        }
        if(undonePathsMap.get(nextPage)!=null) {
            undonePaths = undonePathsMap.get(nextPage);
        }
        else {
            undonePaths = new ArrayList<Path>();
        }

        setupDrawing();
        invalidate();
        page++;
    }

    public void onPrevPage(int currentPage, int prevPage)
    {
        pathsMap.put(currentPage,paths);
        paths = pathsMap.get(prevPage);
        if(undonePathsMap.get(prevPage)!=null)
            undonePaths = undonePathsMap.get(prevPage);
        else
            undonePaths = new ArrayList<Path>();
        setupDrawing();
        invalidate();
        page--;
    }
}
