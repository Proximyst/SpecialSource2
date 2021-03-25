package net.md_5.ss;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.JarFile;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
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
import net.md_5.ss.repo.ClassRepo;
import net.md_5.ss.repo.JarRepo;
import net.md_5.ss.repo.RuntimeRepo;

public class SpecialSource {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("SpecialSource 2.0: (compare|map) [args]");
        } else {
            String[] taskArgs = (String[]) Arrays.copyOfRange(args, 1, args.length);
            OptionParser help = null;
            String s = args[0];
            byte b0 = -1;

            switch (s.hashCode()) {
                case 107868:
                    if (s.equals("map")) {
                        b0 = 1;
                    }
                    break;
                case 950484197:
                    if (s.equals("compare")) {
                        b0 = 0;
                    }
            }

            switch (b0) {
                case 0:
                    throw new UnsupportedOperationException();
                case 1:
                    help = map(taskArgs);
                    if (help != null) {
                        help.printHelpOn((OutputStream) System.err);
                    }

                    return;
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }

    private static OptionParser map(String[] args) throws IOException {
        OptionParser parser = new OptionParser() {
            {
                this.acceptsAll(Arrays.asList("i", "in-jar"), "Input JAR").withRequiredArg().ofType(File.class);
                this.acceptsAll(Arrays.asList("o", "out-jar"), "Output JAR").withRequiredArg().ofType(File.class);
                this.acceptsAll(Arrays.asList("m", "mappings"), "Mappings").withRequiredArg().ofType(File.class).withValuesSeparatedBy(',');
                this.acceptsAll(Arrays.asList("e", "excludes"), "Excludes").withRequiredArg().ofType(File.class);
                this.acceptsAll(Arrays.asList("auto-lvt"), "Fix LVT automatically in the specified style").withRequiredArg().ofType(EnhancedMethodRemapper.LVTStyle.class);
                this.acceptsAll(Arrays.asList("auto-member"), "Automatically generate names for the given member types").withRequiredArg().ofType(EnhancedRemapper.AutoMember.class);
                this.acceptsAll(Arrays.asList("only"), "Map only the specified packages").withRequiredArg().ofType(String.class);
            }
        };
        OptionSet set = parser.parse(args);
        File inJar = (File) set.valueOf("in-jar");
        AggregateRepo inRepo = new AggregateRepo(new ClassRepo[]{new JarRepo(new JarFile(inJar)), RuntimeRepo.getInstance()});
        JarOutput out = new JarOutput((File) set.valueOf("out-jar"));
        Iterator iterator = set.valuesOf("mappings").iterator();

        while (iterator.hasNext()) {
            File file = (File) iterator.next();
            MappingData mappingData = new MappingData();
            String s = extension(file);
            byte b0 = -1;

            switch (s.hashCode()) {
                case 111269:
                    if (s.equals("prg")) {
                        b0 = 1;
                    }
                    break;
                case 3063461:
                    if (s.equals("csrg")) {
                        b0 = 0;
                    }
            }

            switch (b0) {
                case 0:
                    (new CompactSearge()).load(file, mappingData);
                    break;
                case 1:
                    (new Proguard()).load(file, mappingData);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown mappings format");
            }

            EnhancedRemapper remapper = new EnhancedRemapper(inRepo, mappingData, set.valuesOf("auto-member"), (EnhancedMethodRemapper.LVTStyle) set.valueOf("auto-lvt"));
            HashSet includes = new HashSet(set.valuesOf("only"));
            HashSet excludes = new HashSet();

            if (set.has("excludes")) {
                excludes.addAll(Files.readLines((File) set.valueOf("excludes"), Charsets.UTF_8));
            }

            Iterator iterator1;
            ItemInfo item;

            if (remapper.getAutoMember().contains(EnhancedRemapper.AutoMember.SYNTHETIC)) {
                iterator1 = inRepo.iterator();

                while (iterator1.hasNext()) {
                    item = (ItemInfo) iterator1.next();
                    if (shouldHandle(item, includes, excludes) == SpecialSource.HandleType.REMAP && item instanceof ClassInfo) {
                        SyntheticFinder.addSynthetics(((ClassInfo) item).getNode(), remapper, mappingData);
                    }
                }
            }

            iterator1 = inRepo.iterator();

            while (iterator1.hasNext()) {
                item = (ItemInfo) iterator1.next();
                SpecialSource.HandleType handle = shouldHandle(item, includes, excludes);

                if (item instanceof ClassInfo) {
                    ClassInfo c = (ClassInfo) item;

                    switch (handle) {
                        case REMAP:
                            out.write(remapper.map(c.getName()) + ".class", c.remap(remapper));
                            break;
                        case INCLUDE:
                            out.write(c.getName() + ".class", c.toByteArray());
                        case EXCLUDE:
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

        out.close();
        return null;
    }

    private static SpecialSource.HandleType shouldHandle(ItemInfo item, Set includes, Set excludes) {
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

        if (includes.isEmpty()) {
            return SpecialSource.HandleType.REMAP;
        } else {
            Iterator iterator = includes.iterator();

            String match;

            do {
                if (!iterator.hasNext()) {
                    return SpecialSource.HandleType.INCLUDE;
                }

                match = (String) iterator.next();
                if (match.equals(".") && !thing.contains("/")) {
                    return SpecialSource.HandleType.REMAP;
                }
            } while (!thing.startsWith(match));

            return SpecialSource.HandleType.REMAP;
        }
    }

    private static String extension(File file) {
        String name = file.getName();
        int dotIndex = name.lastIndexOf(46);

        return dotIndex == -1 ? name : name.substring(dotIndex + 1);
    }

    private static enum HandleType {

        REMAP, INCLUDE, EXCLUDE;
    }
}
