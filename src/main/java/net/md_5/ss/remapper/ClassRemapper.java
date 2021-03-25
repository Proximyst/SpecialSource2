package net.md_5.ss.remapper;

import org.apache.bcel.classfile.Utility;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public final class ClassRemapper extends org.objectweb.asm.commons.ClassRemapper {
  private final EnhancedRemapper remapper;

  public ClassRemapper(ClassVisitor cv, EnhancedRemapper remapper) {
    super(cv, remapper);
    this.remapper = remapper;
  }

  @Override
  public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
    FieldVisitor fv = this.cv
        .visitField(access, this.remapper.mapFieldName(this.className, name, desc, access), this.remapper.mapDesc(desc),
            this.remapper.mapSignature(signature, true), this.remapper.mapValue(value));

    return fv == null ? null : this.createFieldRemapper(fv);
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    signature = this.remapper.mapSignature(signature, false);
    if (signature != null && signature.charAt(0) == '(') {
      String[] sigArgs = Utility.methodSignatureArgumentTypes(signature);

      if (sigArgs.length != Type.getArgumentTypes(desc).length) {
        signature = null;
      }
    }

    MethodVisitor mv = this.cv.visitMethod(access, this.remapper.mapMethodName(this.className, name, desc, access),
        this.remapper.mapMethodDesc(desc), signature, exceptions == null ? null : this.remapper.mapTypes(exceptions));

    return mv == null ? null : this.createMethodRemapper(mv, access);
  }

  @Override
  protected MethodVisitor createMethodRemapper(MethodVisitor methodVisitor) {
    throw new UnsupportedOperationException("Unspecified flags");
  }

  private MethodVisitor createMethodRemapper(MethodVisitor methodVisitor, int access) {
    return (MethodVisitor) (
        this.remapper.getLvtStyle() != null && this.remapper.getLvtStyle() != EnhancedMethodRemapper.LVTStyle.NONE
            ? new EnhancedMethodRemapper(methodVisitor, this.remapper, access)
            : super.createMethodRemapper(methodVisitor));
  }

  @Override
  public void visitInnerClass(String name, String outerName, String innerName, int access) {
    String mappedName = this.remapper.mapType(name);
    String mappedInner = innerName;

    if (innerName != null) {
      int innerIndex = mappedName.lastIndexOf(36);

      if (innerIndex != -1) {
        mappedInner = mappedName.substring(innerIndex + 1);
      }
    }

    super.visitInnerClass(mappedName, outerName == null ? null : this.remapper.mapType(outerName), mappedInner, access);
  }

  @Override
  public void visitOuterClass(String owner, String name, String desc) {
    super.visitOuterClass(this.remapper.mapType(owner),
        name == null ? null : this.remapper.mapMethodName(owner, name, desc),
        desc == null ? null : this.remapper.mapMethodDesc(desc));
  }
}
