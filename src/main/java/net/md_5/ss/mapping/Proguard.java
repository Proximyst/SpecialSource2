package net.md_5.ss.mapping;

public class Proguard extends MappingsFormat {
  protected void load(MappingData data, String line) {
    String[] split = line.split(" -> ");
    int semi = line.indexOf(':');

    if (semi == line.length()) {
      data.addClassMap(split[1], split[0].replace('.', '/'));
    }
  }
}
