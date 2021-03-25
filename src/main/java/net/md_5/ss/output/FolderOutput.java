package net.md_5.ss.output;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;

public class FolderOutput extends Output {
  public FolderOutput(File file) {
    super(file);
  }

  @Override
  public void write(String fileName, byte[] data) throws IOException {
    File outFile = new File(this.getFile(), fileName);

    outFile.getParentFile().mkdirs();
    Files.write(data, outFile);
  }

  @Override
  public void close() {
  }

  @Override
  public String toString() {
    return "FolderOutput()";
  }

  @Override
  public boolean equals(final Object o) {
    return o instanceof FolderOutput && super.equals(o);
  }

  @Override
  public int hashCode() {
    return 31 * super.hashCode() + 7;
  }
}
