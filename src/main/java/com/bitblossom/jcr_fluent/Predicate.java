package com.bitblossom.jcr_fluent;

import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;

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

  private String formatValue(Object value) {
    if (value instanceof String) {
      String string = (String) value;
      string = string.replaceAll("'", "''").replaceAll("\"", "\"\"");
      // Other special characters that need escaping?
      return String.format("'%s'", value);
    } else if (value instanceof Date) {
      Calendar cal = Calendar.getInstance();
      cal.setTime((Date) value);
      // Use JAXB to help convert to a valid DateTime String (ISO8601)
      return String.format("xs:dateTime('%s')", DatatypeConverter.printDateTime(cal));
    } else if (value instanceof Calendar) {
      return String.format("xs:dateTime('%s')", DatatypeConverter.printDateTime((Calendar) value));
    } else if (value instanceof Number) {
      return String.format("'%s'", value);
    } else {
      return String.format("'%s'", value);
    }
  }

  @Override
  public String toString() {
    // Type 1
    if (predicates == null) {
      switch (op) {
        case EQ:
          return String.format("%s=%s", property.toString(), formatValue(value));
        case NEQ:
          return String.format("not(%s=%s)", property.toString(), formatValue(value));
        case LIKE:
          return String.format("jcr:like(%s, %s)", property.toString(), formatValue(value));
        case GT:
          return String.format("%s>%s", property.toString(), formatValue(value));
        case LT:
          return String.format("%s<%s", property.toString(), formatValue(value));
        case GTEQ:
          return String.format("%s>=%s", property.toString(), formatValue(value));
        case LTEQ:
          return String.format("%s<=%s", property.toString(), formatValue(value));
        case CONTAINS:
          return String.format("jcr:contains(%s, %s)", property.toString(), formatValue(value));
        case EXISTS:
          return String.format("%s", property.toString());
        default:
          throw new UnsupportedOperationException("Operation not implemented yet: " + op);
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
