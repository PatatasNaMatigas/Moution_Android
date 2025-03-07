package ojt.g1.layoutediting;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import ojt.g1.moution.R;

public class LayoutEditor extends AppCompatActivity {

    private float lastTPX;
    private float lastTPY;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        View test = findViewById(R.id.trackpad);
        TabHandles tabHandles = new TabHandles(this, findViewById(R.id.main_layout), test);

        test.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Store the initial touch position (absolute to parent)
                    lastTPX = event.getRawX() - v.getX();
                    lastTPY = event.getRawY() - v.getY();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    // Move the view based on the absolute raw touch position
                    float newX = event.getRawX() - lastTPX;
                    float newY = event.getRawY() - lastTPY;

                    v.setX(newX);
                    v.setY(newY);
                    tabHandles.updateTabs();
                    return true;
            }
            return false;
        });

    }
}
