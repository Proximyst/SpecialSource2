package net.md_5.ss;

import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import net.md_5.ss.mapping.MappingData;
import net.md_5.ss.remapper.EnhancedRemapper;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class SyntheticFinder {

  public static void addSynthetics(ClassNode node, EnhancedRemapper remapper, MappingData mappings) {
    Iterator iterator = node.methods.iterator();

    while (iterator.hasNext()) {
      MethodNode method = (MethodNode) iterator.next();

      if ((method.access & Opcodes.ACC_SYNTHETIC) != 0 && (method.access & Opcodes.ACC_BRIDGE) == 0 && !method.name.contains("$")) {
        AbstractInsnNode insn = null;
        UnmodifiableIterator iter = Iterators.filter(method.instructions.iterator(),
            input -> !(input instanceof LabelNode) && !(input instanceof LineNumberNode)
                && !(input instanceof TypeInsnNode));
        int expected = 0;

        while (true) {
          if (iter.hasNext() && (insn = (AbstractInsnNode) iter.next()) instanceof VarInsnNode) {
            VarInsnNode load = (VarInsnNode) insn;

            if (load.var == expected) {
              expected += load.getOpcode() != 22 && load.getOpcode() != 24 ? 1 : 2;
              continue;
            }
          }

          if (insn == null || insn.getOpcode() != 182 && insn.getOpcode() != 185) {
            break;
          }

          MethodInsnNode invoke = (MethodInsnNode) insn;

          insn = (AbstractInsnNode) iter.next();
          if (172 <= insn.getOpcode() && insn.getOpcode() <= 177 && !iter.hasNext() && node.name.equals(invoke.owner)
              && !method.name.equals(invoke.name) && !method.desc.equals(invoke.desc)) {
            Type methodType = Type.getMethodType(method.desc);
            Type invokeType = Type.getMethodType(invoke.desc);

            if (methodType.getArgumentTypes().length == invokeType.getArgumentTypes().length) {
              mappings.addMethodMap(invoke.owner, invoke.name, invoke.desc, method.name);
            }
          }
          break;
        }
      }
    }

  }

  private SyntheticFinder() {
  }
}
