package com.bitblossom.jcr_fluent;

import com.google.common.base.Joiner;

/**
 * A Predicate represents a statement about a JCR Property that can be true or false. A Predicate
 * can be either a single statement (Property A = 'B') or a grouping of such statements to be
 * evaluated together (Property A = 'B' AND Property C = 'D').
 * 
 * @author eli
 */
public class Predicate {

  // TODO: Split First and Second type of predicate into sub-classes
  // First type of predicate: a property-value comparison
  private final Property property;
  private final Object value;
  private final Op op;

  // Second type of predicate: an aggregation of property-value comparisons
  private final boolean conjunction;
  private final Predicate[] predicates;

  public Predicate(Property property, Object value, Op op) {
    this.property = property;
    this.value = value;
    this.op = op;

    this.conjunction = true;
    this.predicates = null;
  }

  private Predicate(boolean conjunction, Predicate... predicates) {
    this.property = null;
    this.value = null;
    this.op = null;

    this.conjunction = conjunction;
    this.predicates = predicates;
  }

  /**
   * Evaluate all of the supplied Predicate objects as a conjunction. (i.e. x AND y AND z)
   * 
   * @param predicates Predicate objects to be evaluated
   * @return A new Predicate representing the statement
   */
  public static Predicate all(Predicate... predicates) {
    return new Predicate(true, predicates);
  }

  /**
   * Evaluate all of the supplied Predicate objects as a disjunction (i.e. x OR y OR z)
   * 
   * @param predicates Predicate objects to be evaluated
   * @return A new Predicate representing the statement
   */
  public static Predicate any(Predicate... predicates) {
    return new Predicate(false, predicates);
  }


  @Override
  public String toString() {
    // Type 1
    if (predicates == null) {
      switch (op) {
        case EQ:
          return String.format("@%s='%s'", property.toString(), value);
        case NEQ:
          return String.format("not @%s='%s'", property.toString(), value);
        case LIKE:
          return String.format("jcr:like(@%s, '%s')", property.toString(), value);
        default:
          throw new UnsupportedOperationException("Operation not implemented: " + op);
      }
    }
    // Type 2
    else {
      StringBuilder builder = new StringBuilder();
      builder.append("(");

      String operator = conjunction ? " and " : " or ";
      String predicateString = Joiner.on(operator).join(predicates);
      builder.append(predicateString);

      builder.append(")");
      return builder.toString();
    }
  }
}
