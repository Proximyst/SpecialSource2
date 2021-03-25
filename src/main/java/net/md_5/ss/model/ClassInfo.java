package net.md_5.ss.model;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
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

    private ClassRepo repo;
    private ClassReader reader;
    private ClassNode node;
    private final Map methodCache = new HashMap();
    private final Map fieldCache = new HashMap();

    public ClassInfo(ClassRepo repo, ClassReader reader, ClassNode node) {
        this.repo = repo;
        this.reader = reader;
        this.node = node;
    }

    public MethodInfo getMethod(String name, String desc, boolean exact) {
        String key = name + " " + desc;
        MethodInfo info = (MethodInfo) this.methodCache.get(key);

        if (info == null) {
            Type targetType = Type.getMethodType(desc);
            Iterator iterator = this.node.methods.iterator();

            while (iterator.hasNext()) {
                MethodNode method = (MethodNode) iterator.next();
                Type candidateType = Type.getMethodType(method.desc);

                if (method.name.equals(name) && (exact && method.desc.equals(desc) || Arrays.equals(candidateType.getArgumentTypes(), targetType.getArgumentTypes()))) {
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
        FieldInfo info = (FieldInfo) this.fieldCache.get(key);

        if (info == null) {
            Iterator iterator = this.node.fields.iterator();

            while (iterator.hasNext()) {
                FieldNode field = (FieldNode) iterator.next();

                if (field.name.equals(name) && field.desc.equals(desc)) {
                    info = new FieldInfo(this, name, desc, field.access);
                    this.fieldCache.put(key, info);
                    break;
                }
            }
        }

        return info;
    }

    public String getName() {
        return this.node.name;
    }

    public Iterable<String> getParents() {
        return (Iterable) (this.node.superName == null ? this.node.interfaces : Iterables.concat(this.node.interfaces, Collections.singleton(this.node.superName)));
    }

    public Iterable<ClassInfo> getParentClasses() {
        return Iterables.filter(Iterables.transform(this.getParents(),
            input -> ClassInfo.this.repo.getClass(input)), Objects::nonNull);
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

    public Map getMethodCache() {
        return this.methodCache;
    }

    public Map getFieldCache() {
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
        } else if (!(o instanceof ClassInfo)) {
            return false;
        } else {
            ClassInfo other = (ClassInfo) o;

            if (!other.canEqual(this)) {
                return false;
            } else {
                label71:
                {
                    ClassRepo this$repo = this.getRepo();
                    ClassRepo other$repo = other.getRepo();

                    if (this$repo == null) {
                        if (other$repo == null) {
                            break label71;
                        }
                    } else if (this$repo.equals(other$repo)) {
                        break label71;
                    }

                    return false;
                }

                label64:
                {
                    ClassReader this$reader = this.getReader();
                    ClassReader other$reader = other.getReader();

                    if (this$reader == null) {
                        if (other$reader == null) {
                            break label64;
                        }
                    } else if (this$reader.equals(other$reader)) {
                        break label64;
                    }

                    return false;
                }

                label57:
                {
                    ClassNode this$node = this.getNode();
                    ClassNode other$node = other.getNode();

                    if (this$node == null) {
                        if (other$node == null) {
                            break label57;
                        }
                    } else if (this$node.equals(other$node)) {
                        break label57;
                    }

                    return false;
                }

                label50:
                {
                    Map this$methodCache = this.getMethodCache();
                    Map other$methodCache = other.getMethodCache();

                    if (this$methodCache == null) {
                        if (other$methodCache == null) {
                            break label50;
                        }
                    } else if (this$methodCache.equals(other$methodCache)) {
                        break label50;
                    }

                    return false;
                }

                Map this$fieldCache = this.getFieldCache();
                Map other$fieldCache = other.getFieldCache();

                if (this$fieldCache == null) {
                    if (other$fieldCache == null) {
                        return true;
                    }
                } else if (this$fieldCache.equals(other$fieldCache)) {
                    return true;
                }

                return false;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof ClassInfo;
    }

    public int hashCode() {
        ClassRepo $repo = this.getRepo();
        int result = 59 + ($repo == null ? 43 : $repo.hashCode());
        ClassReader $reader = this.getReader();

        result = result * 59 + ($reader == null ? 43 : $reader.hashCode());
        ClassNode $node = this.getNode();

        result = result * 59 + ($node == null ? 43 : $node.hashCode());
        Map $methodCache = this.getMethodCache();

        result = result * 59 + ($methodCache == null ? 43 : $methodCache.hashCode());
        Map $fieldCache = this.getFieldCache();

        result = result * 59 + ($fieldCache == null ? 43 : $fieldCache.hashCode());
        return result;
    }

    public String toString() {
        return "ClassInfo(repo=" + this.getRepo() + ", reader=" + this.getReader() + ", node=" + this.getNode() + ", methodCache=" + this.getMethodCache() + ", fieldCache=" + this.getFieldCache() + ")";
    }
}
