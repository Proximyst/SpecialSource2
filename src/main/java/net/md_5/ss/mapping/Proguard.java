package net.md_5.ss.mapping;

public class Proguard extends MappingsFormat {

    protected void load(MappingData data, String line) {
        String[] split = line.split(" -> ");
        int semi = line.indexOf(58);

        if (semi == line.length()) {
            data.addClassMap(split[1].substring(0, split[1].length()), split[0].replace('.', '/'));
        }

    }
}
