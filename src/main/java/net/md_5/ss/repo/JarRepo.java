package net.md_5.ss.repo;

import com.google.common.collect.AbstractIterator;
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

    if (entry != null) {
      InputStream is = this.jar.getInputStream(entry);

      ClassInfo classinfo;

      try {
        ClassReader cr = new ClassReader(is);
        ClassNode node = new ClassNode();

        cr.accept(node, 0);
        classinfo = new ClassInfo(this, cr, node);
      } catch (Throwable throwable) {
        if (is != null) {
          try {
            is.close();
          } catch (Throwable throwable1) {
            Throwable throwable2 = throwable;
            Throwable throwable3 = throwable1;

            try {
              throwable2.addSuppressed(throwable3);
            } catch (NoSuchMethodError nosuchmethoderror) {
              ;
            }
          }
        }

        throw throwable;
      }

      if (is != null) {
        is.close();
      }

      return classinfo;
    } else {
      return null;
    }
  }

  public Iterator iterator() {
    final Enumeration entries = this.jar.entries();

    return new AbstractIterator() {
      protected ItemInfo computeNext() {
        if (!entries.hasMoreElements()) {
          return (ItemInfo) this.endOfData();
        } else {
          JarEntry entry = (JarEntry) entries.nextElement();
          String name = entry.getName();

          if (name.endsWith(".class")) {
            String internalName = name.substring(0, name.length() - ".class".length());

            return JarRepo.this.getClass(internalName);
          } else {
            byte[] data;

            try {
              InputStream is = JarRepo.this.jar.getInputStream(entry);

              try {
                data = ByteStreams.toByteArray(is);
              } catch (Throwable throwable) {
                if (is != null) {
                  try {
                    is.close();
                  } catch (Throwable throwable1) {
                    Throwable throwable2 = throwable;
                    Throwable throwable3 = throwable1;

                    try {
                      throwable2.addSuppressed(throwable3);
                    } catch (NoSuchMethodError nosuchmethoderror) {
                      ;
                    }
                  }
                }

                throw throwable;
              }

              if (is != null) {
                is.close();
              }
            } catch (IOException ioexception) {
              throw new RuntimeException(ioexception);
            }

            return new Resource(entry.getName(), data);
          }
        }
      }
    };
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
