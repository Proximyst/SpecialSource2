package net.md_5.ss.model;

import java.util.Arrays;
import java.util.Objects;

public class Resource implements ItemInfo {

  private final String path;
  private final byte[] data;

  public Resource(String path, byte[] data) {
    this.path = path;
    this.data = data;
  }

  public String getPath() {
    return this.path;
  }

  public byte[] getData() {
    return this.data;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Resource resource = (Resource) o;
    return Objects.equals(getPath(), resource.getPath())
        && Arrays.equals(getData(), resource.getData());
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(getPath());
    result = 31 * result + Arrays.hashCode(getData());
    return result;
  }

  public String toString() {
    return "Resource(path=" + this.getPath() + ", data=" + Arrays.toString(this.getData()) + ")";
  }
}
