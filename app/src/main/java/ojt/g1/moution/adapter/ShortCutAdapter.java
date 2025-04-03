package ojt.g1.moution.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ojt.g1.moution.Main;
import ojt.g1.moution.R;

public class ShortCutAdapter extends RecyclerView.Adapter<ShortCutAdapter.ShortcutItem> {

    private List<String> shortcutList;
    private Context context;

    public ShortCutAdapter(List<String> shortcutList, Context context) {
        this.shortcutList = shortcutList;
        this.context = context;
    }

    @NonNull
    @Override
    public ShortcutItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shortcut_item, parent, false);
        return new ShortcutItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShortcutItem item, int position) {
        String shortcut = shortcutList.get(position);
        item.shortcutName.setText(shortcut);
        item.button.setOnClickListener(v -> {
            Main.networkHelper.sendMessage("shc%" + shortcut);
        });
    }

    @Override
    public int getItemCount() {
        Log.d("Main--", "getItemCount: " + shortcutList.size());
        return shortcutList.size();
    }

    public static class ShortcutItem extends RecyclerView.ViewHolder {

        ImageView holderImage;
        TextView shortcutName;
        Button button;

        public ShortcutItem(@NonNull View itemView) {
            super(itemView);
            holderImage = itemView.findViewById(R.id.holder);
            shortcutName = itemView.findViewById(R.id.shortcut_name);
            button = itemView.findViewById(R.id.button);
        }
    }
}