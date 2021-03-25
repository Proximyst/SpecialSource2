package net.md_5.ss.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AccessTrackingMap implements Map {

    private final Map map = new HashMap();
    private final Set seen = new HashSet();

    public Object get(Object key) {
        Object val = this.map.get(key);

        if (val != null) {
            this.seen.add(val);
        }

        return val;
    }

    public boolean isSeen(Object val) {
        return this.seen.contains(val);
    }

    public int size() {
        return this.map.size();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public boolean containsKey(Object arg0) {
        return this.map.containsKey(arg0);
    }

    public boolean containsValue(Object arg0) {
        return this.map.containsValue(arg0);
    }

    public Object put(Object arg0, Object arg1) {
        return this.map.put(arg0, arg1);
    }

    public Object remove(Object arg0) {
        return this.map.remove(arg0);
    }

    public void putAll(Map arg0) {
        this.map.putAll(arg0);
    }

    public void clear() {
        this.map.clear();
    }

    public Set keySet() {
        return this.map.keySet();
    }

    public Collection values() {
        return this.map.values();
    }

    public Set entrySet() {
        return this.map.entrySet();
    }

    public Object getOrDefault(Object key, Object defaultValue) {
        return this.map.getOrDefault(key, defaultValue);
    }

    public void forEach(BiConsumer action) {
        this.map.forEach(action);
    }

    public void replaceAll(BiFunction function) {
        this.map.replaceAll(function);
    }

    public Object putIfAbsent(Object key, Object value) {
        return this.map.putIfAbsent(key, value);
    }

    public boolean remove(Object key, Object value) {
        return this.map.remove(key, value);
    }

    public boolean replace(Object key, Object oldValue, Object newValue) {
        return this.map.replace(key, oldValue, newValue);
    }

    public Object replace(Object key, Object value) {
        return this.map.replace(key, value);
    }

    public Object computeIfAbsent(Object key, Function mappingFunction) {
        return this.map.computeIfAbsent(key, mappingFunction);
    }

    public Object computeIfPresent(Object key, BiFunction remappingFunction) {
        return this.map.computeIfPresent(key, remappingFunction);
    }

    public Object compute(Object key, BiFunction remappingFunction) {
        return this.map.compute(key, remappingFunction);
    }

    public Object merge(Object key, Object value, BiFunction remappingFunction) {
        return this.map.merge(key, value, remappingFunction);
    }

    private interface Exclude {

        Object get(Object object);
    }
}
