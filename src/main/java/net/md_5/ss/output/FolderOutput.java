package net.md_5.ss.output;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;

public class FolderOutput extends Output {

  public FolderOutput(File file) {
    super(file);
  }

  public void write(String fileName, byte[] data) throws IOException {
    File outFile = new File(this.getFile(), fileName);

    outFile.getParentFile().mkdirs();
    Files.write(data, outFile);
  }

  public void close() throws IOException {
  }

  public String toString() {
    return "FolderOutput()";
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (!(o instanceof FolderOutput)) {
      return false;
    } else {
      FolderOutput other = (FolderOutput) o;

      return !other.canEqual(this) ? false : super.equals(o);
    }
  }

  protected boolean canEqual(Object other) {
    return other instanceof FolderOutput;
  }

  public int hashCode() {
    int result = super.hashCode();

    return result;
  }
}
