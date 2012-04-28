package com.bitblossom.jcr_fluent;

import static com.bitblossom.jcr_fluent.Predicate.any;
import static com.bitblossom.jcr_fluent.Property.property;
import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Test;

public class JcrQueryTest {

  @Test
  public void canChainMoreThanTwoRestrictions() {
    JcrQuery query =
        JcrQuery.at("/jcr:root/content/myapp/").with(property("@propertyA").eq("valueA"),
            property("@propertyB").eq("valueB"), property("@propertyC").eq(42));

    assertEquals(
        "/jcr:root/content/myapp/*[@propertyA='valueA' and @propertyB='valueB' and @propertyC='42']",
        query.buildStatement());
  }

  @Test
  public void canCreateScopedPredicates() {
    JcrQuery query =
        JcrQuery.at("/jcr:root/content/myapp/").with(property("@propertyA").eq("valueA"),
            any(property("@propertyB").eq("valueB"), property("@propertyC").eq(42)));

    assertEquals(
        "/jcr:root/content/myapp/*[@propertyA='valueA' and (@propertyB='valueB' or @propertyC='42')]",
        query.buildStatement());
  }

  @Test
  public void canGenerateProperDates() {
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT-4:00"));
    cal.set(2011, 5, 13, 23, 45, 59);
    cal.set(Calendar.MILLISECOND, 0);

    JcrQuery query =
        JcrQuery.at("/jcr:root/cars/").includingDescendantPaths()
            .with(property("@jcr:created").gt(cal));

    assertEquals("/jcr:root/cars//*[@jcr:created>xs:dateTime('2011-06-13T23:45:59-04:00')]",
        query.buildStatement());
  }

  @Test
  public void canApplyNodeTypeConstraint() {
    JcrQuery query = JcrQuery.at("/").includingDescendantPaths().withType("nt:resource");
    String expected = "//element(*, nt:resource)";
    assertEquals(expected, query.buildStatement());
  }

  @Test
  public void canApplyNodeTypeConstraintWithPredicate() {
    JcrQuery query =
        JcrQuery.at("/").includingDescendantPaths().withType("nt:resource")
            .with(property("jcr:content/@jcr:data").exists());
    String expected = "//element(*, nt:resource)[jcr:content/@jcr:data]";
    assertEquals(expected, query.buildStatement());
  }

}
