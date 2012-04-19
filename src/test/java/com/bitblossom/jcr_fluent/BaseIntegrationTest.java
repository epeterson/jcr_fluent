package com.bitblossom.jcr_fluent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.core.TransientRepository;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseIntegrationTest {

  private static final Logger log = LoggerFactory.getLogger(BaseIntegrationTest.class);

  protected static Repository repository;
  protected static Session session;

  /**
   * Populates the static repository and session fields. Creates new test repository if one did not
   * already exist.
   * 
   * @throws RepositoryException
   * @throws IOException
   */
  @BeforeClass
  public static void repositorySetup() throws RepositoryException, IOException {
    // Create the repository in the target directory, so it will be removed when "mvn clean" is run
    // Files and paths are automatically created by Jackrabbit
    File xml = new File("target/repository.xml");
    File dir = new File("target/repository/");
    repository = new TransientRepository(xml, dir);
    session = repository.login(new SimpleCredentials("username", "password".toCharArray()));
  }

  @AfterClass
  public static void cleanUp() {
    session.logout();
  }

  /**
   * Imports the contents of an XML file containing test data into the repository. A new top-level
   * path is created for the test data.
   * 
   * If the top-level path already exists in the repository, the import is skipped.
   * 
   * @param dataFile Relative file-system path to an XML file containing test data.
   * @param repoPath Top-level path in the repository that the data will be stored under.
   * @throws RepositoryException
   * @throws IOException
   */
  protected static void importTestData(String dataFile, String repoPath)
      throws RepositoryException, IOException {

    Node root = session.getRootNode();

    // Import the XML file unless already imported
    if (!root.hasNode(repoPath)) {
      log.info("Importing data from file {} at repository path {}... ", dataFile, repoPath);

      // Create an unstructured node under which to import the XML
      Node node = root.addNode(repoPath, "nt:unstructured");

      // Import the file under the created node
      FileInputStream data = new FileInputStream(dataFile);
      session.importXML(node.getPath(), data, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);
      data.close();

      session.save();
    }
  }

}
