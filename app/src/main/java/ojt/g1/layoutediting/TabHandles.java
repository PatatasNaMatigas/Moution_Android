package ojt.g1.layoutediting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import ojt.g1.moution.R;

public class TabHandles {

    private View view;
    private View top;
    private View bottom;
    private View right;
    private View left;
    private float lastTPX;
    private float lastTPY;
    private float lastViewX;
    private float lastViewY;

    @SuppressLint("ClickableViewAccessibility")
    public TabHandles(Context context, ViewGroup parent, View view) {
        if (parent == null) {
            throw new IllegalArgumentException("Parent ViewGroup cannot be null");
        }

        this.view = view;

        // Top
        top = new View(context);
        top.setBackgroundResource(R.drawable.handle);
        top.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 40));

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                ViewGroup.LayoutParams params = top.getLayoutParams();
                params.width = 150;
                top.setLayoutParams(params);
                top.setX(view.getX() + (view.getWidth() / 2f) - (params.width / 2f));
                top.setY(view.getY() - params.height / 2f);

                parent.addView(top);
            }
        });

        top.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastTPY = event.getRawY();
                    lastViewY = view.getY();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float deltaY = event.getRawY() - lastTPY;

                    ViewGroup.LayoutParams viewParams = view.getLayoutParams();
                    int newHeight = Math.max(50, viewParams.height - (int) deltaY);
                    float heightDiff = (viewParams.height - newHeight) / 2f;
                    viewParams.height = newHeight;

                    view.setLayoutParams(viewParams);
                    view.setY(view.getY() + heightDiff);
                    v.setY(view.getY() - v.getHeight() + (top.getLayoutParams().height / 2f));

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

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                ViewGroup.LayoutParams params = bottom.getLayoutParams();
                params.width = 150;
                bottom.setLayoutParams(params);
                bottom.setX(view.getX() + (view.getWidth() / 2f) - (params.width / 2f));
                bottom.setY((view.getY() + view.getHeight()) - params.height / 2f);

                parent.addView(bottom);
            }
        });

        bottom.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastTPY = event.getRawY();
                    lastViewY = view.getY();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float deltaY = event.getRawY() - lastTPY;

                    ViewGroup.LayoutParams viewParams = view.getLayoutParams();
                    viewParams.height = Math.max(50, viewParams.height + (int) deltaY);
                    view.setLayoutParams(viewParams);

                    v.setY(view.getY() + view.getHeight() - v.getHeight() + (bottom.getLayoutParams().height / 2f));
                    view.setY(lastViewY);
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

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                ViewGroup.LayoutParams params = right.getLayoutParams();
                params.height = 150;
                right.setLayoutParams(params);
                right.setX(view.getX() + view.getWidth() - (params.width / 2f));
                right.setY((view.getY() + (view.getHeight() / 2f)) - params.height / 2f);

                parent.addView(right);
            }
        });

        right.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastTPX = event.getRawX();
                    lastViewX = view.getX();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float deltaX = event.getRawX() - lastTPX;

                    ViewGroup.LayoutParams viewParams = view.getLayoutParams();
                    viewParams.width = Math.max(50, viewParams.width + (int) deltaX);
                    view.setLayoutParams(viewParams);

                    v.setX(view.getX() + view.getWidth() - (right.getLayoutParams().width / 2f));
                    view.setX(lastViewX);
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

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                ViewGroup.LayoutParams params = left.getLayoutParams();
                params.height = 150;
                left.setLayoutParams(params);
                left.setX(view.getX() - (params.width / 2f));
                left.setY((view.getY() + (view.getHeight() / 2f)) - params.height / 2f);

                parent.addView(left);
            }
        });

        left.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastTPX = event.getRawX();
                    lastViewX = view.getX();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float deltaX = event.getRawX() - lastTPX;

                    ViewGroup.LayoutParams viewParams = view.getLayoutParams();
                    viewParams.width = Math.max(50, viewParams.width - (int) deltaX);
                    view.setLayoutParams(viewParams);

                    view.setX(lastTPX + deltaX);
                    v.setX(view.getX() - (left.getLayoutParams().width / 2f));
                    lastTPX = event.getRawX();
                    updateTabs();
                    return true;
            }
            return false;
        });

    }

    public void updateTabs() {
        ViewGroup.LayoutParams topParams = top.getLayoutParams();
        top.setX(view.getX() + (view.getWidth() / 2f) - (topParams.width / 2f));
        top.setY(view.getY() - topParams.height / 2f);

        ViewGroup.LayoutParams bottomParams = bottom.getLayoutParams();
        bottom.setX(view.getX() + (view.getWidth() / 2f) - (bottomParams.width / 2f));
        bottom.setY((view.getY() + view.getHeight()) - bottomParams.height / 2f);

        ViewGroup.LayoutParams rightParams = right.getLayoutParams();
        right.setX(view.getX() + view.getWidth() - (rightParams.width / 2f));
        right.setY((view.getY() + (view.getHeight() / 2f)) - rightParams.height / 2f);

        ViewGroup.LayoutParams leftParams = left.getLayoutParams();
        left.setX(view.getX() - (leftParams.width / 2f));
        left.setY((view.getY() + (view.getHeight() / 2f)) - leftParams.height / 2f);
    }

}