package net.md_5.ss.remapper;

import java.lang.reflect.Modifier;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.MethodRemapper;

public class EnhancedMethodRemapper extends MethodRemapper {

  private final EnhancedRemapper remapper;
  private final int methodAccess;

  public EnhancedMethodRemapper(MethodVisitor methodVisitor, EnhancedRemapper remapper, int methodAccess) {
    super(methodVisitor, remapper);
    this.remapper = remapper;
    this.methodAccess = methodAccess;
  }

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
