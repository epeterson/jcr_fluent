package com.bitblossom.jcr_fluent;

import static com.bitblossom.jcr_fluent.Property.property;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.core.TransientRepository;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JcrQueryIntegrationTest {

  private static final Logger log = LoggerFactory.getLogger(JcrQueryIntegrationTest.class);

  private static Repository repository = new TransientRepository();
  private static Session session;

  @BeforeClass
  public static void repositorySetup() throws RepositoryException, IOException {
    session = repository.login(new SimpleCredentials("username", "password".toCharArray()));
    Node root = session.getRootNode();

    // Import the XML file unless already imported
    if (!root.hasNode("ellipse_test")) {
      log.info("Importing xml... ");

      // Create an unstructured node under which to import the XML
      Node node = root.addNode("ellipse_test", "nt:unstructured");

      // Import the file under the created node
      FileInputStream xml = new FileInputStream("target/test-classes/ellipse_test.xml");
      session.importXML(node.getPath(), xml, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);
      xml.close();

      session.save();
      log.info("done.");
    }
  }

  @AfterClass
  public static void cleanUp() {
    session.logout();
  }

  @Test
  public void canLocateTitle() throws RepositoryException {
    NodeIterator nodeItr =
        JcrQuery.at("/jcr:root/ellipse_test/xhtml:html/xhtml:head/xhtml:title/")
            .withName("jcr:xmltext").with(property("jcr:xmlcharacters").eq("Three Namespaces"))
            .execute(session);

    assertTrue(nodeItr.hasNext());
    log.info("Node found: {}", nodeItr.nextNode());
  }
}
