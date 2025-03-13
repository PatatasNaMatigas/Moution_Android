package ojt.g1.layoutediting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import ojt.g1.moution.R;

public class TabHandles {

    private View targetView;
    private View top;
    private View bottom;
    private View right;
    private View left;
    private float lastTPX;
    private float lastTPY;
    private float lastViewX;
    private float lastViewY;

    @SuppressLint("ClickableViewAccessibility")
    public TabHandles(Context context, ViewGroup parent, View targetView) {
        if (parent == null) {
            throw new IllegalArgumentException("Parent ViewGroup cannot be null");
        }

        this.targetView = targetView;

        // Top
        top = new View(context);
        top.setBackgroundResource(R.drawable.handle);
        top.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 40));

        top.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastTPY = event.getRawY();
                    lastViewY = targetView.getY();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float deltaY = event.getRawY() - lastTPY;

                    ViewGroup.LayoutParams viewParams = targetView.getLayoutParams();
                    int newHeight = Math.max(50, viewParams.height - (int) deltaY);
                    float heightDiff = (viewParams.height - newHeight) / 2f;
                    viewParams.height = newHeight;

                    targetView.setLayoutParams(viewParams);
                    targetView.setY(targetView.getY() + heightDiff);
                    v.setY(targetView.getY() - v.getHeight() + (top.getLayoutParams().height / 2f));

                    lastTPY = event.getRawY();

                    updateTabs();
                    return true;
            }
            return false;
        });

        // Bottom
        bottom = new View(context);
        bottom.setBackgroundResource(R.drawable.handle);
        bottom.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 40));
        bottom.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastTPY = event.getRawY();
                    lastViewY = targetView.getY();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float deltaY = event.getRawY() - lastTPY;

                    ViewGroup.LayoutParams viewParams = targetView.getLayoutParams();
                    viewParams.height = Math.max(50, viewParams.height + (int) deltaY);
                    targetView.setLayoutParams(viewParams);

                    v.setY(targetView.getY() + targetView.getHeight() - v.getHeight() + (bottom.getLayoutParams().height / 2f));
                    targetView.setY(lastViewY);
                    lastTPY = event.getRawY();

                    updateTabs();
                    return true;
            }
            return false;
        });

        // Left
        right = new View(context);
        right.setBackgroundResource(R.drawable.handle);
        right.setLayoutParams(new ViewGroup.LayoutParams(40, ViewGroup.LayoutParams.WRAP_CONTENT));

        right.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastTPX = event.getRawX();
                    lastViewX = targetView.getX();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float deltaX = event.getRawX() - lastTPX;

                    ViewGroup.LayoutParams viewParams = targetView.getLayoutParams();
                    viewParams.width = Math.max(50, viewParams.width + (int) deltaX);
                    targetView.setLayoutParams(viewParams);

                    v.setX(targetView.getX() + targetView.getWidth() - (right.getLayoutParams().width / 2f));
                    targetView.setX(lastViewX);
                    lastTPX = event.getRawX();

                    updateTabs();
                    return true;
            }
            return false;
        });

        // Left Handle
        left = new View(context);
        left.setBackgroundResource(R.drawable.handle);
        left.setLayoutParams(new ViewGroup.LayoutParams(40, ViewGroup.LayoutParams.WRAP_CONTENT));
        left.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastTPX = event.getRawX();
                    lastViewX = targetView.getX();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float deltaX = event.getRawX() - lastTPX;

                    ViewGroup.LayoutParams viewParams = targetView.getLayoutParams();
                    viewParams.width = Math.max(50, viewParams.width - (int) deltaX);
                    targetView.setLayoutParams(viewParams);

                    targetView.setX(lastTPX + deltaX);
                    v.setX(targetView.getX() - (left.getLayoutParams().width / 2f));
                    lastTPX = event.getRawX();
                    updateTabs();
                    return true;
            }
            return false;
        });

        ViewGroup.LayoutParams params = left.getLayoutParams();
        params.height = 150;
        left.setLayoutParams(params);
        left.setX(targetView.getX() - (params.width / 2f));
        left.setY((targetView.getY() + (targetView.getHeight() / 2f)) - params.height / 2f);

        parent.addView(left);

        ViewGroup.LayoutParams rightParams = right.getLayoutParams();
        rightParams.height = 150;
        right.setLayoutParams(rightParams);
        right.setX(targetView.getX() + targetView.getWidth() - (rightParams.width / 2f));
        right.setY((targetView.getY() + (targetView.getHeight() / 2f)) - rightParams.height / 2f);

        parent.addView(right);

        ViewGroup.LayoutParams bottomParams = bottom.getLayoutParams();
        bottomParams.width = 150;
        bottom.setLayoutParams(bottomParams);
        bottom.setX(targetView.getX() + (targetView.getWidth() / 2f) - (bottomParams.width / 2f));
        bottom.setY((targetView.getY() + targetView.getHeight()) - bottomParams.height / 2f);

        parent.addView(bottom);

        ViewGroup.LayoutParams topParams = top.getLayoutParams();
        topParams.width = 150;
        top.setLayoutParams(topParams);
        top.setX(targetView.getX() + (targetView.getWidth() / 2f) - (topParams.width / 2f));
        top.setY(targetView.getY() - topParams.height / 2f);

        parent.addView(top);
        top.requestLayout();
    }

    public void updateTabs() {
        targetView.post(() -> {
            ViewGroup.LayoutParams topParams = top.getLayoutParams();
            top.setX(targetView.getX() + (targetView.getWidth() / 2f) - (topParams.width / 2f));
            top.setY(targetView.getY() - topParams.height / 2f);

            ViewGroup.LayoutParams bottomParams = bottom.getLayoutParams();
            bottom.setX(targetView.getX() + (targetView.getWidth() / 2f) - (bottomParams.width / 2f));
            bottom.setY((targetView.getY() + targetView.getHeight()) - bottomParams.height / 2f);

            ViewGroup.LayoutParams rightParams = right.getLayoutParams();
            right.setX(targetView.getX() + targetView.getWidth() - (rightParams.width / 2f));
            right.setY((targetView.getY() + (targetView.getHeight() / 2f)) - rightParams.height / 2f);

            ViewGroup.LayoutParams leftParams = left.getLayoutParams();
            left.setX(targetView.getX() - (leftParams.width / 2f));
            left.setY((targetView.getY() + (targetView.getHeight() / 2f)) - leftParams.height / 2f);

            Log.d("Test--", top.getWidth() + " " + top.getHeight());
        });
    }

}