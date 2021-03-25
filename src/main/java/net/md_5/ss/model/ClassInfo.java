package net.md_5.ss.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import net.md_5.ss.remapper.ClassRemapper;
import net.md_5.ss.remapper.EnhancedRemapper;
import net.md_5.ss.repo.ClassRepo;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class ClassInfo implements ItemInfo {
  private final Map<String, MethodInfo> methodCache = new HashMap<>();
  private final Map<String, FieldInfo> fieldCache = new HashMap<>();

  private ClassRepo repo;
  private ClassReader reader;
  private ClassNode node;

  public ClassInfo(ClassRepo repo, ClassReader reader, ClassNode node) {
    this.repo = repo;
    this.reader = reader;
    this.node = node;
  }

  public MethodInfo getMethod(String name, String desc, boolean exact) {
    String key = name + " " + desc;
    MethodInfo info = this.methodCache.get(key);

    if (info == null) {
      Type targetType = Type.getMethodType(desc);

      for (final MethodNode method : this.node.methods) {
        Type candidateType = Type.getMethodType(method.desc);

        if (method.name.equals(name)
            && (exact && method.desc.equals(desc)
            || Arrays.equals(candidateType.getArgumentTypes(), targetType.getArgumentTypes())
        )) {
          info = new MethodInfo(this, name, method.desc, method.access);
          this.methodCache.put(key, info);
          break;
        }
      }
    }

    return info;
  }

  public FieldInfo getField(String name, String desc) {
    String key = name + " " + desc;
    FieldInfo info = this.fieldCache.get(key);
    if (info != null) {
      return info;
    }

    for (final FieldNode field : this.node.fields) {
      if (field.name.equals(name) && field.desc.equals(desc)) {
        info = new FieldInfo(this, name, desc, field.access);
        this.fieldCache.put(key, info);
        break;
      }
    }

    return info;
  }

  public String getName() {
    return this.node.name;
  }

  public Stream<String> getParents() {
    return Stream.concat(this.node.interfaces.stream(), Stream.ofNullable(this.node.superName));
  }

  public Stream<ClassInfo> getParentClasses() {
    return this.getParents()
        .map(this.repo::getClass)
        .filter(Objects::nonNull);
  }

  public byte[] toByteArray() {
    ClassWriter cw = new ClassWriter(0);

    this.node.accept(cw);
    return cw.toByteArray();
  }

  public byte[] remap(EnhancedRemapper remapper) {
    ClassWriter cw = new ClassWriter(this.reader, 0);

    this.reader.accept(new ClassRemapper(cw, remapper), 0);
    return cw.toByteArray();
  }

  public ClassRepo getRepo() {
    return this.repo;
  }

  public ClassReader getReader() {
    return this.reader;
  }

  public ClassNode getNode() {
    return this.node;
  }

  public Map<String, MethodInfo> getMethodCache() {
    return this.methodCache;
  }

  public Map<String, FieldInfo> getFieldCache() {
    return this.fieldCache;
  }

  public void setRepo(ClassRepo repo) {
    this.repo = repo;
  }

  public void setReader(ClassReader reader) {
    this.reader = reader;
  }

  public void setNode(ClassNode node) {
    this.node = node;
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ClassInfo)) {
      return false;
    }
    ClassInfo other = (ClassInfo) o;
    return Objects.equals(this.getRepo(), other.getRepo())
        && Objects.equals(this.getReader(), other.getReader())
        && Objects.equals(this.getNode(), other.getNode())
        && Objects.equals(this.getMethodCache(), other.getMethodCache())
        && Objects.equals(this.getFieldCache(), other.getFieldCache());
  }

  public int hashCode() {
    return Objects.hash(this.getRepo(), this.getReader(), this.getNode(),
        this.getMethodCache(), this.getFieldCache());
  }

  public String toString() {
    return "ClassInfo(repo=" + this.getRepo() + ", reader=" + this.getReader() + ", node=" + this.getNode()
        + ", methodCache=" + this.getMethodCache() + ", fieldCache=" + this.getFieldCache() + ")";
  }
}
