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

public class PropertyFilter {
	
	/*
	 * compare functions
	 */
	
    public enum Compare {EQUAL, NOT_EQUAL, GREATER_THAN, GREATER_THAN_EQUAL, LESS_THAN, LESS_THAN_EQUAL};
	
    private List<PropertyContainer> propertyContainers = new ArrayList<PropertyContainer>();
    
    public PropertyFilter has(final String key, final Object value, final Compare compare) {
        this.propertyContainers.add(new PropertyContainer(key, value, compare));
        return this;
    }

    public String getFilterString () {
    	if (propertyContainers.isEmpty()) {
    		return "";
    	}
    	
    	StringBuffer sb = new StringBuffer();
    	sb.append(" FILTER ");
    	    	
    	int i = 1; 
    	
    	for (final PropertyContainer container : propertyContainers) {
    		if (i > 1) {
        		sb.append("&& ");
    		}
    		
    		sb.append(" x.`");
    		sb.append(container.key);
    		sb.append("` ");
    		
            switch (container.compare) {
            case EQUAL:
            	sb.append("==");
            	break;
            case NOT_EQUAL:
            	sb.append("!=");
            	break;
            case GREATER_THAN:
            	sb.append(">");
            	break;
            case LESS_THAN:
            	sb.append("<");
            	break;
            case GREATER_THAN_EQUAL:
            	sb.append(">=");
            	break;
            case LESS_THAN_EQUAL:
            	sb.append("<=");
            	break;
            }

    		sb.append(" @value" +  i++ + " ");
    	}
    	
    	return sb.toString();
    }

    public Map<String, Object> getBindVars () {
    	
    	Map<String, Object> result = new HashMap<String, Object>();    	
    	int i = 1;     	
    	for (final PropertyContainer container : propertyContainers) {
    		result.put("value" +  i++, container.value);
    	}
    	
    	return result;
    }
    
    private class PropertyContainer {
        public String key;
        public Object value;
        public Compare compare;

        public PropertyContainer(final String key, final Object value, final Compare compare) {
            this.key = key.replace("\"", "\\\"");
            this.value = value;
            this.compare = compare;
        }
    }

}
