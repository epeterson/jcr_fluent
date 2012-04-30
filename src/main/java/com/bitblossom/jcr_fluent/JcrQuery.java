package com.bitblossom.jcr_fluent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * The JcrQuery class provides an API to conveniently build and execute a JCR XPath query against a
 * JSR-283 repository. <br/>
 * <br/>
 * A typical example might look like this: <code>
 * NodeIterator nodes = JcrQuery.at("/jcr:root/content/")
 *                              .withName("jcr:content")
 *                              .with(property("username").eq("eli").execute(session);
 * </code>
 * 
 * @author eli
 */
public class JcrQuery {

  private static final Logger log = LoggerFactory.getLogger(JcrQuery.class);

  private String path = "/";
  private String nodeName = "*";
  private String nodeType = null;

  private boolean includingDescendantPaths = false;

  private final List<Predicate> predicates = new ArrayList<Predicate>();
  private final List<Order> orderings = new ArrayList<Order>();

  private JcrQuery(String path) {
    // Standardize trailing slashes, to allow for both "path/to/node" and "path/to/node/" as inputs
    if (path.length() > 1 && !path.endsWith("/")) {
      path = path.concat("/");
    }
    this.path = QueryUtils.encodePath(path);
  }

  /**
   * All queries should start here. Describes the root path in the JCR Repository that the query
   * should be executed from.
   * 
   * @param path The query path
   * @return A new JcrQuery object.
   */
  public static JcrQuery at(String path) {
    return new JcrQuery(path);
  }

  /**
   * Specifies a particular node name to match in the query. Not required.<br/>
   * <br/>
   * 
   * For example, given the query path "/jcr:root/content/", specifying a node name of "my_app"
   * would result in the following: "/jcr:root/content/my_app". If no node name was provided, the
   * executed query would result in the following: "/jcr:root/content/*".
   * 
   * @param nodeName A Node name to match
   * @return An updated JcrQuery object.
   */
  public JcrQuery withName(String nodeName) {
    this.nodeName = nodeName;
    return this;
  }

  /**
   * Specifies a particular CQ node type to match in the query. Not required.
   * 
   * @param typeName A node type to match
   * @return An updated JcrQuery object.
   */
  public JcrQuery withType(String typeName) {
    this.nodeType = typeName;
    return this;
  }

  /**
   * Specifies that we want to include all descending paths in our search.<br/>
   * <br/>
   * 
   * For example, given the query path "/jcr:root/content/", including descending paths would result
   * in the following: "/jcr:root/content//".
   * 
   * @return An updated JcrQuery object.
   */
  public JcrQuery includingDescendantPaths() {
    this.includingDescendantPaths = true;
    return this;
  }

  /**
   * Specifies a list of predicate conditions, to match against node properties.
   * 
   * @param predicates Predicate objects to use in the query
   * @return An updated JcrQuery object.
   * 
   * @see Predicate#all(Predicate...)
   * @see Property#eq(Object)
   */
  public JcrQuery with(Predicate... predicates) {
    this.predicates.addAll(Arrays.asList(predicates));
    return this;
  }

  /**
   * <p>
   * Specifies the ordering of the result nodes according to the natural ordering of the values of a
   * property of the result nodes. The default ordering is "ascending".
   * </p>
   * 
   * @param property Name of the property to order on
   * @return An updated JcrQuery object.
   * 
   * @see JcrQuery#orderBy(String, boolean)
   */
  public JcrQuery orderBy(String property) {
    return orderBy(property, true);
  }

  /**
   * <p>
   * Specifies the ordering of the result nodes according to the natural ordering of the values of a
   * property of the result nodes.
   * </p>
   * 
   * <p>
   * Note that multiple orderBy specifiers may be used together, i.e.:
   * <code>query.orderBy("@propertyA").orderBy("@propertyB")</code> will result in the specifier:
   * <code>"order by @propertyA, @propertyB"</code>.
   * </p>
   * 
   * @param property Name of the property to order on
   * @param ascending If true, the order modifier is "ascending" - if false, the order modifier is
   *        "descending"
   * @return An updated JcrQuery object.
   */
  public JcrQuery orderBy(String property, boolean ascending) {
    this.orderings.add(new Order(property, ascending));
    return this;
  }

  @Override
  public String toString() {
    return buildStatement();
  }

  public String buildStatement() {
    StringBuilder builder = new StringBuilder(path);
    if (includingDescendantPaths) {
      builder.append("/");
    }

    if (nodeType != null) {
      // Node type statements look like: "element(nodeName, nodeType)"
      builder.append("element(").append(nodeName).append(", ");
      builder.append(nodeType).append(")");
    } else {
      builder.append(nodeName);
    }

    if (!predicates.isEmpty()) {
      builder.append("[");
      String predicateString = Joiner.on(" and ").join(predicates);
      builder.append(predicateString);
      builder.append("]");
    }

    if (!orderings.isEmpty()) {
      builder.append(" order by ");
      String orderingString = Joiner.on(", ").join(orderings);
      builder.append(orderingString);
    }
    return builder.toString();
  }

  /**
   * Builds the query string and uses the supplied session to execute the query against the
   * repository.
   * 
   * @param session A session with appropriate privileges, which will be used to execute the query
   * @return A NodeIterator object, representing the results of the query.
   * @throws RepositoryException
   */
  @SuppressWarnings("deprecation")
  public NodeIterator execute(Session session) throws RepositoryException {
    QueryManager queryManager = session.getWorkspace().getQueryManager();
    String queryString = buildStatement();
    log.info("Executing Query: {}", queryString);

    Query query = queryManager.createQuery(queryString, Query.XPATH);
    return query.execute().getNodes();
  }

}
