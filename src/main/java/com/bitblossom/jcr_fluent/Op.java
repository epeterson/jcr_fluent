package com.bitblossom.jcr_fluent;

// EQ, - equal
// NEQ, - not-equal
// LIKE, - jcr:like
// GT, - greater-than
// LT, - less-than
// GTEQ, - greater-than or equal-to
// LTEQ, - less-than or equal-to
// ACTIV, - jcr node activated
// NACTIV - jcr node not activated

/**
 * An operation that can be performed on a JCR property, like '=', '>', '<', 'jcr:like', etc.
 * 
 * @author eli
 */
public enum Op {
  EQ, NEQ, LIKE, GT, LT, GTEQ, LTEQ, ACTIV, NACTIV, CONTAINS
}
