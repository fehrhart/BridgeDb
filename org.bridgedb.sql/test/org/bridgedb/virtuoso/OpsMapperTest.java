/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.virtuoso;

import org.bridgedb.IDMapperException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.TestSqlFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * This class depends on URLasIDMapperSQLTest having loaded the data.
 * 
 * @author Christian
 */
@Ignore //not doing ops mapper at the moment. Too slow.
public class OpsMapperTest extends org.bridgedb.ops.OpsMapperTest {
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException{

        connectionOk = false;
        SQLAccess sqlAccess = TestSqlFactory.createTestVirtuosoAccess();
        connectionOk = true;
        URLMapperVirtuoso urlMapperVirtuoso = new URLMapperVirtuoso(sqlAccess);
        opsMapper = urlMapperVirtuoso;
    }
            
}
