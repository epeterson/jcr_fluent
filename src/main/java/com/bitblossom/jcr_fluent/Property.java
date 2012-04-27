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
    // XXX: Should the path to a selector property be escaped? Jackrabbit ISO9075#encodePath doesn't
    this.property = QueryUtils.encodePath(property);
  }

  /**
   * Creates and returns a new Property object.
   * 
   * @param property A String representing the property name
   * @return A new Property object.
   */
  public static Property property(String property) {
    return new Property(property);
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

  public Predicate contains(Object value) {
    return new Predicate(this, value, Op.CONTAINS);
  }

  @Override
  public String toString() {
    return property;
  }

}
