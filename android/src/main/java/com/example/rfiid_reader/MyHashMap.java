package com.example.rfiid_reader;
import java.util.HashMap;
import java.util.Map;

class MyHashMap<K, V> extends HashMap<K, V> {
    private final Map<OnHashMapUpdatedListener<K, V>, Object> listeners = new HashMap<>();

    @Override
    public V put(K key, V value) {
        V result = super.put(key, value);
        notifyListeners();
        return result;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        super.putAll(m);
        notifyListeners();
    }

    @Override
    public V remove(Object key) {
        V result = super.remove(key);
        notifyListeners();
        return result;
    }

    public void addListener(OnHashMapUpdatedListener<K, V> listener) {
        listeners.put(listener, new Object());
    }

    public void removeListener(OnHashMapUpdatedListener<K, V> listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (OnHashMapUpdatedListener<K, V> listener : listeners.keySet()) {
            listener.onHashMapUpdated(this);
        }
    }

    public interface OnHashMapUpdatedListener<K, V> {
        void onHashMapUpdated(MyHashMap<K, V> hashMap);
    }
}
