package com.bitblossom.jcr_fluent;

/**
 * 
 * @author eli
 *
 */
public class Restrictions {

	private static final String EQ 		= "Equals";
	private static final String NEQ 	= "Not-equals";
	private static final String LIKE	= "Like";
	private static final String GT		= "Greater-than";
	private static final String LT		= "Less-than";
	private static final String GTEQ	= "Greater-than or Equal";
	private static final String LTEQ	= "Less-than or Equal";
	private static final String ACTIV	= "Active";
	private static final String NACTIV	= "Not Active";
	
	private static final String AND		= "And";
	private static final String OR		= "Or";
	
	
	private String op;
	private Restrictions[] restrictions;
	
	private String property;
	private Object value;
	
	private Restrictions(String op, Restrictions ...restrictions) {
		if (restrictions.length < 2) {
			throw new IllegalArgumentException("Not enough restrictions");
		}
		
		this.op = op;
		this.restrictions = restrictions;
	};
	
	private Restrictions(String op, String property, Object value) {
		this.op = op;
		this.property = property;
		this.value = value;
	}
	
	private Restrictions(String op, String property) {
		this.op = op;
		this.property = property;
	}
	
	public static Restrictions and(Restrictions ...restrictions) {
		return new Restrictions(AND, restrictions); 
	}
	
	public static Restrictions or(Restrictions ...restrictions) {
		return new Restrictions(OR, restrictions);
	}
	

	public static Restrictions eq(String property, Object value) {
		return new Restrictions(EQ, property, value);
	}
	
	public static Restrictions neq(String property, Object value) {
		return new Restrictions(NEQ, property, value);
	}
	
	public static Restrictions like(String property, Object value) {
		return new Restrictions(LIKE, property, value);
	}
	
	public static Restrictions gt(String property, Object value) {
		return new Restrictions(GT, property, value);
	}	
	
	public static Restrictions lt(String property, Object value) {
		return new Restrictions(LT, property, value);
	}
	
	public static Restrictions gteq(String property, Object value) {
		return new Restrictions(GTEQ, property, value);
	}
	
	public static Restrictions lteq(String property, Object value) {
		return new Restrictions(LTEQ, property, value);
	}
	
	public static Restrictions isActivated(String property) {
		return new Restrictions(ACTIV, property);
	}
	
	public static Restrictions isNotActivated(String property) {
		return new Restrictions(NACTIV, property);
	}
	
	public String asString() {
		if (restrictions != null) {
			String operationType;
			if (op.equals(AND)) {
				operationType = " and ";
			}
			else if (op.equals(OR)) {
				operationType = " or ";
			}
			else {
				throw new UnsupportedOperationException("Operation not implemented: " + op);
			}
			
			StringBuilder builder = new StringBuilder();
			builder.append(restrictions[0].asString());
			
			int restrictionsIdx = 1;
			while (restrictionsIdx < restrictions.length) {
				builder.append(operationType);
				builder.append(restrictions[restrictionsIdx].asString());
				restrictionsIdx++;
			}
			return builder.toString();
		}
		else {
			if (op.equals(EQ)) {
				return String.format("@%s='%s'", property, value);
			}
			else if (op.equals(NEQ)) {
				return String.format("not @%s='%s'", property, value);
			}
			else if (op.equals(LIKE)) {
				return String.format("jcr:like(@%s, '%s')", property, value);
			}
			else {
				throw new UnsupportedOperationException("Operation not implemented: " + op);
			}
		}
	}
	
}
