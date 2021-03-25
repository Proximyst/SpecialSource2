package net.md_5.ss.mapping;

import java.util.regex.Pattern;

public class CompactSearge extends MappingsFormat {
  private static final Pattern WHITESPACE = Pattern.compile("\\s+");

  public void load(MappingData data, String line) {
    String[] split = WHITESPACE.split(line);

    switch (split.length) {
      case 2:
        String from = split[0];

        if (from.endsWith("/")) {
          data.addPackageMap(from, split[1]);
        } else {
          data.addClassMap(from, split[1]);
        }
        break;
      case 3:
        data.addFieldMap(split[0], split[1], split[2]);
        break;
      case 4:
        data.addMethodMap(split[0], split[1], split[2], split[3]);
        break;
      default:
        throw new IllegalArgumentException("Unknown line: " + line);
    }
  }
}
