package net.md_5.ss;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.JarFile;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.md_5.ss.mapping.CompactSearge;
import net.md_5.ss.mapping.MappingData;
import net.md_5.ss.mapping.Proguard;
import net.md_5.ss.model.ClassInfo;
import net.md_5.ss.model.ItemInfo;
import net.md_5.ss.model.Resource;
import net.md_5.ss.output.JarOutput;
import net.md_5.ss.remapper.EnhancedMethodRemapper;
import net.md_5.ss.remapper.EnhancedRemapper;
import net.md_5.ss.repo.AggregateRepo;
import net.md_5.ss.repo.JarRepo;
import net.md_5.ss.repo.RuntimeRepo;

public class SpecialSource {
  public static void main(String[] args) throws IOException {
    if (args.length == 0 || !args[0].equals("map")) {
      System.out.println("SpecialSource 2.0: map [args]");
      return;
    }

    map(Arrays.copyOfRange(args, 1, args.length)); // map _always_ returns null...
  }

  private static void map(String[] args) throws IOException {
    final OptionParser parser = new OptionParser();
    final OptionSpec<File> optionInputJar =
        parser.acceptsAll(ImmutableList.of("i", "in-jar"), "Input JAR").withRequiredArg().ofType(File.class);
    final OptionSpec<File> optionOutputJar =
        parser.acceptsAll(ImmutableList.of("o", "out-jar"), "Output JAR").withRequiredArg().ofType(File.class);
    final OptionSpec<File> optionsMappingsFile =
        parser.acceptsAll(ImmutableList.of("m", "mappings"), "Mappings")
            .withRequiredArg()
            .ofType(File.class)
            .withValuesSeparatedBy(',');
    final OptionSpec<File> optionsExcludesFile =
        parser.acceptsAll(ImmutableList.of("e", "excludes"), "Excludes").withRequiredArg().ofType(File.class);
    final OptionSpec<EnhancedMethodRemapper.LVTStyle> optionAutoLvt =
        parser.acceptsAll(Collections.singletonList("auto-lvt"), "Fix LVT automatically in specified style")
            .withRequiredArg()
            .ofType(EnhancedMethodRemapper.LVTStyle.class);
    final OptionSpec<EnhancedRemapper.AutoMember> optionAutoMember =
        parser.acceptsAll(Collections.singletonList("auto-member"),
            "Automatically generate names for the given member types")
            .withRequiredArg()
            .ofType(EnhancedRemapper.AutoMember.class);
    final OptionSpec<String> optionPackageWhitelist =
        parser.acceptsAll(Collections.singletonList("only"), "Map only the specified packages")
            .withRequiredArg()
            .ofType(String.class);

    OptionSet set = parser.parse(args);
    File inJar = set.valueOf(optionInputJar);
    AggregateRepo inRepo = new AggregateRepo(new JarRepo(new JarFile(inJar)), RuntimeRepo.getInstance());
    try (JarOutput out = new JarOutput(set.valueOf(optionOutputJar))) {

      for (final File file : set.valuesOf(optionsMappingsFile)) {
        MappingData mappingData = new MappingData();
        String extension = extension(file);

        switch (extension) {
          case "prg":
          case "txt":
            new Proguard().load(file, mappingData);
            break;
          case "csrg":
            new CompactSearge().load(file, mappingData);
            break;
          default:
            throw new IllegalArgumentException("Unknown mappings format");
        }

        EnhancedRemapper remapper = new EnhancedRemapper(inRepo, mappingData, set.valuesOf(optionAutoMember),
            set.valueOf(optionAutoLvt));
        Set<String> includes = new HashSet<>(set.valuesOf(optionPackageWhitelist));
        Set<String> excludes = new HashSet<>();

        if (set.has(optionsExcludesFile)) {
          excludes.addAll(Files.readLines(set.valueOf(optionsExcludesFile), StandardCharsets.UTF_8));
        }

        Iterator<ItemInfo> iterator1;
        ItemInfo item;

        if (remapper.getAutoMember().contains(EnhancedRemapper.AutoMember.SYNTHETIC)) {
          iterator1 = inRepo.iterator();

          while (iterator1.hasNext()) {
            item = iterator1.next();
            if (shouldHandle(item, includes, excludes) == HandleType.REMAP && item instanceof ClassInfo) {
              SyntheticFinder.addSynthetics(((ClassInfo) item).getNode(), remapper, mappingData);
            }
          }
        }

        iterator1 = inRepo.iterator();

        while (iterator1.hasNext()) {
          item = iterator1.next();
          HandleType handle = shouldHandle(item, includes, excludes);

          if (item instanceof ClassInfo) {
            ClassInfo c = (ClassInfo) item;

            switch (handle) {
              case REMAP:
                out.write(c.getVersionBase() + remapper.map(c.getName()) + ".class", c.remap(remapper));
                break;
              case INCLUDE:
                out.write(c.getVersionBase() + c.getName() + ".class", c.toByteArray());
                break;
              case EXCLUDE:
                // No-op
                break;
              default:
                throw new IllegalArgumentException("Unknown handle status");
            }
          } else if (item instanceof Resource) {
            Resource r = (Resource) item;

            out.write(r.getPath(), r.getData());
          }
        }

        System.out.println(mappingData.printUnused());
      }
    }
  }

  private static SpecialSource.HandleType shouldHandle(ItemInfo item, Set<String> includes, Set<String> excludes) {
    String thing;

    if (item instanceof ClassInfo) {
      thing = ((ClassInfo) item).getName();
    } else {
      if (!(item instanceof Resource)) {
        return SpecialSource.HandleType.REMAP;
      }

      thing = ((Resource) item).getPath();
    }

    if (!excludes.isEmpty()) {
      String excludeName = thing;
      int dollar = thing.indexOf(36);

      if (dollar != -1) {
        excludeName = thing.substring(0, dollar);
      }

      if (excludes.contains(excludeName)) {
        return SpecialSource.HandleType.EXCLUDE;
      }
    }

    if (!includes.isEmpty()) {
      Iterator<String> iterator = includes.iterator();

      String match;

      do {
        if (!iterator.hasNext()) {
          return HandleType.INCLUDE;
        }

        match = iterator.next();
        if (match.equals(".") && !thing.contains("/")) {
          return HandleType.REMAP;
        }
      } while (!thing.startsWith(match));
    }

    return HandleType.REMAP;
  }

  private static String extension(File file) {
    String name = file.getName();
    int dotIndex = name.lastIndexOf('.');

    return dotIndex == -1 ? name : name.substring(dotIndex + 1);
  }

  private enum HandleType {
    REMAP,
    INCLUDE,
    EXCLUDE,
  }
}
