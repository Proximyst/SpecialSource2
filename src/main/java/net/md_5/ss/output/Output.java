package net.md_5.ss.output;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public abstract class Output implements Closeable, AutoCloseable {
  private final File file;

  public abstract void write(String fileName, byte[] data) throws IOException;

  protected Output(File file) {
    this.file = file;
  }

  public File getFile() {
    return this.file;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Output output = (Output) o;
    return Objects.equals(getFile(), output.getFile());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getFile());
  }

  @Override
  public String toString() {
    return "Output(file=" + this.getFile() + ")";
  }
}
