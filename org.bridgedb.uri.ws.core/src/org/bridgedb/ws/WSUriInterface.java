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
package org.bridgedb.ws;

import java.util.List;
import org.bridgedb.url.Mapping;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.ws.bean.DataSourceUriSpacesBean;
import org.bridgedb.ws.bean.MappingSetInfoBean;
import org.bridgedb.ws.bean.OverallStatisticsBean;
import org.bridgedb.ws.bean.ProfileBean;
import org.bridgedb.ws.bean.URLBean;
import org.bridgedb.ws.bean.URLExistsBean;
import org.bridgedb.ws.bean.URLSearchBean;
import org.bridgedb.ws.bean.XrefBean;

/**
 *
 * @author Christian
 */
public interface WSUriInterface extends WSCoreInterface{

    public List<Mapping> mapURL(String URL, String profileURL, List<String> targetUriSpace) throws BridgeDBException;

//    public List<Mapping> mapURL(String URL, List<String> targetUriSpace) throws BridgeDBException;
    
    public List<Mapping> mapToURLs(String id, String scrCode, String profileURL, List<String> targetUriSpace) 
            throws BridgeDBException;

    public URLExistsBean URLExists(String URL) throws BridgeDBException;

    public URLSearchBean URLSearch(String text, String limitString) throws BridgeDBException;

    public XrefBean toXref(String URL) throws BridgeDBException;

    public Mapping getMapping(String id) throws BridgeDBException;

    public List<Mapping> getSampleMappings() throws BridgeDBException;

    public OverallStatisticsBean getOverallStatistics() throws BridgeDBException;

    public List<MappingSetInfoBean> getMappingSetInfos(String sourceSysCode, String targetSysCode) throws BridgeDBException;

    public MappingSetInfoBean getMappingSetInfo(String mappingSetId) throws BridgeDBException;

    public DataSourceUriSpacesBean getDataSource(String dataSource) throws BridgeDBException;
    
    public List<ProfileBean> getProfiles() throws BridgeDBException;
    
    public ProfileBean getProfile(String id) throws BridgeDBException;
    
    //public ValidationBean validateString(String info, String mimeTypee, String storeType, String validationType, 
    //        String includeWarnings) throws BridgeDBException;

    public String getSqlCompatVersion() throws BridgeDBException;
     
 }