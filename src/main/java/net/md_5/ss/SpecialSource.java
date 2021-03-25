package net.md_5.ss;

import static java.util.Arrays.asList;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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
    if (args.length == 0) {
      System.out.println("SpecialSource 2.0: (compare|map) [args]");
      return;
    }

    String[] taskArgs = Arrays.copyOfRange(args, 1, args.length);
    OptionParser help;

    // "map" and "compare" are legal...
    String command = args[0];

    if (!command.equals("map")) {
      // ... however "compare" is not implemented.
      throw new UnsupportedOperationException();
    }

    help = map(taskArgs);
    if (help != null) {
      help.printHelpOn(System.err);
    }
  }

  private static OptionParser map(String[] args) throws IOException {
    OptionParser parser = new OptionParser();
    OptionSpec<File> optionInputJar =
        parser.acceptsAll(asList("i", "in-jar"), "Input JAR").withRequiredArg().ofType(File.class);
    OptionSpec<File> optionOutputJar =
        parser.acceptsAll(asList("o", "out-jar"), "Output JAR").withRequiredArg().ofType(File.class);
    OptionSpec<File> optionsMappingsFile =
        parser.acceptsAll(asList("m", "mappings"), "Mappings")
            .withRequiredArg()
            .ofType(File.class)
            .withValuesSeparatedBy(',');
    OptionSpec<File> optionsExcludesFile =
        parser.acceptsAll(asList("e", "excludes"), "Excludes").withRequiredArg().ofType(File.class);
    OptionSpec<EnhancedMethodRemapper.LVTStyle> optionAutoLvt =
        parser.acceptsAll(asList("auto-lvt"), "Fix LVT automatically in specified style")
            .withRequiredArg()
            .ofType(EnhancedMethodRemapper.LVTStyle.class);
    OptionSpec<EnhancedRemapper.AutoMember> optionAutoMember =
        parser.acceptsAll(asList("auto-member"), "Automatically generate names for the given member types")
            .withRequiredArg()
            .ofType(EnhancedRemapper.AutoMember.class);
    OptionSpec<String> optionPackageWhitelist =
        parser.acceptsAll(asList("only"), "Map only the specified packages")
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
                out.write(remapper.map(c.getName()) + ".class", c.remap(remapper));
                break;
              case INCLUDE:
                out.write(c.getName() + ".class", c.toByteArray());
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

    return null;
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
