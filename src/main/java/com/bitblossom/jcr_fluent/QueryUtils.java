package com.bitblossom.jcr_fluent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;


public class QueryUtils {

  private static final Logger log = LoggerFactory.getLogger(QueryUtils.class);

  /**
   * <p>
   * Given a JCR Path, returns a version escaped such that it is usable by the JCR Repository.
   * </p>
   * <p>
   * In XML, a node is disallowed from starting with a number - but in order to facilitate a common
   * use-case, JCR repositories represent such node names by escaping the leading number in the
   * format "_x0000_", where "0000" represents a four-digit hexadecimal representation of the
   * number.
   * </p>
   * <p>
   * For example, the properly escaped form of the path: "/jcr:root/content/myapp/2012/04/26" is:
   * "/jcr:root/content/myapp/_x0032_012/_x0030_4/_x0032_6".
   * </p>
   * 
   * @param path A String representing a JCR Path to be escaped
   * @return An escaped version of the provided String
   */
  public static String encodePath(String path) {
    log.debug("Encoding path {}", path);
    Pattern startingSlashes = Pattern.compile("^[/]+");
    Pattern trailingSlashes = Pattern.compile("[/]+$");

    String prefix = "";
    Matcher startingMatcher = startingSlashes.matcher(path);
    if (startingMatcher.find()) {
      prefix = startingMatcher.group();
      path = path.substring(startingMatcher.end());
      log.trace("Prefix match found @pos {}: {} - {}",
          new String[] {String.valueOf(startingMatcher.start()), prefix, path});
    }

    String suffix = "";
    Matcher trailingMatcher = trailingSlashes.matcher(path);
    if (trailingMatcher.find()) {
      suffix = trailingMatcher.group();
      path = path.substring(0, trailingMatcher.start());
      log.trace("Trailing match found @pos {}: {} - {}",
          new String[] {String.valueOf(trailingMatcher.start()), suffix, path});
    }

    StringBuilder encodedPath = new StringBuilder(prefix);
    String[] pathSegments = path.split("/");
    for (int i = 0; i < pathSegments.length; i++) {
      pathSegments[i] = encodeString(pathSegments[i]);
    }
    encodedPath.append(Joiner.on("/").join(pathSegments));
    encodedPath.append(suffix);
    String encodedPathString = encodedPath.toString();
    log.debug("Encoded path: {}", encodedPathString);
    return encodedPathString;
  }

  /**
   * If the provided String begins with a digit, escapes the String such that the leading digit is
   * replaced by the sequence "_x0000_", where 0000 is a four-digit hexadecimal representation of
   * the number.
   * 
   * @param string A String to be escaped
   * @return An escaped version of the provided String
   */
  private static String encodeString(String string) {
    if (string.isEmpty()) {
      return string;
    }

    char firstChar = string.charAt(0);
    if (Character.isDigit(firstChar)) {
      String remainingString = string.substring(1);
      String encodedChar = Integer.toHexString(firstChar);
      String encodedPrefix = Strings.padStart(encodedChar, 4, '0');
      return String.format("_x%s_%s", encodedPrefix, remainingString);
    } else {
      return string;
    }
  }

}
