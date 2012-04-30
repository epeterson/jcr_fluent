package com.bitblossom.jcr_fluent;

/**
 * An Order represents an ordering of result nodes.
 * 
 * @author eli
 */
public class Order {

  private boolean ascending = true;
  private final String property;

  public Order(String property) {
    this.property = property;
  }

  public Order(String property, boolean ascending) {
    this.property = property;
    this.ascending = ascending;
  }

  @Override
  public String toString() {
    String direction = ascending ? "ascending" : "descending";
    return String.format("%s %s", property, direction);
  }

}
