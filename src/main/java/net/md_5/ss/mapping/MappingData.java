package net.md_5.ss.mapping;

import java.util.Iterator;
import java.util.Map.Entry;
import net.md_5.ss.util.AccessTrackingMap;

public class MappingData {

    private final AccessTrackingMap packageMaps = new AccessTrackingMap();
    private final AccessTrackingMap classMaps = new AccessTrackingMap();
    private final AccessTrackingMap methodMaps = new AccessTrackingMap();
    private final AccessTrackingMap fieldMaps = new AccessTrackingMap();

    public void addPackageMap(String from, String to) {
        this.packageMaps.put(from, to);
    }

    public String getPackageMap(String from) {
        return (String) this.packageMaps.get(from);
    }

    public void addClassMap(String from, String to) {
        this.classMaps.put(from, to);
    }

    public String getClassMap(String from) {
        return (String) this.classMaps.get(from);
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
        return (String) this.fieldMaps.get(fromOwner + ":" + fromName);
    }

    public String printUnused() {
        StringBuilder sb = new StringBuilder();
        AccessTrackingMap[] aaccesstrackingmap = new AccessTrackingMap[]{this.packageMaps, this.classMaps, this.methodMaps, this.fieldMaps};
        int i = aaccesstrackingmap.length;

        for (int j = 0; j < i; ++j) {
            AccessTrackingMap section = aaccesstrackingmap[j];
            Iterator iterator = section.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();

                if (!section.isSeen(entry.getValue())) {
                    sb.append("Unseen: ").append((String) entry.getKey()).append(" -> ").append((String) entry.getValue()).append("\n");
                }
            }
        }

        return sb.toString();
    }

    public AccessTrackingMap getPackageMaps() {
        return this.packageMaps;
    }

    public AccessTrackingMap getClassMaps() {
        return this.classMaps;
    }

    public AccessTrackingMap getMethodMaps() {
        return this.methodMaps;
    }

    public AccessTrackingMap getFieldMaps() {
        return this.fieldMaps;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof MappingData)) {
            return false;
        } else {
            MappingData other = (MappingData) o;

            if (!other.canEqual(this)) {
                return false;
            } else {
                label59:
                {
                    AccessTrackingMap this$packageMaps = this.getPackageMaps();
                    AccessTrackingMap other$packageMaps = other.getPackageMaps();

                    if (this$packageMaps == null) {
                        if (other$packageMaps == null) {
                            break label59;
                        }
                    } else if (this$packageMaps.equals(other$packageMaps)) {
                        break label59;
                    }

                    return false;
                }

                label52:
                {
                    AccessTrackingMap this$classMaps = this.getClassMaps();
                    AccessTrackingMap other$classMaps = other.getClassMaps();

                    if (this$classMaps == null) {
                        if (other$classMaps == null) {
                            break label52;
                        }
                    } else if (this$classMaps.equals(other$classMaps)) {
                        break label52;
                    }

                    return false;
                }

                label45:
                {
                    AccessTrackingMap this$methodMaps = this.getMethodMaps();
                    AccessTrackingMap other$methodMaps = other.getMethodMaps();

                    if (this$methodMaps == null) {
                        if (other$methodMaps == null) {
                            break label45;
                        }
                    } else if (this$methodMaps.equals(other$methodMaps)) {
                        break label45;
                    }

                    return false;
                }

                AccessTrackingMap this$fieldMaps = this.getFieldMaps();
                AccessTrackingMap other$fieldMaps = other.getFieldMaps();

                if (this$fieldMaps == null) {
                    if (other$fieldMaps == null) {
                        return true;
                    }
                } else if (this$fieldMaps.equals(other$fieldMaps)) {
                    return true;
                }

                return false;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof MappingData;
    }

    public int hashCode() {
        AccessTrackingMap $packageMaps = this.getPackageMaps();
        int result = 59 + ($packageMaps == null ? 43 : $packageMaps.hashCode());
        AccessTrackingMap $classMaps = this.getClassMaps();

        result = result * 59 + ($classMaps == null ? 43 : $classMaps.hashCode());
        AccessTrackingMap $methodMaps = this.getMethodMaps();

        result = result * 59 + ($methodMaps == null ? 43 : $methodMaps.hashCode());
        AccessTrackingMap $fieldMaps = this.getFieldMaps();

        result = result * 59 + ($fieldMaps == null ? 43 : $fieldMaps.hashCode());
        return result;
    }

    public String toString() {
        return "MappingData(packageMaps=" + this.getPackageMaps() + ", classMaps=" + this.getClassMaps() + ", methodMaps=" + this.getMethodMaps() + ", fieldMaps=" + this.getFieldMaps() + ")";
    }
}
