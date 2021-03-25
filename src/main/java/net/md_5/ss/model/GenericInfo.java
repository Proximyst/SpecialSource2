package net.md_5.ss.model;

import java.util.Objects;

public class GenericInfo {
  private final ClassInfo owner;
  private final String name;
  private final String desc;
  private final int access;

  public GenericInfo(ClassInfo owner, String name, String desc, int access) {
    this.owner = owner;
    this.name = name;
    this.desc = desc;
    this.access = access;
  }

  public ClassInfo getOwner() {
    return this.owner;
  }

  public String getName() {
    return this.name;
  }

  public String getDesc() {
    return this.desc;
  }

  public int getAccess() {
    return this.access;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final GenericInfo that = (GenericInfo) o;
    return getAccess() == that.getAccess()
        && Objects.equals(getOwner(), that.getOwner())
        && Objects.equals(getName(), that.getName())
        && Objects.equals(getDesc(), that.getDesc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getOwner(), getName(), getDesc(), getAccess());
  }

  public String toString() {
    return "GenericInfo(owner=" + this.getOwner() + ", name=" + this.getName() + ", desc=" + this.getDesc()
        + ", access=" + this.getAccess() + ")";
  }
}
