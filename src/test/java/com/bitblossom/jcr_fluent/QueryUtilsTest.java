package com.bitblossom.jcr_fluent;

import static org.junit.Assert.assertEquals;

import org.apache.jackrabbit.util.ISO9075;
import org.junit.Test;

public class QueryUtilsTest {

  @Test
  public void canProperlyEscapePathSegmentsStartingWithNumbers() {
    String path = "/jcr:root/content/myapp/2012/04/19/mynode";
    String escapedPath = ISO9075.encodePath(path);
    assertEquals(escapedPath, QueryUtils.encodePath(path));
  }

  @Test
  public void canProperlyEscapePathsWhichIncludeAllChildrenSelector() {
    String path = "/jcr:root/content//myapp//2012/04/19/mynode/";
    String escapedPath = ISO9075.encodePath(path);
    assertEquals(escapedPath, QueryUtils.encodePath(path));
  }

  @Test
  public void canProperlyEscapePathsWithStartingAndTrailingAllChildrenSelectors() {
    String path = "//jcr:root/content/myapp/2012/04/19/mynode//";
    String escapedPath = ISO9075.encodePath(path);
    assertEquals(escapedPath, QueryUtils.encodePath(path));
  }

  @Test
  public void canProperlyEscapePathsWithNoStartingOrTrailingSlashes() {
    String path = "jcr:root/content/myapp/2012/04/19/mynode";
    String escapedPath = ISO9075.encodePath(path);
    assertEquals(escapedPath, QueryUtils.encodePath(path));
  }

}
