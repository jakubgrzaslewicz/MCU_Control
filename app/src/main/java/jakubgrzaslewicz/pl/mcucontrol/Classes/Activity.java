package jakubgrzaslewicz.pl.mcucontrol.Classes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.afinal.simplecache.ACache;

/**
 * Created by Jakub Grząślewicz on 16.01.2018.
 */

public class Activity extends AppCompatActivity {
    public static String TAG = "MCU_CONTROL";
    public ACache Cache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Cache = ACache.get(this);
    }
}
