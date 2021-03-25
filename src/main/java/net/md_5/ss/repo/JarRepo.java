package net.md_5.ss.repo;

import com.google.common.collect.Iterators;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import net.md_5.ss.model.ClassInfo;
import net.md_5.ss.model.ItemInfo;
import net.md_5.ss.model.Resource;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

public class JarRepo extends ClassRepo {
  private final JarFile jar;

  protected ClassInfo getClass0(String internalName) throws IOException {
    JarEntry entry = this.jar.getJarEntry(internalName + ".class");
    if (entry == null) {
      return null;
    }

    ClassInfo classinfo;
    try (InputStream is = this.jar.getInputStream(entry)) {
      ClassReader cr = new ClassReader(is);
      ClassNode node = new ClassNode();

      cr.accept(node, 0);
      classinfo = new ClassInfo(this, cr, node);
    }

    return classinfo;
  }

  @Override
  public Iterator<ItemInfo> iterator() {
    final Enumeration<JarEntry> entries = this.jar.entries();

    return Iterators.transform(entries.asIterator(), entry -> {
      String name = entry.getName();

      if (name.endsWith(".class")) {
        String internalName = name.substring(0, name.length() - ".class".length());

        return JarRepo.this.getClass(internalName);
      }

      byte[] data;
      try (InputStream is = this.jar.getInputStream(entry)) {
        data = ByteStreams.toByteArray(is);
      } catch (IOException ioexception) {
        throw new RuntimeException(ioexception);
      }

      return new Resource(name, data);
    });
  }

  public JarRepo(JarFile jar) {
    this.jar = jar;
  }

  public JarFile getJar() {
    return this.jar;
  }

  public String toString() {
    return "JarRepo(jar=" + this.getJar() + ")";
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (!(o instanceof JarRepo)) {
      return false;
    } else {
      JarRepo other = (JarRepo) o;

      if (!other.canEqual(this)) {
        return false;
      } else {
        JarFile this$jar = this.getJar();
        JarFile other$jar = other.getJar();

        if (this$jar == null) {
          if (other$jar == null) {
            return true;
          }
        } else if (this$jar.equals(other$jar)) {
          return true;
        }

        return false;
      }
    }
  }

  protected boolean canEqual(Object other) {
    return other instanceof JarRepo;
  }

  public int hashCode() {
    JarFile $jar = this.getJar();
    int result = 59 + ($jar == null ? 43 : $jar.hashCode());

    return result;
  }
}
