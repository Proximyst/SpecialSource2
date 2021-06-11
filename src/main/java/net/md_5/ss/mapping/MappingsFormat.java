package net.md_5.ss.mapping;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public abstract class MappingsFormat {

  protected abstract void load(MappingData mappingdata, String line);

  protected void loadStep2(MappingData mappingData, String line) {
  }

  public final void load(File file, MappingData data) throws IOException {
    final List<String> mappingLines = new LinkedList<>();

    for (String line : Files.readLines(file, StandardCharsets.UTF_8)) {
      line = line.trim();
      int commentIndex = line.indexOf('#');

      if (commentIndex != -1) {
        line = line.substring(0, commentIndex);
      }

      if (!line.isEmpty()) {
        mappingLines.add(line);
      }
    }

    for (final String line : mappingLines) {
      this.load(data, line);
    }

    for (final String line : mappingLines) {
      this.loadStep2(data, line);
    }
  }
}
