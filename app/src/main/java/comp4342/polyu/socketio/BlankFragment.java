package comp4342.polyu.socketio;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

import static comp4342.polyu.socketio.MainActivity.drawing;
import static comp4342.polyu.socketio.MainActivity.syn;

public class BlankFragment extends Fragment {
    EditText content;
    Button send;
    TextView hello;
    public JSONObject json;
    int count=0;
    private Socket socket;
    {
        try{
            socket = IO.socket("http://192.168.1.154:3000");
        }catch(URISyntaxException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        send = (Button) view.findViewById(R.id.send);
        content = (EditText) view.findViewById(R.id.content);
        hello = (TextView) view.findViewById(R.id.textView);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendMessage();
            }
        });
    }

    public void connect()
    {
        socket.connect();
        socket.on("message", handleIncomingMessages);
        socket.on("doing",handleIncomingDoing);
    }

    public void sendMessage(JSONObject json){
        socket.emit("message", json);
    }

    public void sendDoing(JSONObject json){
        socket.emit("doing", json);
    }

    public void sendCount(int count){
        JSONObject json = new JSONObject();
        try {
            json.put("count",count);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("count", json);
    }

    private Emitter.Listener handleIncomingMessages = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            new SynDrawing().execute((JSONObject) args[0]);
        }
    };

    private Emitter.Listener handleIncomingDoing = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            new SynDoing().execute((JSONObject) args[0]);
        }
    };

        @Override
    public void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }

    public class SynDrawing extends AsyncTask<JSONObject,String,JSONObject> {

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            JSONObject data = params[0];
            return data;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);

            syn.setText("hello:"+result.toString());
            try {
                int ServerCount = result.getInt("count");
                float x = (float) result.getDouble("x");
                float y = (float) result.getDouble("y");
                int action = result.getInt("action");
                if (ServerCount==count) {
                    drawing.PaintPath(action, x, y);
                    count++;
                }
                else {
                        sendCount(count);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class SynDoing extends AsyncTask<JSONObject,String,JSONObject> {

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            JSONObject data = params[0];
            return data;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);

            try {
                String doing = result.getString("doing");
                if (doing.equals("undo"))
                    drawing.onUndo();
                else if (doing.equals("redo"))
                    drawing.onRedo();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class SynCount extends AsyncTask<JSONObject,String,JSONObject> {

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            JSONObject data = params[0];
            return data;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);

            try {
                String doing = result.getString("doing");
                if (doing.equals("undo"))
                    drawing.onUndo();
                else if (doing.equals("redo"))
                    drawing.onRedo();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
