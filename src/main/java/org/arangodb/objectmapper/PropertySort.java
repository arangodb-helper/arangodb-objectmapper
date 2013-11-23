package org.arangodb.objectmapper;

//////////////////////////////////////////////////////////////////////////////////////////
//
//Object mapper for ArangoDB by triAGENS GmbH Cologne.
//
//Copyright triAGENS GmbH Cologne.
//
//////////////////////////////////////////////////////////////////////////////////////////

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertySort {
	
	/*
	 * compare functions
	 */
	
    public enum Direction {ASCENDING, DESCENDING};
	
    private List<PropertyContainer> propertyContainers = new ArrayList<PropertyContainer>();
    
    public PropertySort sort(final String key, final Direction direction) {
        this.propertyContainers.add(new PropertyContainer(key, direction));
        return this;
    }

    public String getSortString () {
    	if (propertyContainers.isEmpty()) {
    		return "";
    	}
    	
    	StringBuffer sb = new StringBuffer();
    	sb.append(" SORT ");
    	    	
    	int i = 1; 
    	
    	for (final PropertyContainer container : propertyContainers) {
    		if (i > 1) {
        		sb.append(", ");
    		}
    		
    		sb.append(" x.`");
    		sb.append(container.key);
    		sb.append("` ");
    		
            switch (container.direction) {
            case ASCENDING:
            	sb.append("ASC");
            	break;
            case DESCENDING:
            	sb.append("DESC");
            	break;
            }

            i++;
    	}
    	
    	return sb.toString();
    }

    private class PropertyContainer {
        public String key;
        public Direction direction;

        public PropertyContainer(final String key, final Direction direction) {
            this.key = key.replace("\"", "\\\"");
            this.direction = direction;
        }
    }

}
