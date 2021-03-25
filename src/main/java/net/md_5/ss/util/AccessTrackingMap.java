package net.md_5.ss.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AccessTrackingMap<K, V> implements Map<K, V> {
  private final Map<K, V> map;
  private final Set<V> seen = new HashSet<>();

  public AccessTrackingMap() {
    this(new HashMap<>());
  }

  public AccessTrackingMap(final Map<K, V> map) {
    this.map = map;
  }

  @Override
  public V get(Object key) {
    V val = this.map.get(key);

    if (val != null) {
      this.seen.add(val);
    }

    return val;
  }

  public boolean isSeen(V val) {
    return this.seen.contains(val);
  }

  @Override
  public int size() {
    return this.map.size();
  }

  @Override
  public boolean isEmpty() {
    return this.map.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return this.map.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return this.map.containsValue(value);
  }

  @Override
  public V put(K key, V value) {
    return this.map.put(key, value);
  }

  @Override
  public V remove(Object key) {
    return this.map.remove(key);
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> map) {
    this.map.putAll(map);
  }

  @Override
  public void clear() {
    this.map.clear();
  }

  @Override
  public Set<K> keySet() {
    return this.map.keySet();
  }

  @Override
  public Collection<V> values() {
    return this.map.values();
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return this.map.entrySet();
  }

  @Override
  public V getOrDefault(Object key, V defaultValue) {
    return this.map.getOrDefault(key, defaultValue);
  }

  @Override
  public void forEach(BiConsumer<? super K, ? super V> action) {
    this.map.forEach(action);
  }

  @Override
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
    this.map.replaceAll(function);
  }

  @Override
  public V putIfAbsent(K key, V value) {
    return this.map.putIfAbsent(key, value);
  }

  @Override
  public boolean remove(Object key, Object value) {
    return this.map.remove(key, value);
  }

  @Override
  public boolean replace(K key, V oldValue, V newValue) {
    return this.map.replace(key, oldValue, newValue);
  }

  @Override
  public V replace(K key, V value) {
    return this.map.replace(key, value);
  }

  @Override
  public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
    return this.map.computeIfAbsent(key, mappingFunction);
  }

  @Override
  public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    return this.map.computeIfPresent(key, remappingFunction);
  }

  @Override
  public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    return this.map.compute(key, remappingFunction);
  }

  @Override
  public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    return this.map.merge(key, value, remappingFunction);
  }
}
