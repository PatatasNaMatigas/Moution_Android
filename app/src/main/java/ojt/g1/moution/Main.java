package ojt.g1.moution;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
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
    private static float lastTPX;
    private static float lastTPY;
    private static float lastScrollY;
    private static NetworkHelper networkHelper;
    private static String SERVER_IP = "";
    private final int SERVER_PORT = 25135;
    private static boolean isZooming = false;
    private static ScaleGestureDetector scaleGestureDetector;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        networkHelper = new NetworkHelper();
        networkHelper.connectToServer(this, SERVER_IP, SERVER_PORT);

        View trackpad = findViewById(R.id.trackpad);
        Button lmb = findViewById(R.id.lmb);
        View mmb = findViewById(R.id.scroll_wheel);
        Button rmb = findViewById(R.id.rmb);
        trackpad.setTag("trackpad|touchable");
        lmb.setTag("lmb|touchable");
        mmb.setTag("mmb|touchable");
        rmb.setTag("rmb|touchable");

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            private float lastScaleFactor = 1f;

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor();

                if (Math.abs(scaleFactor - lastScaleFactor) > 0.03f) {
                    lastScaleFactor = scaleFactor;

                    if (scaleFactor > 1) {
                        networkHelper.sendMessage("zm%i");
                    } else {
                        networkHelper.sendMessage("zm%o");
                    }
                }
                isZooming = true;
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                super.onScaleEnd(detector);
                isZooming = false;
            }
        });

        trackpad.setOnTouchListener(getTrackpadTouchFunction());
        lmb.setOnTouchListener(getLMBTouchFunction());
        mmb.setOnTouchListener(getMMBTouchFunction());
        rmb.setOnTouchListener(getRMBTouchFunction());

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
                    networkHelper.sendMessage("a%mmb%d");
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float deltaY = (event.getY() - lastScrollY);

                    if (Math.abs(deltaY) > SCROLL_MOVE_THRESHOLD) {
                        lastScrollY = event.getY();
                        networkHelper.sendMessage("sc%" + deltaY + "%y");
                    }
                    networkHelper.sendMessage("a%mmb%u");
                    return true;
                case MotionEvent.ACTION_UP:
                    networkHelper.sendMessage("a%mmb%u");
                    return true;
            }
            return false;
        };
    }

    @SuppressLint("ClickableViewAccessibility")
    public static View.OnTouchListener getTrackpadTouchFunction() {
        return (v, event) -> {
            scaleGestureDetector.onTouchEvent(event);

            if (event.getPointerCount() > 1) {
                isZooming = true;
            }

            if (!isZooming) {
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
                    case MotionEvent.ACTION_UP:
                        isZooming = false;
                        return true;
                }
            }

            if (event.getPointerCount() > 1) {
                isZooming = true;
            } else if (event.getPointerCount() == 1) {
                isZooming = false;
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