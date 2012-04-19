package com.bitblossom.jcr_fluent;

import static com.bitblossom.jcr_fluent.Predicate.any;
import static com.bitblossom.jcr_fluent.Property.property;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class JcrQueryTest {

  @Test
  public void canChainMoreThanTwoRestrictions() {
    JcrQuery query =
        JcrQuery.at("/jcr:root/content/myapp/").with(property("propertyA").eq("valueA"),
            property("propertyB").eq("valueB"), property("propertyC").eq(42));

    assertTrue(query
        .buildStatement()
        .equals(
            "/jcr:root/content/myapp/*[@propertyA='valueA' and @propertyB='valueB' and @propertyC='42']"));
  }

  @Test
  public void canCreateScopedPredicates() {
    JcrQuery query =
        JcrQuery.at("/jcr:root/content/myapp/").with(property("propertyA").eq("valueA"),
            any(property("propertyB").eq("valueB"), property("propertyC").eq(42)));

    assertTrue(query
        .buildStatement()
        .equals(
            "/jcr:root/content/myapp/*[@propertyA='valueA' and (@propertyB='valueB' or @propertyC='42')]"));

  }

}
