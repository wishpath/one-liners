package org.sa.z_data_structure;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ValueAscendingMap<K, V extends Comparable<? super V>> extends LinkedHashMap<K, V> {
  @Override
  public V put(K key, V value) {
    V oldValue = super.put(key, value);
    valueSort();
    return oldValue;
  }

  @Override
  public V remove(Object key) {
    V removedValue = super.remove(key);
    valueSort();
    return removedValue;
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    super.putAll(m);
    valueSort();
  }

  @Override
  public void clear() {
    super.clear();
  }

  @Override
  public V putIfAbsent(K key, V value) {
    V result = super.putIfAbsent(key, value);
    valueSort();
    return result;
  }

  @Override
  public boolean remove(Object key, Object value) {
    boolean result = super.remove(key, value);
    valueSort();
    return result;
  }

  @Override
  public boolean replace(K key, V oldValue, V newValue) {
    boolean result = super.replace(key, oldValue, newValue);
    valueSort();
    return result;
  }

  @Override
  public V replace(K key, V value) {
    V result = super.replace(key, value);
    valueSort();
    return result;
  }

  @Override
  public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
    V result = super.computeIfAbsent(key, mappingFunction);
    valueSort();
    return result;
  }

  @Override
  public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    V result = super.computeIfPresent(key, remappingFunction);
    valueSort();
    return result;
  }

  @Override
  public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    V result = super.compute(key, remappingFunction);
    valueSort();
    return result;
  }

  @Override
  public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    V result = super.merge(key, value, remappingFunction);
    valueSort();
    return result;
  }

  private void valueSort() {
    List<Map.Entry<K, V>> sortedEntries = entrySet().stream()
        .sorted(Map.Entry.comparingByValue())
        .toList();
    super.clear();
    for (Map.Entry<K, V> entry : sortedEntries) super.put(entry.getKey(), entry.getValue());
  }
}


