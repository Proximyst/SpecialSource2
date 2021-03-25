package net.md_5.ss.output;

import java.io.File;
import java.io.IOException;

public abstract class Output {

    private final File file;

    public abstract void write(String s, byte[] abyte) throws IOException;

    public abstract void close() throws IOException;

    public Output(File file) {
        this.file = file;
    }

    public File getFile() {
        return this.file;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Output)) {
            return false;
        } else {
            Output other = (Output) o;

            if (!other.canEqual(this)) {
                return false;
            } else {
                File this$file = this.getFile();
                File other$file = other.getFile();

                if (this$file == null) {
                    if (other$file == null) {
                        return true;
                    }
                } else if (this$file.equals(other$file)) {
                    return true;
                }

                return false;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof Output;
    }

    public int hashCode() {
        File $file = this.getFile();
        int result = 59 + ($file == null ? 43 : $file.hashCode());

        return result;
    }

    public String toString() {
        return "Output(file=" + this.getFile() + ")";
    }
}
