package net.md_5.ss.mapping;

public class CompactSearge extends MappingsFormat {

  public void load(MappingData data, String line) {
    String[] split = line.split(" ");

    switch (split.length) {
      case 2:
        String from = split[0];

        if (from.endsWith("/")) {
          data.addPackageMap(split[0], split[1]);
        } else {
          data.addClassMap(split[0], split[1]);
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
