package net.md_5.ss.repo;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import net.md_5.ss.model.ClassInfo;
import net.md_5.ss.model.ItemInfo;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

public class RuntimeRepo extends ClassRepo {
  private static final RuntimeRepo instance = new RuntimeRepo();

  protected ClassInfo getClass0(String internalName) throws IOException {
    ClassReader cr = new ClassReader(internalName);
    ClassNode node = new ClassNode();

    cr.accept(node, 0);
    return new ClassInfo("", this, cr, node);
  }

  @Override
  public Iterator<ItemInfo> iterator() {
    return Collections.emptyIterator();
  }

  private RuntimeRepo() {
  }

  public static RuntimeRepo getInstance() {
    return RuntimeRepo.instance;
  }
}
