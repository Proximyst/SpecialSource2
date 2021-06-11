package net.md_5.ss.remapper;

import java.lang.reflect.Modifier;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.MethodRemapper;

public class EnhancedMethodRemapper extends MethodRemapper {

  private final EnhancedRemapper remapper;
  private final int methodAccess;
  private final boolean isEnumConstructor;

  public EnhancedMethodRemapper(MethodVisitor methodVisitor, EnhancedRemapper remapper, int methodAccess, boolean isEnumConstructor) {
    super(methodVisitor, remapper);
    this.remapper = remapper;
    this.methodAccess = methodAccess;
    this.isEnumConstructor = isEnumConstructor;
  }

  @Override
  public @Nullable AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
    return this.isEnumConstructor ? null : super.visitParameterAnnotation(parameter, descriptor, visible);
  }

  @Override
  public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
    if (this.remapper.getLvtStyle() != LVTStyle.BASIC) {
      throw new IllegalArgumentException("Cannot map LVT in style " + this.remapper.getLvtStyle());
    }

    if (Modifier.isStatic(this.methodAccess)) {
      name = "var" + index;
    } else {
      name = index == 0 ? "this" : "var" + (index - 1);
    }

    super.visitLocalVariable(name, descriptor, signature, start, end, index);
    return;
  }

  public enum LVTStyle {
    NONE,
    BASIC,
  }
}
