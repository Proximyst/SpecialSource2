package net.md_5.ss.repo;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.md_5.ss.model.ClassInfo;
import net.md_5.ss.model.ItemInfo;

public class AggregateRepo extends ClassRepo {
  private final List<ClassRepo> children;

  public AggregateRepo(ClassRepo... children) {
    this.children = ImmutableList.copyOf(children);
  }

  @Override
  public ClassInfo getClass0(String internalName) {
    for (ClassRepo child : this.children) {
      ClassInfo found = child.getClass(internalName);

      if (found != null) {
        found.setRepo(this);
        return found;
      }
    }

    return null;
  }

  @Override
  public Iterator<ItemInfo> iterator() {
    return Iterables.concat(this.children).iterator();
  }

  @Override
  public String toString() {
    return "AggregateRepo(children=" + this.children + ")";
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final AggregateRepo that = (AggregateRepo) o;
    return Objects.equals(children, that.children);
  }

  @Override
  public int hashCode() {
    return Objects.hash(children);
  }
}
