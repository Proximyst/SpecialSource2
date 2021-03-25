package net.md_5.ss.remapper;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
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
    private final Collection autoMember;
    private final EnhancedMethodRemapper.LVTStyle lvtStyle;

    /** @deprecated */
    @Deprecated
    public final String mapFieldName(String owner, String name, String desc) {
        FieldInfo declarer = this.findFieldDeclarer(owner, name, desc, false, true);

        return this.mapFieldName(owner, name, desc, declarer == null ? -1 : declarer.getAccess());
    }

    /** @deprecated */
    @Deprecated
    public final String mapMethodName(String owner, String name, String desc) {
        MethodInfo declarer = this.findMethodDeclarer(owner, name, desc, false, true, true);

        return this.mapMethodName(owner, name, desc, declarer == null ? -1 : declarer.getAccess());
    }

    private FieldInfo findFieldDeclarer(String owner, String name, String desc, boolean ignoreFirst, boolean allowPrivate) {
        ClassInfo ownerInfo = this.repo.getClass(owner);

        if (ownerInfo != null) {
            FieldInfo info = ignoreFirst ? null : ownerInfo.getField(name, desc);

            if (info == null) {
                Iterator iterator = ownerInfo.getParentClasses().iterator();

                while (iterator.hasNext()) {
                    ClassInfo parent = (ClassInfo) iterator.next();

                    info = this.findFieldDeclarer(parent.getName(), name, desc, false, false);
                    if (info != null) {
                        break;
                    }
                }
            }

            return info != null && !allowPrivate && Modifier.isPrivate(info.getAccess()) ? null : info;
        } else {
            return null;
        }
    }

    private MethodInfo findMethodDeclarer(String owner, String name, String desc, boolean ignoreFirst, boolean exact, boolean allowPrivate) {
        ClassInfo ownerInfo = this.repo.getClass(owner);

        if (ownerInfo != null) {
            MethodInfo info = ignoreFirst ? null : ownerInfo.getMethod(name, desc, exact);

            if (info == null) {
                Iterator iterator = ownerInfo.getParentClasses().iterator();

                while (iterator.hasNext()) {
                    ClassInfo parent = (ClassInfo) iterator.next();

                    info = this.findMethodDeclarer(parent.getName(), name, desc, false, exact, false);
                    if (info != null) {
                        break;
                    }
                }
            }

            return info != null && !allowPrivate && Modifier.isPrivate(info.getAccess()) ? null : info;
        } else {
            return null;
        }
    }

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

            if (this.autoMember.contains(EnhancedRemapper.AutoMember.LOGGER) && Modifier.isStatic(access) && Modifier.isFinal(access) && desc.equals("Lorg/apache/logging/log4j/Logger;")) {
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
                mapped = this.mapFieldName0(declarer.getOwner().getName(), declarer.getName(), declarer.getDesc(), declarer.getAccess());
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
                mapped = this.mapMethodName0(declarer.getOwner().getName(), declarer.getName(), declarer.getDesc(), declarer.getAccess());
            }
        }

        return mapped;
    }

    public EnhancedRemapper(ClassRepo repo, MappingData data, Collection autoMember, EnhancedMethodRemapper.LVTStyle lvtStyle) {
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

    public Collection getAutoMember() {
        return this.autoMember;
    }

    public EnhancedMethodRemapper.LVTStyle getLvtStyle() {
        return this.lvtStyle;
    }

    public String toString() {
        return "EnhancedRemapper(repo=" + this.getRepo() + ", data=" + this.getData() + ", autoMember=" + this.getAutoMember() + ", lvtStyle=" + this.getLvtStyle() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof EnhancedRemapper)) {
            return false;
        } else {
            EnhancedRemapper other = (EnhancedRemapper) o;

            if (!other.canEqual(this)) {
                return false;
            } else {
                label59:
                {
                    ClassRepo this$repo = this.getRepo();
                    ClassRepo other$repo = other.getRepo();

                    if (this$repo == null) {
                        if (other$repo == null) {
                            break label59;
                        }
                    } else if (this$repo.equals(other$repo)) {
                        break label59;
                    }

                    return false;
                }

                label52:
                {
                    MappingData this$data = this.getData();
                    MappingData other$data = other.getData();

                    if (this$data == null) {
                        if (other$data == null) {
                            break label52;
                        }
                    } else if (this$data.equals(other$data)) {
                        break label52;
                    }

                    return false;
                }

                label45:
                {
                    Collection this$autoMember = this.getAutoMember();
                    Collection other$autoMember = other.getAutoMember();

                    if (this$autoMember == null) {
                        if (other$autoMember == null) {
                            break label45;
                        }
                    } else if (this$autoMember.equals(other$autoMember)) {
                        break label45;
                    }

                    return false;
                }

                EnhancedMethodRemapper.LVTStyle this$lvtStyle = this.getLvtStyle();
                EnhancedMethodRemapper.LVTStyle other$lvtStyle = other.getLvtStyle();

                if (this$lvtStyle == null) {
                    if (other$lvtStyle == null) {
                        return true;
                    }
                } else if (this$lvtStyle.equals(other$lvtStyle)) {
                    return true;
                }

                return false;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof EnhancedRemapper;
    }

    public int hashCode() {
        ClassRepo $repo = this.getRepo();
        int result = 59 + ($repo == null ? 43 : $repo.hashCode());
        MappingData $data = this.getData();

        result = result * 59 + ($data == null ? 43 : $data.hashCode());
        Collection $autoMember = this.getAutoMember();

        result = result * 59 + ($autoMember == null ? 43 : $autoMember.hashCode());
        EnhancedMethodRemapper.LVTStyle $lvtStyle = this.getLvtStyle();

        result = result * 59 + ($lvtStyle == null ? 43 : $lvtStyle.hashCode());
        return result;
    }

    public static enum AutoMember {

        SYNTHETIC, TOKENS, LOGGER;
    }
}
