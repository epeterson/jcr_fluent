package com.bitblossom.jcr_fluent;


/**
 * A Property represents a JCR Property name. A Property provides several convenience methods for
 * building Predicate objects.
 * 
 * @author eli
 */
public class Property {

  private final String property;

  private Property(String property) {
    // XXX: Should the path to a selector property be escaped? I would assume yes, but Jackrabbit
    // ISO9075#encodePath doesn't. Should make integration test to verify.
    this.property = QueryUtils.encodePath(property);
  }

  /**
   * Creates and returns a new Property object. A property must include the identifying character
   * '@'.
   * 
   * @param property A String representing the property name
   * @return A new Property object.
   */
  public static Property property(String property) {
    if (!property.contains("@")) {
      throw new IllegalArgumentException("A property name must contain the '@' character");
    }
    return new Property(property);
  }

  /**
   * Creates and returns a new Predicate object, which will match the given value against all
   * properties of the current node-set. This is equivalent to a "jcr:contains(., value)"
   * restriction.
   * 
   * @return A new Predicate object.
   */
  public static Predicate anyPropertyContains(Object value) {
    Property anyProperty = new Property(".");
    return new Predicate(anyProperty, value, Op.CONTAINS);
  }

  public Predicate eq(Object value) {
    return new Predicate(this, value, Op.EQ);
  }

  public Predicate neq(Object value) {
    return new Predicate(this, value, Op.NEQ);
  }

  public Predicate like(Object value) {
    return new Predicate(this, value, Op.LIKE);
  }

  public Predicate gt(Object value) {
    return new Predicate(this, value, Op.GT);
  }

  public Predicate lt(Object value) {
    return new Predicate(this, value, Op.LT);
  }

  /**
   * Note that single quote, double quote, hyphen and backslash all need to be escaped. The escape
   * indicator is backslash ("\").
   * 
   * @param value The value to search for
   * @return A new Predicate object, representing a jcr:contains() function.
   */
  public Predicate contains(Object value) {
    return new Predicate(this, value, Op.CONTAINS);
  }

  public Predicate exists() {
    return new Predicate(this, null, Op.EXISTS);
  }

  public Predicate nexists() {
    return new Predicate(this, null, Op.NEXISTS);
  }

  @Override
  public String toString() {
    return property;
  }

}
