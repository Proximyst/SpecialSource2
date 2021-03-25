package net.md_5.ss.model;

public class FieldInfo extends GenericInfo {
  public FieldInfo(ClassInfo owner, String name, String desc, int access) {
    super(owner, name, desc, access);
  }

  @Override
  public String toString() {
    return "FieldInfo()";
  }

  @Override
  public boolean equals(final Object o) {
    return o instanceof FieldInfo && super.equals(o);
  }

  @Override
  public int hashCode() {
    return 43 * super.hashCode() + 7;
  }
}
