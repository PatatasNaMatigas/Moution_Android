package ojt.g1.layoutediting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.ArrayList;

import ojt.g1.uicustomization.LayoutData;

public class LayoutEditor {

    private float lastTPX;
    private float lastTPY;

    @SuppressLint("ClickableViewAccessibility")
    public LayoutEditor(Context context, ConstraintLayout constraintLayout) {
        ArrayList<View> allTouchables = LayoutData.getAllTouchables(constraintLayout);
        boolean[] active = new boolean[allTouchables.size()];
        TabHandles[] tabHandles = new TabHandles[allTouchables.size()];

        for (int i = 0; i < allTouchables.size(); i++) {
            View view = allTouchables.get(i);

            ConstraintLayout parentLayout = (ConstraintLayout) view.getParent();
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(parentLayout);
            constraintSet.clear(view.getId());
            constraintSet.applyTo(parentLayout);
            
            int finalI = i;
            view.setOnClickListener(v -> {
                if (tabHandles[finalI] == null)
                    tabHandles[finalI] = new TabHandles(context, constraintLayout, v);
                active[finalI] = true;
            });

            view.setOnTouchListener((v, event) -> {
                if (active[finalI]) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            lastTPX = event.getRawX() - v.getX();
                            lastTPY = event.getRawY() - v.getY();
                            return false;

                        case MotionEvent.ACTION_MOVE:
                            float newX = event.getRawX() - lastTPX;
                            float newY = event.getRawY() - lastTPY;

                            v.setX(newX);
                            v.setY(newY);

                            if (tabHandles[finalI] == null)
                                tabHandles[finalI] = new TabHandles(context, constraintLayout, v);

                            tabHandles[finalI].updateTabs();
                            return true;
                    }
                }
                return false;
            });
        }
    }
}
