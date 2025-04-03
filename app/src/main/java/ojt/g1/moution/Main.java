package ojt.g1.moution;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

import ojt.g1.connectivity.NetworkHelper;
import ojt.g1.layoutediting.LayoutEditor;
import ojt.g1.moution.adapter.ShortCutAdapter;

public class Main extends AppCompatActivity {

    private static final float SENSITIVITY = 1.5f;
    private static final float MIN_MOVE_THRESHOLD = SENSITIVITY / 2;
    private static final float SCROLL_MOVE_THRESHOLD = 5;
    private static float lastTPX;
    private static float lastTPY;
    private static float lastScrollY;
    public static NetworkHelper networkHelper;
    private static String SERVER_IP = "";
    private final int SERVER_PORT = 25135;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        networkHelper = new NetworkHelper();
        networkHelper.connectToServer(this, SERVER_IP, SERVER_PORT);

        View trackpad = findViewById(R.id.trackpad);
        trackpad.setTag("trackpad|touchable");
        ImageView lmb = findViewById(R.id.left_click_ic);
        lmb.setTag("lmb|touchable");
        View mmb = findViewById(R.id.scroll_wheel);
        mmb.setTag("mmb|touchable");
        ImageView rmb = findViewById(R.id.right_click_ic);
        rmb.setTag("rmb|touchable");

        trackpad.setOnTouchListener(getTrackpadTouchFunction());
        mmb.setOnTouchListener(getMMBTouchFunction());
        lmb.setOnTouchListener(getLMBTouchFunction());
        rmb.setOnTouchListener(getRMBTouchFunction());

        findViewById(R.id.disconnect).setOnClickListener(v -> {
            networkHelper.close(this);
        });

        findViewById(R.id.hotkeys).setOnClickListener(v -> {
            findViewById(R.id.darken_filter).setVisibility(View.VISIBLE);
            findViewById(R.id.shortcuts).setVisibility(View.VISIBLE);
            findViewById(R.id.exit).setVisibility(View.VISIBLE);
            mmb.setOnTouchListener(null);
            lmb.setOnTouchListener(null);
            rmb.setOnTouchListener(null);
            trackpad.setOnTouchListener(null);
            findViewById(R.id.disconnect).setClickable(false);
        });

        findViewById(R.id.exit).setOnClickListener(v -> {
            findViewById(R.id.darken_filter).setVisibility(View.GONE);
            findViewById(R.id.shortcuts).setVisibility(View.GONE);
            findViewById(R.id.add_shortcut).setVisibility(View.GONE);
            findViewById(R.id.exit).setVisibility(View.GONE);
            mmb.setOnTouchListener(getMMBTouchFunction());
            lmb.setOnTouchListener(getLMBTouchFunction());
            rmb.setOnTouchListener(getRMBTouchFunction());
            trackpad.setOnTouchListener(getTrackpadTouchFunction());
            findViewById(R.id.disconnect).setClickable(true);
        });

        findViewById(R.id.copy_button).setOnClickListener(v -> {
            networkHelper.sendMessage("shc%copy");
        });

        findViewById(R.id.paste_button).setOnClickListener(v -> {
            networkHelper.sendMessage("shc%paste");
        });

        findViewById(R.id.redo_button).setOnClickListener(v -> {
            networkHelper.sendMessage("shc%redo");
        });

        findViewById(R.id.undo_button).setOnClickListener(v -> {
            networkHelper.sendMessage("shc%undo");
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public static View.OnTouchListener getLMBTouchFunction() {
        return (v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    networkHelper.sendMessage("a%lmb%d");
                    return true;
                case MotionEvent.ACTION_UP:
                    networkHelper.sendMessage("a%lmb%u");
                    return true;
            }
            return false;
        };
    }

    @SuppressLint("ClickableViewAccessibility")
    public static View.OnTouchListener getRMBTouchFunction() {
        return (v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    networkHelper.sendMessage("a%rmb%d");
                    return true;
                case MotionEvent.ACTION_UP:
                    networkHelper.sendMessage("a%rmb%u");
                    return true;
            }
            return false;
        };
    }

    @SuppressLint("ClickableViewAccessibility")
    public static View.OnTouchListener getMMBTouchFunction() {
        return (v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastScrollY = event.getY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float deltaY = (event.getY() - lastScrollY) * SENSITIVITY;

                    if (Math.abs(deltaY) > SCROLL_MOVE_THRESHOLD) {
                        lastScrollY = event.getY();
                        networkHelper.sendMessage("sc%" + deltaY);
                    }
                    return true;
            }
            return false;
        };
    }

    @SuppressLint("ClickableViewAccessibility")
    public static View.OnTouchListener getTrackpadTouchFunction() {
        return (v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastTPX = event.getX();
                    lastTPY = event.getY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = (event.getX() - lastTPX) * SENSITIVITY;
                    float deltaY = (event.getY() - lastTPY) * SENSITIVITY;

                    if (Math.abs(deltaX) > MIN_MOVE_THRESHOLD || Math.abs(deltaY) > MIN_MOVE_THRESHOLD) {
                        lastTPX = event.getX();
                        lastTPY = event.getY();
                        networkHelper.sendMessage("mm%" + deltaX + "|" + deltaY);
                    }
                    return true;
            }
            return false;
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkHelper.close(this);
    }

    protected static void setServerIp(String ip) {
        SERVER_IP = ip;
    }
}