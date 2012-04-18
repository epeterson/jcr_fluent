package com.bitblossom.jcr_fluent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

/**
 * 
 * @author eli
 * 
 */
public class JcrQuery {

  private String path = "/";
  private String nodeName = "*";

  private boolean includingDescendantPaths = false;

  private final List<Predicate> predicates = new ArrayList<Predicate>();

  private JcrQuery(String path) {
    // TODO: Make sure that path fits a specific format (trailing-leading slashes, etc)
    this.path = path;
  }

  /**
   * All queries should start here. Describes the root path in the JCR Repository that the query
   * should be executed from.
   * 
   * @param path The query path.
   * @return A new JcrQuery object,
   */
  public static JcrQuery at(String path) {
    return new JcrQuery(path);
  }

  public JcrQuery withName(String nodeName) {
    this.nodeName = nodeName;
    return this;
  }

  public JcrQuery withType(String typeName) {
    Predicate typeComparison = Property.property("cq:type").eq(typeName);
    predicates.add(typeComparison);
    return this;
  }

  public JcrQuery includingDescendantPaths() {
    this.includingDescendantPaths = true;
    return this;
  }

  public JcrQuery with(Predicate... predicates) {
    this.predicates.addAll(Arrays.asList(predicates));
    return this;
  }

  public String buildStatement() {
    StringBuilder builder = new StringBuilder(path);
    if (includingDescendantPaths) {
      builder.append("/");
    }

    builder.append(nodeName);
    if (!predicates.isEmpty()) {
      builder.append("[");

      builder.append(predicate.asString());

      builder.append("]");
    }
    return builder.toString();
  }

  @SuppressWarnings("deprecation")
  public NodeIterator execute(Session session) throws RepositoryException {
    QueryManager queryManager = session.getWorkspace().getQueryManager();
    Query query = queryManager.createQuery(buildStatement(), Query.XPATH);
    return query.execute().getNodes();
  }

}
