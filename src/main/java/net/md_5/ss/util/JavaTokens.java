package net.md_5.ss.util;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class JavaTokens {

  public static final Set<String> TOKENS = ImmutableSet
      .of("abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue",
          "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "goto", "if",
          "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private",
          "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this",
          "throw", "throws", "transient", "try", "void", "volatile", "while", "true", "false", "null");

  /**
   * Append an underscore if deemed a Java "token" (read: keyword).
   * <p>
   * To be a token, the {@code name} must:
   * <ul>
   *   <li>Start with any of the valid Java {@link #TOKENS};</li>
   *   <li>Only contain any amount of underscores after the token matched.</li>
   * </ul>
   *
   * @param name the name of the member.
   * @return the new name or {@code null} if it was not valid.
   */
  public static String appendIfToken(String name) {
    for (String token : TOKENS) {
      if (name.startsWith(token)) {
        if (name.substring(token.length()).chars().anyMatch(c -> c != '_')) {
          return null;
        }

        return name + "_";
      }
    }

    return null;
  }

  private JavaTokens() {
  }
}
