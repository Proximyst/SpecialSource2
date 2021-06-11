package net.md_5.ss.mapping;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Proguard extends MappingsFormat {
  private static final Pattern MEMBER_PATTERN = Pattern.compile("(?:\\d+:\\d+:)?(.*?) (.*?) \\-> (.*)");
  private String lastClass;

  @Override
  protected void load(MappingData data, String line) {
    this.load(data, line, false);
  }

  protected void load(MappingData data, String line, boolean addMethods) {
    if (line.endsWith(":")) {
      // This is a class
      final String[] split = line.split(" -> ");
      final String nameDesc = split[0].replace('.', '/');
      final String signature = split[1].substring(0, split[1].length() - 1).replace('.', '/');
      this.lastClass = signature;
      data.addClassMap(signature, nameDesc);
    } else if (addMethods) {
      final Matcher methodMatcher = MEMBER_PATTERN.matcher(line);
      Verify.verify(methodMatcher.find(), "members must be provided if we add methods");
      final String nameDesc = methodMatcher.group(2);
      if (nameDesc.contains("(")) {
        // TODO(Mariell Hoversholm): Where the hell does 40 come from?
        final String signature = csrgDesc(data, nameDesc.substring(nameDesc.indexOf(40)), methodMatcher.group(1));
        final String mojangName = nameDesc.substring(0, nameDesc.indexOf(40));
        data.addMethodMap(this.lastClass, methodMatcher.group(3), signature, mojangName);
      } else {
        data.addFieldMap(this.lastClass, methodMatcher.group(3), nameDesc);
      }
    }
  }

  @Override
  protected void loadStep2(final MappingData mappingData, final String line) {
    this.load(mappingData, line, true);
  }

  private static String csrgDesc(MappingData data, String args, String ret) {
    final String[] parts = args.substring(1, args.length() - 1).split(",");
    final StringBuilder builder = new StringBuilder("(");

    for (String part : parts) {
      if (!part.isEmpty()) {
        builder.append(toJvmType(data, part));
      }
    }

    return builder.append(')').append(toJvmType(data, ret)).toString();
  }

  private static String toJvmType(MappingData data, String type) {
    switch (type) {
      case "byte":
        return "B";
      case "short":
        return "S";
      case "int":
        return "I";
      case "long":
        return "J";
      case "float":
        return "F";
      case "double":
        return "D";
      case "void":
        return "V";
      case "boolean":
        return "Z";
      case "char":
        return "C";
      default:
        if (type.endsWith("[]")) {
          return "[" + toJvmType(data, type.substring(0, type.length() - 2));
        }

        Preconditions.checkArgument(type.startsWith("L"), "unknown type: " + type);
        final String typeName = type.replace('.', '/');
        final String mappedType = data.getClassMapInverse(typeName);
        return "L" + (mappedType != null ? mappedType : typeName) + ";";
    }
  }
}
