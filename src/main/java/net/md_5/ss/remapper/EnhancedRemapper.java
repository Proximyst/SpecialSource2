package net.md_5.ss.remapper;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Objects;
import net.md_5.ss.mapping.MappingData;
import net.md_5.ss.model.ClassInfo;
import net.md_5.ss.model.FieldInfo;
import net.md_5.ss.model.MethodInfo;
import net.md_5.ss.repo.ClassRepo;
import net.md_5.ss.util.JavaTokens;
import org.objectweb.asm.commons.Remapper;

public class EnhancedRemapper extends Remapper {
  private final ClassRepo repo;
  private final MappingData data;
  private final Collection<AutoMember> autoMember;
  private final EnhancedMethodRemapper.LVTStyle lvtStyle;

  /**
   * @deprecated
   */
  @Deprecated
  @Override
  public final String mapFieldName(String owner, String name, String desc) {
    FieldInfo declarer = this.findFieldDeclarer(owner, name, desc, false, true);

    return this.mapFieldName(owner, name, desc, declarer == null ? -1 : declarer.getAccess());
  }

  /**
   * @deprecated
   */
  @Deprecated
  @Override
  public final String mapMethodName(String owner, String name, String desc) {
    MethodInfo declarer = this.findMethodDeclarer(owner, name, desc, false, true, true);

    return this.mapMethodName(owner, name, desc, declarer == null ? -1 : declarer.getAccess());
  }

  private FieldInfo findFieldDeclarer(String owner, String name, String desc, boolean ignoreFirst,
      boolean allowPrivate) {
    ClassInfo ownerInfo = this.repo.getClass(owner);
    if (ownerInfo == null) {
      return null;
    }

    FieldInfo info = ignoreFirst ? null : ownerInfo.getField(name, desc);

    if (info == null) {
      info = ownerInfo.getParentClasses()
          .map(parent -> this.findFieldDeclarer(parent.getName(), name, desc, false, false))
          .findFirst()
          .orElse(null);
    }

    return info != null && !allowPrivate && Modifier.isPrivate(info.getAccess()) ? null : info;
  }

  private MethodInfo findMethodDeclarer(String owner, String name, String desc, boolean ignoreFirst, boolean exact,
      boolean allowPrivate) {
    ClassInfo ownerInfo = this.repo.getClass(owner);
    if (ownerInfo == null) {
      return null;
    }

    MethodInfo info = ignoreFirst ? null : ownerInfo.getMethod(name, desc, exact);

    if (info == null) {
      info = ownerInfo.getParentClasses()
          .map(parent -> this.findMethodDeclarer(parent.getName(), name, desc, false, exact, false))
          .findFirst()
          .orElse(null);
    }

    return info != null && !allowPrivate && Modifier.isPrivate(info.getAccess()) ? null : info;
  }

  @Override
  public String map(String typeName) {
    String mapped = this.data.getClassMap(typeName);

    if (mapped == null) {
      String outerName = typeName;

      int lastDollar;
      while ((lastDollar = outerName.lastIndexOf(36)) != -1) {
        String innerName = typeName.substring(lastDollar);

        outerName = outerName.substring(0, lastDollar);
        mapped = this.data.getClassMap(outerName);
        if (mapped != null) {
          mapped = mapped + innerName;
          break;
        }
      }
    }

    return mapped == null ? typeName : mapped;
  }

  public String mapFieldName(String owner, String name, String desc, int access) {
    String mapped = this.mapFieldName0(owner, name, desc, access);

    if (mapped == null) {
      if (this.autoMember.contains(EnhancedRemapper.AutoMember.TOKENS)) {
        mapped = JavaTokens.appendIfToken(name);
      }

      if (this.autoMember.contains(EnhancedRemapper.AutoMember.LOGGER) && Modifier.isStatic(access) && Modifier
          .isFinal(access) && desc.equals("Lorg/apache/logging/log4j/Logger;")) {
        mapped = "LOGGER";
      }
    }

    return mapped == null ? name : mapped;
  }

  private String mapFieldName0(String owner, String name, String desc, int access) {
    String mapped = this.data.getFieldMap(owner, name);

    if (mapped == null && !Modifier.isPrivate(access)) {
      FieldInfo declarer = this.findFieldDeclarer(owner, name, desc, true, false);

      if (declarer != null) {
        mapped = this
            .mapFieldName0(declarer.getOwner().getName(), declarer.getName(), declarer.getDesc(), declarer.getAccess());
      }
    }

    return mapped;
  }

  public String mapMethodName(String owner, String name, String desc, int access) {
    String mapped = this.mapMethodName0(owner, name, desc, access);

    if (mapped == null && this.autoMember.contains(EnhancedRemapper.AutoMember.TOKENS)) {
      mapped = JavaTokens.appendIfToken(name);
    }

    return mapped == null ? name : mapped;
  }

  private String mapMethodName0(String owner, String name, String desc, int access) {
    String mapped = this.data.getMethodMap(owner, name, desc);

    if (mapped == null && !Modifier.isPrivate(access)) {
      MethodInfo declarer = this.findMethodDeclarer(owner, name, desc, true, false, false);

      if (declarer != null) {
        mapped = this.mapMethodName0(declarer.getOwner().getName(), declarer.getName(), declarer.getDesc(),
            declarer.getAccess());
      }
    }

    return mapped;
  }

  public EnhancedRemapper(ClassRepo repo, MappingData data, Collection autoMember,
      EnhancedMethodRemapper.LVTStyle lvtStyle) {
    this.repo = repo;
    this.data = data;
    this.autoMember = autoMember;
    this.lvtStyle = lvtStyle;
  }

  public ClassRepo getRepo() {
    return this.repo;
  }

  public MappingData getData() {
    return this.data;
  }

  public Collection<AutoMember> getAutoMember() {
    return this.autoMember;
  }

  public EnhancedMethodRemapper.LVTStyle getLvtStyle() {
    return this.lvtStyle;
  }

  @Override
  public String toString() {
    return "EnhancedRemapper(repo=" + this.getRepo() + ", data=" + this.getData() + ", autoMember=" + this
        .getAutoMember() + ", lvtStyle=" + this.getLvtStyle() + ")";
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final EnhancedRemapper that = (EnhancedRemapper) o;
    return Objects.equals(getRepo(), that.getRepo())
        && Objects.equals(getData(), that.getData())
        && Objects.equals(getAutoMember(), that.getAutoMember())
        && getLvtStyle() == that.getLvtStyle();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getRepo(), getData(), getAutoMember(), getLvtStyle());
  }

  public enum AutoMember {
    SYNTHETIC,
    TOKENS,
    LOGGER,
  }
}
