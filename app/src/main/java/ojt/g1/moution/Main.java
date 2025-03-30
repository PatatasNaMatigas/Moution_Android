package ojt.g1.moution;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import ojt.g1.connectivity.NetworkHelper;
import ojt.g1.layoutediting.LayoutEditor;

public class Main extends AppCompatActivity {

    private static final float SENSITIVITY = 1.5f;
    private static final float MIN_MOVE_THRESHOLD = SENSITIVITY / 2;
    private static final float SCROLL_MOVE_THRESHOLD = 10;
    private static final long DOUBLE_TAP_THRESHOLD = 300; // Max time between taps in ms
    private static float lastTPX;
    private static float lastTPY;
    private static float lastScrollY;
    private static float lastScrollX;
    private long lastTapTime = 0;
    private boolean scrolling;
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
        trackpad.setTag("trackpad|touchable");
        ImageView lmb = findViewById(R.id.left_click_ic);
        lmb.setTag("lmb|touchable");
        View mmb = findViewById(R.id.scroll_wheel);
        mmb.setTag("mmb|touchable");
        ImageView rmb = findViewById(R.id.right_click_ic);
        rmb.setTag("rmb|touchable");

        trackpad.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastTPX = event.getX();
                    lastTPY = event.getY();

                    long currentTime = SystemClock.uptimeMillis();
                    if (currentTime - lastTapTime <= DOUBLE_TAP_THRESHOLD) {
                        networkHelper.sendMessage("a%lmb%d");
                    }
                    lastTapTime = currentTime;
                    return true;
                case MotionEvent.ACTION_POINTER_DOWN:
                    lastScrollY = event.getY(1);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (event.getPointerCount() == 1) {
                        float deltaX = (event.getX() - lastTPX) * SENSITIVITY;
                        float deltaY = (event.getY() - lastTPY) * SENSITIVITY;

                        if ((Math.abs(deltaX) > MIN_MOVE_THRESHOLD || Math.abs(deltaY) > MIN_MOVE_THRESHOLD)) {
                            lastTPX = event.getX();
                            lastTPY = event.getY();
                            networkHelper.sendMessage("mm%" + deltaX + "|" + deltaY);
                        }
                    } else if (event.getPointerCount() == 2) {
                        float deltaY = (event.getY(1) - lastScrollY);

                        if (Math.abs(deltaY) > SCROLL_MOVE_THRESHOLD) {
                            lastScrollY = event.getY(1);
                            networkHelper.sendMessage("sc%" + -deltaY + "%y");
                        }
                        networkHelper.sendMessage("a%mmb%u");
                    }
                    return true;
                case MotionEvent.ACTION_POINTER_UP:
                    if (event.getPointerCount() == 2) {
                        return true;
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    networkHelper.sendMessage("a%lmb%u");
                    return true;
            }
            return false;
        });

        mmb.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastScrollY = event.getY();
                    lastScrollX = event.getX();
                    networkHelper.sendMessage("a%mmb%d");
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float deltaY = (event.getY() - lastScrollY);
                    float deltaX = (event.getX() - lastScrollX);

                    if (Math.abs(deltaY - deltaX) > 5 && Math.abs(deltaY) > SCROLL_MOVE_THRESHOLD) {
                        lastScrollY = event.getY();
                        networkHelper.sendMessage("sc%" + deltaY + "%y");
                    } else if (Math.abs(deltaX - deltaY) > 5 && Math.abs(deltaX) > SCROLL_MOVE_THRESHOLD) {
                        lastScrollX = event.getX();
                        networkHelper.sendMessage("sc%" + deltaX + "%x");
                    }
                    networkHelper.sendMessage("a%mmb%u");
                    return true;
                case MotionEvent.ACTION_UP:
                    networkHelper.sendMessage("a%mmb%u");
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

        findViewById(R.id.disconnect).setOnClickListener(v -> {
            networkHelper.close(this);
        });

        findViewById(R.id.edit_layout).setOnClickListener(v -> {
            new LayoutEditor(this, findViewById(R.id.main_layout));
        });

//        Profiles profiles = new Profiles(this);
//        profiles.newProfile(this, findViewById(R.id.main_layout), "default");
//        profiles.setProfile(this, findViewById(R.id.main_layout), "default");
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

    public void initializeTasks() {
        File tasksDirectory = new File(getPackageName(), "tasks");
        File[] files = tasksDirectory.listFiles();
        Task[] tasks = new Task[files.length];
        for (int i = 0; i < files.length; i++) {
            String[] data = new String[4];
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(files[i]))) {
                data[0] = bufferedReader.readLine(); // subj
                data[1] = bufferedReader.readLine(); // taskn
                data[2] = bufferedReader.readLine(); // dued
                data[3] = bufferedReader.readLine(); // duet
            } catch (Exception e) {
                e.printStackTrace();
            }
            tasks[i] = new Task();
            tasks[i].subject = data[0];
            tasks[i].taskName = data[1];
            tasks[i].dueDate = data[2];
            tasks[i].dueTime = data[3];
        }
    }

    public static class Task {
        String subject;
        String taskName;
        String dueDate;
        String dueTime;
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