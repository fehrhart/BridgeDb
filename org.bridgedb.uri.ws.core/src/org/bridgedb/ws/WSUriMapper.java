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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.statistics.ProfileInfo;
import org.bridgedb.url.Mapping;
import org.bridgedb.url.URLMapper;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.ws.bean.DataSourceUriSpacesBean;
import org.bridgedb.ws.bean.MappingSetInfoBean;
import org.bridgedb.ws.bean.MappingSetInfoBeanFactory;
import org.bridgedb.ws.bean.OverallStatisticsBean;
import org.bridgedb.ws.bean.OverallStatisticsBeanFactory;
import org.bridgedb.ws.bean.ProfileBean;
import org.bridgedb.ws.bean.ProfileBeanFactory;
import org.bridgedb.ws.bean.URLBean;
import org.bridgedb.ws.bean.URLSearchBean;
import org.bridgedb.ws.bean.UriSpaceBean;
import org.bridgedb.ws.bean.XrefBean;
import org.bridgedb.ws.bean.XrefBeanFactory;
import org.bridgedb.ws.bean.XrefMapBean;

/**
 *
 * @author Christian
 */
public class WSUriMapper extends WSCoreMapper implements URLMapper{
    
    WSUriInterface uriService;
    private static final ArrayList<String> NO_SYSCODES = null;
    private static final ArrayList<String> NO_URI_PATTERNS = null;
    
    
    public WSUriMapper(WSUriInterface uriService){
        super(uriService);
        this.uriService = uriService;
    }

    @Override
    public Set<Xref> mapID(Xref sourceXref, String profileURL, DataSource... tgtDataSources) throws BridgeDBException {
        ArrayList<String> tgtSysCodes = new ArrayList<String>();
        for (int i = 0 ; i < tgtDataSources.length; i++){
            tgtSysCodes.add(tgtDataSources[i].getSystemCode());
        }
        List<Mapping> beans = uriService.map(sourceXref.getId(), sourceXref.getDataSource().getSystemCode(), 
                profileURL, tgtSysCodes, NO_URI_PATTERNS);
        HashSet<Xref> results = new HashSet<Xref>();
        for (Mapping bean:beans){
           DataSource targetDataSource = DataSource.getBySystemCode(bean.getSourceSysCode());
           Xref targetXref = new Xref(bean.getTargetId(), targetDataSource);
           results.add(targetXref);
        }
        return results;
    }
    
    @Override
    public Set<Mapping> mapFullByDataSource(Xref sourceXref, String profileURL, DataSource... tgtDataSources) 
            throws BridgeDBException {
        ArrayList<String> tgtSysCodes = new ArrayList<String>();
        if (tgtDataSources != null){
            for (DataSource tgtDataSource:tgtDataSources){
                tgtSysCodes.add(tgtDataSource.getSystemCode());
            }
        }
        List<Mapping> beans = uriService.map(sourceXref.getId(), sourceXref.getDataSource().getSystemCode(), 
                profileURL, tgtSysCodes, NO_URI_PATTERNS);
        return new HashSet<Mapping>(beans); 
    }
 
    @Override
    public Set<Mapping> mapFullByUriPattern (Xref sourceXref, String profileURL, UriPattern... tgtUriPatterns)
            throws BridgeDBException {
        ArrayList<String> tgtUriPatternStrings = new ArrayList<String>();
        if (tgtUriPatterns != null){
            for (UriPattern tgtUriPattern:tgtUriPatterns){
                tgtUriPatternStrings.add(tgtUriPattern.getUriPattern());
            }
        }
        List<Mapping> beans = uriService.map(sourceXref.getId(), sourceXref.getDataSource().getSystemCode(), 
                profileURL, NO_URI_PATTERNS, tgtUriPatternStrings);
        return new HashSet<Mapping>(beans); 
    }
 
    @Override
    public Map<String, Set<String>> mapURL(Collection<String> sourceURLs, 
    		String profileURL, String... targetURISpaces) throws BridgeDBException {
        HashMap<String, Set<String>> results = new HashMap<String, Set<String>> ();
        for (String sourceURL:sourceURLs){
            Set<String> urls = mapURL(sourceURL, profileURL, targetURISpaces);
            results.put(sourceURL, urls);
        }
        return results;
    }

    @Override
    public Set<String> mapURL(String sourceURL, String profileURL,
    		String... targetURISpaces) throws BridgeDBException {
        List<Mapping> beans = uriService.map(sourceURL, profileURL, Arrays.asList(targetURISpaces));
        HashSet<String> targetURLS = new HashSet<String>(); 
        for (Mapping bean:beans){
            targetURLS.addAll(bean.getTargetURL());
        }
        return targetURLS;
    }

    @Override
    public Set<String> mapToURLs(Xref xref, String profileURL, String... targetURISpaces) throws BridgeDBException {
        List<Mapping> beans = uriService.mapToURLs(xref.getId(), xref.getDataSource().getSystemCode(), profileURL, 
                Arrays.asList(targetURISpaces));
        HashSet<String> targetURLS = new HashSet<String>(); 
        for (Mapping bean:beans){
            targetURLS.addAll(bean.getTargetURL());
        }
        return targetURLS;
    }

    @Override
    public Set<Mapping> mapToURLsFull(Xref xref, String profileURL, String... targetURISpaces) throws BridgeDBException {
        List<Mapping> beans = uriService.mapToURLs(xref.getId(), xref.getDataSource().getSystemCode(), profileURL, 
                Arrays.asList(targetURISpaces));
        return new HashSet<Mapping>(beans); 
    }

    @Override
    public Map<Xref, Set<String>> mapToURLs(Collection<Xref> srcXrefs, String profileURL, String... targetURISpaces) 
            throws BridgeDBException {
        HashMap<Xref, Set<String>> results = new HashMap<Xref, Set<String>> ();
        for (Xref ref:srcXrefs){
            Set<String> urls = mapToURLs(ref, profileURL, targetURISpaces);
            results.put(ref, urls);
        }
        return results;
     }

    @Override
    public boolean uriExists(String URL) throws BridgeDBException {
        return uriService.URLExists(URL).exists();
    }

    @Override
    public Set<String> urlSearch(String text, int limit) throws BridgeDBException {
        URLSearchBean  bean = uriService.URLSearch(text, "" + limit);
        return bean.getURLSet();
    }

    @Override
    public Set<Mapping> mapURLFull(String sourceURL, String profileURL, String... targetURISpaces) throws BridgeDBException {
        List<Mapping> beans = uriService.mapURL(sourceURL, profileURL, Arrays.asList(targetURISpaces));
        return new HashSet<Mapping>(beans);
    }

    @Override
    public Xref toXref(String URL) throws BridgeDBException {
        XrefBean bean = uriService.toXref(URL);
        return XrefBeanFactory.asXref(bean);
    }

    @Override
    public Mapping getMapping(int id) throws BridgeDBException {
        return uriService.getMapping("" + id);
    }

    @Override
    public List<Mapping> getSampleMapping() throws BridgeDBException {
        return uriService.getSampleMappings();
    }
    
    @Override
    public OverallStatistics getOverallStatistics() throws BridgeDBException {
        OverallStatisticsBean bean = uriService.getOverallStatistics();
        return OverallStatisticsBeanFactory.asOverallStatistics(bean);
    }

    @Override
    public MappingSetInfo getMappingSetInfo(int mappingSetId) throws BridgeDBException {
        MappingSetInfoBean bean = uriService.getMappingSetInfo("" + mappingSetId);
        return MappingSetInfoBeanFactory.asMappingSetInfo(bean);
    }

    @Override
    public List<MappingSetInfo> getMappingSetInfos(String sourceSysCode, String targetSysCode) throws BridgeDBException {
        List<MappingSetInfoBean> beans = uriService.getMappingSetInfos(sourceSysCode, targetSysCode);
        ArrayList<MappingSetInfo> results = new ArrayList<MappingSetInfo>(); 
        for (MappingSetInfoBean bean:beans){
            results.add(MappingSetInfoBeanFactory.asMappingSetInfo(bean));
        }
        return results;  
    }
   
    @Override
    public Set<String> getUriSpaces(String dataSource) throws BridgeDBException {
        DataSourceUriSpacesBean bigBean = uriService.getDataSource(dataSource);
        List<UriSpaceBean> beans = bigBean.getUriSpace(); 
        HashSet<String> results = new HashSet<String>();
        for (UriSpaceBean bean:beans){
            results.add(bean.getUriSpace());
        }
        return results;
    }

	@Override
	public List<ProfileInfo> getProfiles() throws BridgeDBException {
		List<ProfileBean> beans = uriService.getProfiles();
		List<ProfileInfo> results = new ArrayList<ProfileInfo>();
		for (ProfileBean bean:beans) {
			results.add(ProfileBeanFactory.asProfileInfo(bean));
		}
		return results;
	}

	@Override
	public ProfileInfo getProfile(String profileURI)
			throws BridgeDBException {
		ProfileBean profile = uriService.getProfile(profileURI);
		ProfileInfo result = ProfileBeanFactory.asProfileInfo(profile);
		return result;
	}
    
    @Override
    public Set<String> getSourceUriSpace(int mappingSetId) throws BridgeDBException {
        MappingSetInfo info = getMappingSetInfo(mappingSetId);
        return getUriSpaces(info.getSourceSysCode());
    }

    @Override
    public Set<String> getTargetUriSpace(int mappingSetId) throws BridgeDBException {
        MappingSetInfo info = getMappingSetInfo(mappingSetId);
        return getUriSpaces(info.getTargetSysCode());
    }

    @Override
    public int getSqlCompatVersion() throws BridgeDBException {
        return Integer.parseInt(uriService.getSqlCompatVersion());
    }

  }