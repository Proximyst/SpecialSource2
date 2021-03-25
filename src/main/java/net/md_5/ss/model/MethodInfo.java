package net.md_5.ss.model;

public class MethodInfo extends GenericInfo {
  public MethodInfo(ClassInfo owner, String name, String desc, int access) {
    super(owner, name, desc, access);
  }

  @Override
  public String toString() {
    return "MethodInfo()";
  }

  @Override
  public boolean equals(final Object o) {
    return o instanceof MethodInfo && super.equals(o);
  }

  @Override
  public int hashCode() {
    return 53 * super.hashCode() + 7;
  }
}
