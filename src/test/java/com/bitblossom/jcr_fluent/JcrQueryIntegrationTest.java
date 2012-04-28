package com.bitblossom.jcr_fluent;

import static com.bitblossom.jcr_fluent.Predicate.all;
import static com.bitblossom.jcr_fluent.Property.property;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.junit.BeforeClass;
import org.junit.Test;

public class JcrQueryIntegrationTest extends BaseIntegrationTest {

  @BeforeClass
  public static void loadTestData() throws RepositoryException, IOException {
    importTestData("target/test-classes/ellipse_test.xml", "ellipse_test");
  }

  @Test
  public void canLocateTitleWithNameAndProperty() throws RepositoryException {
    NodeIterator nodeItr =
        JcrQuery.at("/jcr:root/ellipse_test/xhtml:html/xhtml:head/xhtml:title/")
            .withName("jcr:xmltext").with(property("@jcr:xmlcharacters").eq("Three Namespaces"))
            .execute(session);

    assertTrue(nodeItr.hasNext());
    assertEquals("jcr:xmltext", nodeItr.nextNode().getName());
  }

  @Test
  public void canLocateSvg() throws RepositoryException {
    NodeIterator nodeItr =
        JcrQuery.at("/jcr:root/ellipse_test/xhtml:html/xhtml:body/")
            .with(all(property("@width").eq("12cm"), property("@height").eq("10cm")))
            .execute(session);

    assertTrue(nodeItr.hasNext());
    assertEquals("svg:svg", nodeItr.nextNode().getName());
  }

  @Test
  public void canLocateElementWithPropertyExisting() throws RepositoryException {
    NodeIterator nodeItr =
        JcrQuery.at("/jcr:root/ellipse_test/xhtml:html/xhtml:body/")
            .with(property("@width").exists()).execute(session);
    assertTrue(nodeItr.hasNext());
    assertEquals("svg:svg", nodeItr.nextNode().getName());
  }

  @Test
  public void canLocateElementWithNestedProperty() throws RepositoryException {
    NodeIterator nodeItr =
        JcrQuery.at("/jcr:root/ellipse_test/").with(property("xhtml:body/svg:svg/@width").exists())
            .execute(session);
    assertTrue(nodeItr.hasNext());
    assertEquals("xhtml:html", nodeItr.nextNode().getName());
  }

}
