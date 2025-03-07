package ojt.g1.uicustomization;

import android.content.Context;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.File;
import java.util.ArrayList;

public class Profiles {

    private ArrayList<LayoutData> layoutData = new ArrayList<>();

    public Profiles(Context context) {
        File profilesDirectory = new File(context.getFilesDir(), "profiles");
        if (!profilesDirectory.exists())
            profilesDirectory.mkdirs();

        File[] profiles = profilesDirectory.listFiles();
        if (profiles != null) {
            for (File profile : profiles) {
                layoutData.add(new LayoutData(new ConstraintLayout(context), profile.getName()));
            }
        }
    }

    public void newProfile(Context context, ConstraintLayout constraintLayout, String name) {
        LayoutData newLayout = new LayoutData(constraintLayout, name);
        layoutData.add(newLayout);
        newLayout.saveAllComponentData(context);
    }

    public void setProfile(Context context, ConstraintLayout constraintLayout, String name) {
        for (LayoutData layout : layoutData) {
            if (layout.getProfileName().equals(name)) {
                layout.loadAllComponentData(context, constraintLayout);
                return;
            }
        }
    }
}
