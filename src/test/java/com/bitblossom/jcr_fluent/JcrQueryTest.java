package com.bitblossom.jcr_fluent;

import static org.junit.Assert.*;

import org.junit.Test;

public class JcrQueryTest {

	@Test
	public void canChainMoreThanTwoRestrictions() {
		JcrQuery query = new JcrQuery("/jcr:root/content/myapp/")
								.add(Restrictions.and(
										Restrictions.eq("propertyA", "valueA"),
										Restrictions.eq("propertyB", "valueB"),
										Restrictions.eq("propertyC", 42)));
		
		System.out.println(query.buildStatement());
		assertTrue(query.buildStatement().equals(
				"/jcr:root/content/myapp/*[@propertyA='valueA' and @propertyB='valueB' and @propertyC='42']"));
	}
	
}
