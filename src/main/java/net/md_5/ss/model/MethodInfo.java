package net.md_5.ss.model;

public class MethodInfo extends GenericInfo {

    public MethodInfo(ClassInfo owner, String name, String desc, int access) {
        super(owner, name, desc, access);
    }

    public String toString() {
        return "MethodInfo()";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof MethodInfo)) {
            return false;
        } else {
            MethodInfo other = (MethodInfo) o;

            return !other.canEqual(this) ? false : super.equals(o);
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof MethodInfo;
    }

    public int hashCode() {
        int result = super.hashCode();

        return result;
    }
}
