package ojt.g1.uicustomization;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import ojt.g1.moution.Main;

public class LayoutData {

    private ConstraintLayout constraintLayout;

    private String profileName;

    public LayoutData(ConstraintLayout constraintLayout, String profileName) {
        this.constraintLayout = constraintLayout;
        this.profileName = profileName;
    }

    public void saveAllComponentData(Context context) {
        int childCount = constraintLayout.getChildCount();
        JSONArray componentArray = new JSONArray();

        for (int i = 0; i < childCount; i++) {
            View view = constraintLayout.getChildAt(i);

            if (view instanceof Button) {
                Button button = (Button) view;
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", button.getId());
                    jsonObject.put("x", button.getX());
                    jsonObject.put("y", button.getY());
                    jsonObject.put("width", button.getWidth());
                    jsonObject.put("height", button.getHeight());
                    jsonObject.put("action", button.getTag() != null ? button.getTag().toString() : "defaultAction");

                    componentArray.put(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (view instanceof TextView) {
                TextView textView = (TextView) view;
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", textView.getId());
                    jsonObject.put("x", textView.getX());
                    jsonObject.put("y", textView.getY());
                    jsonObject.put("width", textView.getWidth());
                    jsonObject.put("height", textView.getHeight());
                    jsonObject.put("text", textView.getText());
                    jsonObject.put("action", textView.getTag() != null ? textView.getTag().toString() : "defaultAction");

                    componentArray.put(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (view != null) {
                View v = view;
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", v.getId());
                    jsonObject.put("x", v.getX());
                    jsonObject.put("y", v.getY());
                    jsonObject.put("width", v.getWidth());
                    jsonObject.put("height", v.getHeight());
                    jsonObject.put("action", v.getTag() != null ? v.getTag().toString() : "defaultAction");

                    componentArray.put(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        File file = new File(context.getFilesDir() + "/profiles", profileName + ".json");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(componentArray.toString(4));
            writer.flush();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadAllComponentData(Context context, ConstraintLayout constraintLayout) {
        File file = new File(context.getFilesDir() + "/profiles", profileName + ".json");

        if (!file.exists()) {
            Log.e("LayoutData", "File not found: " + file.getAbsolutePath());
            return;
        }

        StringBuilder jsonString = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
                Log.d("Test--", line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            JSONArray componentArray = new JSONArray(jsonString.toString());
            constraintLayout.removeAllViews();

            for (int i = 0; i < componentArray.length(); i++) {
                JSONObject jsonObject = componentArray.getJSONObject(i);
                int id = jsonObject.getInt("id");
                float x = (float) jsonObject.getDouble("x");
                float y = (float) jsonObject.getDouble("y");
                int width = jsonObject.getInt("width");
                int height = jsonObject.getInt("height");

                View view = null;

                if (jsonObject.has("text")) {
                    TextView textView = new TextView(context);
                    textView.setText(jsonObject.getString("text"));
                    String tag = jsonObject.getString("action");
                    textView.setTag(tag);
                    if (tag.equals("mmb"))
                        textView.setOnTouchListener(Main.getMMBTouchFunction());
                    view = textView;
                } else if (jsonObject.has("action")) {
                    Button button = new Button(context);
                    button.setText("Button");
                    String tag = jsonObject.getString("action");
                    button.setTag(tag);
                    switch (tag) {
                        case "lmb":
                            button.setOnTouchListener(Main.getLMBTouchFunction());
                            break;
                        case "rmb":
                            button.setOnTouchListener(Main.getRMBTouchFunction());
                    }
                    view = button;
                }
                if (view != null) {
                    String tag = jsonObject.getString("action");
                    view.setTag(tag);
                    if (tag.equals("trackpad"))
                        view.setOnTouchListener(Main.getTrackpadTouchFunction());
                    view.setId(id);
                    ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(width, height);
                    view.setLayoutParams(params);
                    constraintLayout.addView(view);

                    view.setX(x);
                    view.setY(y);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getProfileName() {
        return profileName;
    }
}
