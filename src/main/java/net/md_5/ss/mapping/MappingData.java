package net.md_5.ss.mapping;

import java.util.Map.Entry;
import java.util.Objects;
import net.md_5.ss.util.AccessTrackingMap;

public class MappingData {
  private final AccessTrackingMap<String, String> packageMaps = new AccessTrackingMap<>();
  private final AccessTrackingMap<String, String> classMaps = new AccessTrackingMap<>();
  private final AccessTrackingMap<String, String> methodMaps = new AccessTrackingMap<>();
  private final AccessTrackingMap<String, String> fieldMaps = new AccessTrackingMap<>();

  public void addPackageMap(String from, String to) {
    this.packageMaps.put(from, to);
  }

  public String getPackageMap(String from) {
    return this.packageMaps.get(from);
  }

  public void addClassMap(String from, String to) {
    this.classMaps.put(from, to);
  }

  public String getClassMap(String from) {
    return this.classMaps.get(from);
  }

  public void addMethodMap(String fromOwner, String fromName, String fromDesc, String newName) {
    this.methodMaps.put(fromOwner + ":" + fromName + " " + fromDesc, newName);
  }

  public String getMethodMap(String fromOwner, String fromName, String fromDesc) {
    return (String) this.methodMaps.get(fromOwner + ":" + fromName + " " + fromDesc);
  }

  public void addFieldMap(String fromOwner, String fromName, String newName) {
    this.fieldMaps.put(fromOwner + ":" + fromName, newName);
  }

  public String getFieldMap(String fromOwner, String fromName) {
    return this.fieldMaps.get(fromOwner + ":" + fromName);
  }

  public String printUnused() {
    StringBuilder sb = new StringBuilder();
    AccessTrackingMap<String, String>[] mappings = new AccessTrackingMap[]{this.packageMaps,
        this.classMaps, this.methodMaps,
        this.fieldMaps};

    for (AccessTrackingMap<String, String> section : mappings) {
      for (final Entry<String, String> entry : section.entrySet()) {
        if (!section.isSeen(entry.getValue())) {
          sb.append("Unseen: ")
              .append(entry.getKey())
              .append(" -> ")
              .append(entry.getValue())
              .append("\n");
        }
      }
    }

    return sb.toString();
  }

  public AccessTrackingMap<String, String> getPackageMaps() {
    return this.packageMaps;
  }

  public AccessTrackingMap<String, String> getClassMaps() {
    return this.classMaps;
  }

  public AccessTrackingMap<String, String> getMethodMaps() {
    return this.methodMaps;
  }

  public AccessTrackingMap<String, String> getFieldMaps() {
    return this.fieldMaps;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MappingData)) {
      return false;
    }
    final MappingData that = (MappingData) o;
    return Objects.equals(this.getPackageMaps(), that.getPackageMaps())
        && Objects.equals(this.getClassMaps(), that.getClassMaps())
        && Objects.equals(this.getMethodMaps(), that.getMethodMaps())
        && Objects.equals(this.getFieldMaps(), that.getFieldMaps());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.getPackageMaps(), this.getClassMaps(), this.getMethodMaps(), this.getFieldMaps());
  }

  @Override
  public String toString() {
    return "MappingData(packageMaps=" + this.getPackageMaps() + ", classMaps=" + this.getClassMaps() + ", methodMaps="
        + this.getMethodMaps() + ", fieldMaps=" + this.getFieldMaps() + ")";
  }
}
