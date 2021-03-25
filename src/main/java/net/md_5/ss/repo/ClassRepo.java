package net.md_5.ss.repo;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.io.IOException;
import net.md_5.ss.model.ClassInfo;

public abstract class ClassRepo implements Iterable {

  private final Cache cache = CacheBuilder.newBuilder().maximumSize(4096L).build();

  public final synchronized ClassInfo getClass(String internalName) {
    ClassInfo info = (ClassInfo) this.cache.getIfPresent(internalName);

    if (info == null) {
      try {
        info = this.getClass0(internalName);
        if (info != null) {
          this.cache.put(internalName, info);
        }
      } catch (IOException ioexception) {
        ;
      }
    }

    return info;
  }

  protected abstract ClassInfo getClass0(String s) throws IOException;
}
