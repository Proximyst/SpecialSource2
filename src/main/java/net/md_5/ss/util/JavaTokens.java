package net.md_5.ss.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class JavaTokens {

    public static final Set TOKENS = Collections.unmodifiableSet(new HashSet(Arrays.asList("abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while", "true", "false", "null")));

    public static String appendIfToken(String name) {
        Iterator iterator = JavaTokens.TOKENS.iterator();

        String token;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            token = (String) iterator.next();
        } while (!name.startsWith(token));

        String rest = name.substring(token.length());
        char[] achar = rest.toCharArray();
        int i = achar.length;

        for (int j = 0; j < i; ++j) {
            char c = achar[j];

            if (c != '_') {
                return null;
            }
        }

        return name + "_";
    }

    private JavaTokens() {}
}
