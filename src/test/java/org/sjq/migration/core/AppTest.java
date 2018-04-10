package org.sjq.migration.core;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
    
    public void testSet(){
    	Long l1=new Long(1L);
    	Long l2=new Long(1L);
    	
    	Set<Long> s=new HashSet<>();
    	System.out.println(s.add(l1));
    	System.out.println(s.add(l2));
    	System.out.println(s.size());
    }
}
