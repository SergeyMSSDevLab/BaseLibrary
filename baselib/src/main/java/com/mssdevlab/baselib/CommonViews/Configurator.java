package com.mssdevlab.baselib.CommonViews;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import java.util.ArrayList;
import java.util.Map;

/**
 * Common views configurations holder.
 */
public class Configurator {
    private static final Map<String, Configuration> configurationMap = new ArrayMap<>(4);
    private static final Map<String, ArrayList<ConfiguratorListener>> listenersMap = new ArrayMap<>(4);;

    public static void setListener(@NonNull String tag, @NonNull ConfiguratorListener listener){
        ArrayList<ConfiguratorListener> listeners = listenersMap.get(tag);
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(listener);
        listenersMap.put(tag, listeners);
        onConfigureCompleted(tag);
    }
    public static void setConfiguration(@NonNull String tag, @NonNull Configuration config){
        configurationMap.put(tag, config);
        onConfigureCompleted(tag);
    }

    private static void onConfigureCompleted(String tag){
        Configuration config = configurationMap.get(tag);
        if (config != null){
            ArrayList<ConfiguratorListener> listeners = listenersMap.get(tag);
            for (ConfiguratorListener listener : listeners) {
                listener.onConfigureCompleted(null, tag);   // TODO: Create viewholder object from configuration
            }
        }
    }
}
