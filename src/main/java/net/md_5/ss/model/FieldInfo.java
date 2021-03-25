package net.md_5.ss.model;

public class FieldInfo extends GenericInfo {

  public FieldInfo(ClassInfo owner, String name, String desc, int access) {
    super(owner, name, desc, access);
  }

  public String toString() {
    return "FieldInfo()";
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (!(o instanceof FieldInfo)) {
      return false;
    } else {
      FieldInfo other = (FieldInfo) o;

      return !other.canEqual(this) ? false : super.equals(o);
    }
  }

  protected boolean canEqual(Object other) {
    return other instanceof FieldInfo;
  }

  public int hashCode() {
    int result = super.hashCode();

    return result;
  }
}
