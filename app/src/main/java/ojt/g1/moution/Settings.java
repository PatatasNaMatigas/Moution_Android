package ojt.g1.moution;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        findViewById(R.id.copy_button).setOnClickListener(v -> {
            String mouse = ((EditText) findViewById(R.id.mouse_sensi)).getText().toString();
            String scroll = ((EditText) findViewById(R.id.scroll_sensi)).getText().toString();
            Main.SENSITIVITY = (mouse.isEmpty()) ? Main.SENSITIVITY : Float.parseFloat(mouse);
            Main.SCROLL_MOVE_THRESHOLD = (scroll.isEmpty()) ? Main.SCROLL_MOVE_THRESHOLD : Float.parseFloat(scroll);
            startActivity(new Intent(this, Main.class));
            finish();
        });
    }
}
