package com.bitblossom.jcr_fluent;

import static com.bitblossom.jcr_fluent.Restriction.property;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class JcrQueryTest {

  @Test
  public void canChainMoreThanTwoRestrictions() {
    JcrQuery query =
        JcrQuery.at("/jcr:root/content/myapp/").matchingAll(property("propertyA").eq("valueA"),
            property("propertyB").eq("valueB"), property("propertyC").eq(42));

    assertTrue(query
        .buildStatement()
        .equals(
            "/jcr:root/content/myapp/*[@propertyA='valueA' and @propertyB='valueB' and @propertyC='42']"));
  }

  @Test
  public void canCreateScopedPredicates() {
    JcrQuery query =
        JcrQuery.at("/jcr:root/content/myapp/").matchingAll(
            property("propertyA").eq("valueA").orMatchingAny(property("propertyB").eq("valueB"),
                property("propertyC").eq("valueC")));

    System.out.println(query.buildStatement());
    assertTrue(query
        .buildStatement()
        .equals(
            "/jcr:root/content/myapp/*[@propertyA='valueA' and (@propertyB='valueB' or @propertyC='42')]"));

  }

}
