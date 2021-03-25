package net.md_5.ss.mapping;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public abstract class MappingsFormat {

  protected abstract void load(MappingData mappingdata, String s);

  public final void load(File file, MappingData data) throws IOException {
    Iterator iterator = Files.readLines(file, Charsets.UTF_8).iterator();

    while (iterator.hasNext()) {
      String line = (String) iterator.next();

      line = line.trim();
      int commentIndex = line.indexOf(35);

      if (commentIndex != -1) {
        line = line.substring(0, commentIndex);
      }

      if (!line.isEmpty()) {
        this.load(data, line);
      }
    }

  }
}
