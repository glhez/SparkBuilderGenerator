package com.helospark.spark.builder.preferences;

import java.util.HashMap;
import java.util.Map;

import com.helospark.spark.builder.handlers.StatefulBean;
import com.helospark.spark.builder.handlers.codegenerator.component.helper.PreferenceStoreProvider;

/**
 * Retrieve user changeable preferences.
 * <p>
 * Note: By default Eclipse's preferences menu is used, but a dialog can override the default preference.
 * This is a stateful class, dialog overrides can change, therefore it is not threadsafe.
 * 
 * @author maudrain
 */
public class PreferencesManager implements StatefulBean {
    private PreferenceStoreProvider preferenceStoreProvider;
    private Map<PluginPreference<?>, Object> dialogOverride;

    public PreferencesManager(PreferenceStoreProvider preferenceStoreProvider) {
        this.preferenceStoreProvider = preferenceStoreProvider;
        dialogOverride = new HashMap<>();
    }

    private <T> T getPreferenceValue(PluginPreference<T> preference) {
        if (dialogOverride.containsKey(preference)) {
            return (T) dialogOverride.get(preference);
        }
        return preference.getCurrentPreferenceValue(preferenceStoreProvider.providePreferenceStore());
    }

    public boolean getBooleanPreference(PluginPreference<Boolean> preference) {
        return Boolean.TRUE.equals(getPreferenceValue(preference));
    }

    public String getStringPreference(PluginPreference<String> preference) {
        return getPreferenceValue(preference);
    }

    public <E extends Enum<E>> E getEnumValue(PluginPreference<E> preference) {
        return getPreferenceValue(preference);
    }

    public <T> void addOverride(PluginPreference<T> preference, T value) {
        dialogOverride.put(preference, value);
    }

    @Override
    public void clearState() {
        dialogOverride.clear();
    }

}
