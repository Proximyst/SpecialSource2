package net.md_5.ss.model;

public class GenericInfo {

    private final ClassInfo owner;
    private final String name;
    private final String desc;
    private final int access;

    public GenericInfo(ClassInfo owner, String name, String desc, int access) {
        this.owner = owner;
        this.name = name;
        this.desc = desc;
        this.access = access;
    }

    public ClassInfo getOwner() {
        return this.owner;
    }

    public String getName() {
        return this.name;
    }

    public String getDesc() {
        return this.desc;
    }

    public int getAccess() {
        return this.access;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof GenericInfo)) {
            return false;
        } else {
            GenericInfo other = (GenericInfo) o;

            if (!other.canEqual(this)) {
                return false;
            } else {
                label51:
                {
                    ClassInfo this$owner = this.getOwner();
                    ClassInfo other$owner = other.getOwner();

                    if (this$owner == null) {
                        if (other$owner == null) {
                            break label51;
                        }
                    } else if (this$owner.equals(other$owner)) {
                        break label51;
                    }

                    return false;
                }

                label44:
                {
                    String this$name = this.getName();
                    String other$name = other.getName();

                    if (this$name == null) {
                        if (other$name == null) {
                            break label44;
                        }
                    } else if (this$name.equals(other$name)) {
                        break label44;
                    }

                    return false;
                }

                String this$desc = this.getDesc();
                String other$desc = other.getDesc();

                if (this$desc == null) {
                    if (other$desc == null) {
                        return this.getAccess() == other.getAccess();
                    }
                } else if (this$desc.equals(other$desc)) {
                    return this.getAccess() == other.getAccess();
                }

                return false;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof GenericInfo;
    }

    public int hashCode() {
        ClassInfo $owner = this.getOwner();
        int result = 59 + ($owner == null ? 43 : $owner.hashCode());
        String $name = this.getName();

        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $desc = this.getDesc();

        result = result * 59 + ($desc == null ? 43 : $desc.hashCode());
        result = result * 59 + this.getAccess();
        return result;
    }

    public String toString() {
        return "GenericInfo(owner=" + this.getOwner() + ", name=" + this.getName() + ", desc=" + this.getDesc() + ", access=" + this.getAccess() + ")";
    }
}
