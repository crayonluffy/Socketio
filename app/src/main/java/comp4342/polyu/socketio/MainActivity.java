package comp4342.polyu.socketio;

import android.graphics.Color;
import android.graphics.Path;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static DrawingView drawing;
    public static JSONObject json;
    public static TextView syn,pageTv;
    Button undo,redo,next,prev;
    public int page = 1;
    public int count = 0;
    public static Map<Integer, Integer> pageMap = new HashMap<Integer, Integer>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        syn = (TextView) findViewById(R.id.receiveData);
        pageTv = (TextView) findViewById(R.id.page);

        pageTv.setText(""+page);

        //--------------------------------------------------------------------------------------------------------------------------------------------------------
        //Add the TouchEventView to activity_main.xml
        View C = findViewById(R.id.drawing);
        ViewGroup parent = (ViewGroup) C.getParent();
        int index = parent.indexOfChild(C);
        parent.removeView(C);
        drawing = new DrawingView(this);
        drawing.setId(R.id.drawing);
        drawing.setBackgroundColor(Color.YELLOW);
        parent.addView(drawing, index);
        //--------------------------------------------------------------------------------------------------------------------------------------------------------

        undo = (Button) findViewById(R.id.undo);
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawing.onClickUndo();
            }
        });

        redo = (Button) findViewById(R.id.redo);
        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawing.onClickRedo();
            }
        });

        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(page!=100) {
                    page++;
                    drawing.onNextPage(page-1,page);
                    pageTv.setText("" + page);
                }
            }
        });

        prev = (Button) findViewById(R.id.prev);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(page>1) {
                    page--;
                    drawing.onPrevPage(page+1,page);
                    pageTv.setText("" + page);
                }
            }
        });

    }

}
