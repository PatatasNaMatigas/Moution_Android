package ojt.g1.moution;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import ojt.g1.connectivity.NetworkHelper;

public class Moution extends AppCompatActivity {

    private static final float SENSITIVITY = 1.5f;
    private static final float MIN_MOVE_THRESHOLD = SENSITIVITY / 2;
    private float lastX, lastY;
    private NetworkHelper networkHelper;
    private final String SERVER_IP = "192.168.0.102";
    private final int SERVER_PORT = 25135;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        networkHelper = new NetworkHelper();
        networkHelper.connectToServer(SERVER_IP, SERVER_PORT);

        View trackpad = findViewById(R.id.trackpad);
        Button lmb = findViewById(R.id.lmb);
        Button rmb = findViewById(R.id.rmb);

        trackpad.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = event.getX();
                    lastY = event.getY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = (event.getX() - lastX) * SENSITIVITY;
                    float deltaY = (event.getY() - lastY) * SENSITIVITY;

                    if (Math.abs(deltaX) > MIN_MOVE_THRESHOLD || Math.abs(deltaY) > MIN_MOVE_THRESHOLD) {
                        lastX = event.getX();
                        lastY = event.getY();
                        networkHelper.sendMessage("mm%" + deltaX + "|" + deltaY);
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
            networkHelper.connectToServer(SERVER_IP, SERVER_PORT);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkHelper.close();
    }
}