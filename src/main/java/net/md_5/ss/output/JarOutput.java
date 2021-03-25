package net.md_5.ss.output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class JarOutput extends Output {
  private final JarOutputStream jar;

  public JarOutput(File file) throws IOException {
    super(file);
    this.jar = new JarOutputStream(new FileOutputStream(file));
  }

  public void write(String fileName, byte[] data) throws IOException {
    JarEntry entry = new JarEntry(fileName);

    entry.setTime(0L);
    entry.setSize((long) data.length);
    this.jar.putNextEntry(entry);
    this.jar.write(data);
    this.jar.closeEntry();
  }

  public void close() throws IOException {
    this.jar.close();
  }

  public JarOutputStream getJar() {
    return this.jar;
  }

  public String toString() {
    return "JarOutput(jar=" + this.getJar() + ")";
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (!(o instanceof JarOutput)) {
      return false;
    } else {
      JarOutput other = (JarOutput) o;

      if (!other.canEqual(this)) {
        return false;
      } else if (!super.equals(o)) {
        return false;
      } else {
        JarOutputStream this$jar = this.getJar();
        JarOutputStream other$jar = other.getJar();

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
    return other instanceof JarOutput;
  }

  public int hashCode() {
    boolean PRIME = true;
    int result = super.hashCode();
    JarOutputStream $jar = this.getJar();

    result = result * 59 + ($jar == null ? 43 : $jar.hashCode());
    return result;
  }
}
