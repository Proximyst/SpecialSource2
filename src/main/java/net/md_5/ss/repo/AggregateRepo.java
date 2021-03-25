package net.md_5.ss.repo;

import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.Iterator;
import net.md_5.ss.model.ClassInfo;

public class AggregateRepo extends ClassRepo {

  private final ClassRepo[] children;

  public AggregateRepo(ClassRepo... children) {
    this.children = children;
  }

  public ClassInfo getClass0(String internalName) {
    ClassRepo[] aclassrepo = this.children;
    int i = aclassrepo.length;

    for (int j = 0; j < i; ++j) {
      ClassRepo child = aclassrepo[j];
      ClassInfo found = child.getClass(internalName);

      if (found != null) {
        found.setRepo(this);
        return found;
      }
    }

    return null;
  }

  public Iterator iterator() {
    return Iterables.concat((Iterable[]) this.children).iterator();
  }

  public ClassRepo[] getChildren() {
    return this.children;
  }

  public String toString() {
    return "AggregateRepo(children=" + Arrays.deepToString(this.getChildren()) + ")";
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (!(o instanceof AggregateRepo)) {
      return false;
    } else {
      AggregateRepo other = (AggregateRepo) o;

      return !other.canEqual(this) ? false : Arrays.deepEquals(this.getChildren(), other.getChildren());
    }
  }

  protected boolean canEqual(Object other) {
    return other instanceof AggregateRepo;
  }

  public int hashCode() {
    int result = 59 + Arrays.deepHashCode(this.getChildren());

    return result;
  }
}
