package com.mssdevlab.baselib.CommonViews;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import java.util.Map;

/**
 * Common views configurations holder.
 */
public class Configurator {
    private static final String LOG_TAG = Configurator.class.getCanonicalName();
    private static final Map<String, Configuration> mConfigurationMap = new ArrayMap<>(4);
    private static final Map<String, Map<String, ConfiguratorListener>> mListenersMap = new ArrayMap<>(4);

    public static void setListener(@Nullable String configTag, @Nullable String instanceTag, @NonNull ConfiguratorListener listener){
        Log.v(LOG_TAG, "setListener entered.");
        if (configTag != null && instanceTag != null) {
            Map<String, ConfiguratorListener> listeners = mListenersMap.get(configTag);
            if (listeners == null) {
                listeners = new ArrayMap<>(2);
                mListenersMap.put(configTag, listeners);
            }
            listeners.put(instanceTag, listener);
            completeConfiguration(configTag, instanceTag);
        }
    }

    public static void removeListener(@Nullable String configTag, @Nullable String instanceTag){
        Log.v(LOG_TAG, "setListener entered.");
        if (configTag != null && instanceTag != null) {
            Map<String, ConfiguratorListener> listeners = mListenersMap.get(configTag);
            if (listeners != null && listeners.containsKey(instanceTag)){
                listeners.remove(instanceTag);
            }
        }
    }

    public static void setConfiguration(@NonNull String configTag, @NonNull Configuration config){
        Log.v(LOG_TAG, "setConfiguration entered.");
        mConfigurationMap.put(configTag, config);
        completeConfiguration(configTag);
    }

    private static void completeConfiguration(@NonNull String configTag){
        Log.v(LOG_TAG, "completeConfiguration(configTag) entered.");
        Configuration config = mConfigurationMap.get(configTag);
        if (config != null){
            Map<String, ConfiguratorListener> listeners = mListenersMap.get(configTag);
            if (listeners != null) {
                for (ConfiguratorListener listener : listeners.values()) {
                    listener.onConfigureCompleted(config.getHolder());
                }
            }
        }
    }

    private static void completeConfiguration(@NonNull String configTag, @NonNull String instanceTag){
        Log.v(LOG_TAG, "completeConfiguration(configTag, instanceTag) entered.");
        Configuration config = mConfigurationMap.get(configTag);
        if (config != null){
            Map<String, ConfiguratorListener> listeners = mListenersMap.get(configTag);
            if (listeners != null) {
                ConfiguratorListener listener = listeners.get(instanceTag);
                if (listener != null) {
                    listener.onConfigureCompleted(config.getHolder());
                }
            }
        }
    }
}
