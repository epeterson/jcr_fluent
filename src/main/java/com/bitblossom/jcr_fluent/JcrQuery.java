package com.bitblossom.jcr_fluent;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

/**
 * new XPathQueryBuilder("/jcr:root/content/myapp")
 * 		.allPathsBelow()			// "//"
 * 		.element("jcr:content")		// "//jcr:content"
 * 		.propertyExists("jcr:title")		// "//jcr:content[@jcr:title]"
 * 		.propertyEqual("jcr:authoredBy", "eli")	// "//jcr:content[@jcr:title and @jcr:authoredBy="eli"]
 * 		.propertyLike("jcr:something", "value")		// "jcr:like(@jcr:something, "value")
 * 		.propertyNotEqual()
 * 		.or()
 * 		.and()
 * 
 * 		.isNotActivated()
 * 		.propertyLessThan(propertyName, lessThan)
 * 		.propertyLessThan(propertyName, lessThan, DateFormat)
 * 		.propertyGreaterThan()
 * 
 * @author eli
 *
 */
public class JcrQuery {
	
	private String path = "/";
	private String element = "*";
	
	private boolean allPathsBelow = false;
	
	private List<Restrictions> restrictions = new ArrayList<Restrictions>();
	private List<Order> orderings = new ArrayList<Order>();

	public JcrQuery(String path) {
		//TODO: Make sure that path fits a specific format (trailing-leading slashes, etc)
		this.path = path;
	}

	public JcrQuery allPathsBelow() {
		this.allPathsBelow = true;
		return this;
	}
	
	public JcrQuery element(String element) {
		this.element = element;
		return this;
	}
	
	
	public JcrQuery add(Restrictions restriction) {
		this.restrictions.add(restriction);
		return this;
	}
	
	public JcrQuery addOrder(Order order) {
		throw new UnsupportedOperationException("Not implemented");
	}
	
	
	public String buildStatement() {
		StringBuilder builder = new StringBuilder(path);
		if (allPathsBelow) {
			builder.append("/");
		}
		
		builder.append(element);
		if (!restrictions.isEmpty()) {
			builder.append("[");
			builder.append(restrictions.get(0).asString());
			
			int restrictionsIdx = 1;
			while (restrictionsIdx < restrictions.size()) {
				builder.append(" and ");
				builder.append(restrictions.get(restrictionsIdx).asString());
				restrictionsIdx++;
			}
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
