/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.io.File;
import java.util.Set;
import org.apache.log4j.Logger;
import org.bridgedb.IDMapperException;
import org.bridgedb.rdf.reader.StatementReader;
import org.bridgedb.tools.metadata.MetaDataCollection;
import org.bridgedb.tools.metadata.MetaDataSpecification;
import org.bridgedb.tools.metadata.validator.MetaDataSpecificationRegistry;
import org.bridgedb.tools.metadata.validator.ValidationType;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.utils.Reporter;
import org.bridgedb.utils.StoreType;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Christian
 */
public class TestRemoteVoidsAndLinksets {
 
    private static boolean NO_WARNINGS = false;
    private static boolean EXCEPTION_ON_ERROR = false;
    private LinksetLoader loader = new LinksetLoader();
    
    static final Logger logger = Logger.getLogger(TestRemoteVoidsAndLinksets.class);
    
    private void checkVoid(String SourceName) throws BridgeDBException{
        check(SourceName, ValidationType.VOID, EXCEPTION_ON_ERROR);
    }
    
    private void checkMinimum(String SourceName) throws BridgeDBException {
        check(SourceName, ValidationType.LINKSMINIMAL, NO_WARNINGS);
    }
    
    private void check(String SourceName, ValidationType validationType, boolean allowErrors) throws BridgeDBException {
        System.out.println(validationType + "  " + validationType.isLinkset());
        String report = loader.validateAddress(SourceName, StoreType.TEST, validationType, NO_WARNINGS);
        if (report.contains("ERROR")){
            output("Validation error in " + SourceName, allowErrors);
            output(report, allowErrors);
            if (!allowErrors){
                throw new BridgeDBException ("Check failed");
            }
        } else {
            Reporter.println("No problems found with  " + SourceName);
        }
    }
 
    private void output(String text, boolean allowErrors){
        if (allowErrors){
            Reporter.println(text);
        } else {
            Reporter.error(text);
        }
    }
    
    public TestRemoteVoidsAndLinksets() throws IDMapperException{
        checkMinimum("C:/Dropbox/linksets/sample1To2.ttl");
        checkMinimum("https://www.dropbox.com/s/9a5vb5cchjtlxyc/sample1To2.ttl");
        checkVoid("https://github.com/openphacts/ops-platform-setup/blob/master/void/drugbank_void.ttl#db-drugs");
        checkVoid("https://github.com/openphacts/ops-platform-setup/blob/master/void/chebi/chebi93_void.ttl");
        checkVoid("https://github.com/openphacts/ops-platform-setup/blob/master/void/chebi/chebi99_void.ttl");
        loader.load("https://github.com/openphacts/ops-platform-setup/blob/master/void/chebi/ChEBI100VoID.ttl", StoreType.TEST, ValidationType.VOID);
        checkVoid("https://github.com/openphacts/ops-platform-setup/blob/master/void/chebi/has_functional_parentChEBI100Linkset.ttl");
        checkVoid("https://github.com/openphacts/ops-platform-setup/blob/master/void/chebi/has_parent_hydrideChEBI100Linkset.ttl");
        checkVoid("https://github.com/openphacts/Documentation/blob/master/datadesc/examples/chembl-rdf-void.ttl");
        checkVoid("https://github.com/openphacts/Documentation/blob/master/datadesc/examples/chemspider-void.ttl");
//        checkVoid("https://github.com/openphacts/Documentation/blob/master/datadesc/examples/chemspider2chemblrdf-linkset.ttl");
//        checkVoid("https://github.com/openphacts/Documentation/blob/master/datadesc/examples/chemspider2drugbank-linkset.ttl");
 //       checkVoid("ftp://ftp.rsc-us.org/OPS/20130117/void_2013-01-17.ttl#chemSpiderDataset");
     }
        
    public static void main(String[] args) throws IDMapperException {
        TestRemoteVoidsAndLinksets checker = new TestRemoteVoidsAndLinksets();
    }

}