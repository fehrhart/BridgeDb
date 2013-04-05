// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.linkset.rdf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bridgedb.linkset.LinkSetStore;
import org.bridgedb.rdf.constants.VoidConstants;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.StoreType;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
public class RdfReader implements LinkSetStore{

    private final StoreType storeType;
    
    public RdfReader(StoreType storeType){
        if (storeType == null){
            throw new NullPointerException("StoreType = null");
        }
        this.storeType = storeType;
    }
    
    //@Override
    //public List<String> getLinksetNames() throws BridgeDBException {
    //    return RdfWrapper.getContextNames(storeType);
    //}

    @Override
    public String getLinksetRDF(int linksetId) throws BridgeDBException{
        Resource linkSetGraph = RdfFactory.getLinksetUri(linksetId); 
        return getRDF(linkSetGraph);
    }

    @Override
    public String getVoidRDF(int voidId) throws BridgeDBException{
        Resource voidGraph = RdfFactory.getVoidUri(voidId); 
        return getRDF(voidGraph);
    }

    private String getRDF(Resource resource) throws BridgeDBException {
        RdfWrapper rdfWrapper = null;
        try {
            rdfWrapper = RdfFactory.setupConnection(storeType);
            String result = rdfWrapper.getRDF(resource);
            return result;
        } catch (RDFHandlerException ex) {
            throw new BridgeDBException("Unable to read RDF", ex);
        } finally {
            shutDown(rdfWrapper);
        }
    }

    @Override
    public List<Statement> getStatementsForResource(Resource resource) throws BridgeDBException{
        RdfWrapper rdfWrapper = null;
        try {
            rdfWrapper = RdfFactory.setupConnection(storeType);
            List<Statement> result = rdfWrapper.getStatementList(resource, RdfWrapper.ANY_PREDICATE, RdfWrapper.ANY_OBJECT);
            return result;
        } catch (RDFHandlerException ex) {
            throw new BridgeDBException("Unable to read RDF", ex);
        } finally {
            shutDown(rdfWrapper);
        }
    }
    
    public Set<Statement> getSuperSet(Resource resource) throws BridgeDBException{
        //TODO this will cause endless recursion is two Ids are subsets of each other
        List<Statement> results = new ArrayList<Statement>();
        Set<Resource> allReadyChecked = new HashSet<Resource>();
        RdfWrapper rdfWrapper = null;
        try {
            rdfWrapper = RdfFactory.setupConnection(storeType);       
            return getSuperSet(resource, rdfWrapper, allReadyChecked);
        } catch (RDFHandlerException ex) {
            throw new BridgeDBException("Unable to read RDF", ex);
        } finally {
            shutDown(rdfWrapper);
        }
    }
    
    private Set<Statement> getSuperSet(Resource resource, RdfWrapper rdfWrapper, Set<Resource> allReadyChecked) throws RDFHandlerException {
        Set<Statement> results = new HashSet<Statement>();
        List<Statement> subsetStatements = 
                rdfWrapper.getStatementList(RdfWrapper.ANY_SUBJECT, VoidConstants.SUBSET, resource);
        for (Statement subsetStatement:subsetStatements){
            Resource superResource = subsetStatement.getSubject();
            if (!allReadyChecked.contains(superResource)){
                allReadyChecked.add(superResource);
                results.addAll(
                        rdfWrapper.getStatementList(superResource, RdfWrapper.ANY_PREDICATE, RdfWrapper.ANY_OBJECT));
                results.addAll(getSuperSet(superResource, rdfWrapper, allReadyChecked));
            }
        }
        return results;
    }

    private void shutDown(RdfWrapper rdfWrapper) throws BridgeDBException{
        if (rdfWrapper != null){
            try {
                rdfWrapper.shutdown();
            } catch (RDFHandlerException ex) {
                throw new BridgeDBException ("Error shuting down RDFWrapper ", ex);
            }
        }
    }

}