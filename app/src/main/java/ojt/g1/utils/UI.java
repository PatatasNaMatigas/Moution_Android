package ojt.g1.utils;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

public class UI {

    public static Views getComponents(ConstraintLayout constraintLayout) {
        int childCount = constraintLayout.getChildCount();
        Views views = new Views();

        for (int i = 0; i < childCount; i++) {
            View view = constraintLayout.getChildAt(i);

            if (view instanceof Button) {
                views.addButton((Button) view);
            } else if (view instanceof TextView) {
                views.addTextView((TextView) view);
            } else if (view != null) {
                views.addView(view);
            }
        }

        return views;
    }

    public static class Views {

        private ArrayList<Button> buttons;
        private ArrayList<TextView> textViews;
        private ArrayList<View> views;

        public void addButton(Button button) {
            buttons.add(button);
        }

        public void addTextView(TextView textView) {
            textViews.add(textView);
        }

        public void addView(View view) {
            views.add(view);
        }

        public ArrayList<Button> getButtons() {
            return buttons;
        }

        public ArrayList<TextView> getTextViews() {
            return textViews;
        }

        public ArrayList<View> getViews() {
            return views;
        }
    }
}
