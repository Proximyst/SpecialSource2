package net.md_5.ss.model;

import java.util.Arrays;

public class Resource implements ItemInfo {

    private final String path;
    private final byte[] data;

    public Resource(String path, byte[] data) {
        this.path = path;
        this.data = data;
    }

    public String getPath() {
        return this.path;
    }

    public byte[] getData() {
        return this.data;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Resource)) {
            return false;
        } else {
            Resource other = (Resource) o;

            if (!other.canEqual(this)) {
                return false;
            } else {
                String this$path = this.getPath();
                String other$path = other.getPath();

                if (this$path == null) {
                    if (other$path == null) {
                        return Arrays.equals(this.getData(), other.getData());
                    }
                } else if (this$path.equals(other$path)) {
                    return Arrays.equals(this.getData(), other.getData());
                }

                return false;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof Resource;
    }

    public int hashCode() {
        String $path = this.getPath();
        int result = 59 + ($path == null ? 43 : $path.hashCode());

        result = result * 59 + Arrays.hashCode(this.getData());
        return result;
    }

    public String toString() {
        return "Resource(path=" + this.getPath() + ", data=" + Arrays.toString(this.getData()) + ")";
    }
}
