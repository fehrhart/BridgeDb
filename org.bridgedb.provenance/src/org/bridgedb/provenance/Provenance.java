package org.bridgedb.provenance;

import java.util.Calendar;
import org.bridgedb.IDMapperException;

/**
 * Warning under development. Will change without notice.
 * 
 * @author Christian
 */
public interface Provenance {
    
    public static int NO_ID_ASSIGNED = -1;

    //I wonder if something with hierarcy works better here
    //For example why not org.semanticweb.owlapi.model.OWLClass
    public String getPredicate() throws IDMapperException;
    
    public String getCreatedBy();

    //I choose long as it can easily be converted to and from java.util.Date, java.sql.Date and various calanders
    public long getCreation();

    public long getUpload();
    
    public boolean isTransative();
    
    //Not sure if int is the correct class here.
    //int is easy as databases can autoincrement.
    //Possibly with a -1 return if no id has been assigned
    public int getId();
    
}
