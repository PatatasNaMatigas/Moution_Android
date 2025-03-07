package ojt.g1.moution;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ojt.g1.connectivity.NetworkHelper;
import ojt.g1.uicustomization.Profiles;

public class Main extends AppCompatActivity {

    private static final float SENSITIVITY = 1.5f;
    private static final float MIN_MOVE_THRESHOLD = SENSITIVITY / 2;
    private static final float SCROLL_MOVE_THRESHOLD = 10;
    private static float lastTPX;
    private static float lastTPY;
    private static float lastScrollY;
    private static NetworkHelper networkHelper;
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
        trackpad.setTag("trackpad");
        Button lmb = findViewById(R.id.lmb);
        lmb.setTag("lmb");
        TextView mmb = findViewById(R.id.scroll_wheel);
        mmb.setTag("mmb");
        Button rmb = findViewById(R.id.rmb);
        rmb.setTag("rmb");

        trackpad.setOnTouchListener((v, event) -> {
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
        });

        mmb.setOnTouchListener((v, event) -> {
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
        });

        lmb.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    networkHelper.sendMessage("a%lmb%d");
                    return true;
                case MotionEvent.ACTION_UP:
                    networkHelper.sendMessage("a%lmb%u");
                    return true;
            }
            return false;
        });
        rmb.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    networkHelper.sendMessage("a%rmb%d");
                    return true;
                case MotionEvent.ACTION_UP:
                    networkHelper.sendMessage("a%rmb%u");
                    return true;
            }
            return false;
        });

        findViewById(R.id.connect).setOnClickListener(v -> {
            networkHelper.close(this);
            networkHelper.connectToServer(this, SERVER_IP, SERVER_PORT);
        });

        Profiles profiles = new Profiles(this);
//        profiles.newProfile(this, findViewById(R.id.main_layout), "default");
        profiles.setProfile(this, findViewById(R.id.main_layout), "default");
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