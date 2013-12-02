package org.arangodb.objectmapper;

//////////////////////////////////////////////////////////////////////////////////////////
//
// Object mapper for ArangoDB by triAGENS GmbH Cologne.
//
// Copyright triAGENS GmbH Cologne.
//
//////////////////////////////////////////////////////////////////////////////////////////

import java.util.HashMap;
import java.util.Map;

import org.arangodb.objectmapper.PropertyFilter.Compare;
import org.arangodb.objectmapper.PropertySort.Direction;
import org.arangodb.objectmapper.jackson.ArangoDbDocument;


/**
 * a simple query (only one collection)
 *
 * @author abrandt
 */

public class ArangoDbQuery<T extends ArangoDbDocument> {

    /**
     * the database
     */

    private final Database database;

    /**
     * the class value type
     */

	private final Class<T> valueType;

    /**
     * maximal numbers of results
     */

    private Long limit = null;

    /**
     * numbers of results to skip
     */

    private Long offset = null;

    /**
     * batch size
     */

    private Long batchSize = 1000L;
    
    /**
     * whether or not we ask the server to count the documents in the result
     */

    private boolean count = true;

    /**
     * simple property filters
     */

    private PropertyFilter propertyFilter = new PropertyFilter();

    /**
     * sort property filters
     */

    private PropertySort propertySort = new PropertySort();

    /**
     * constructor
     *
     * @param database    the database
     * @param valueType   the class (to specify collection and returned objects)
     */

    public ArangoDbQuery(final Database database, final Class<T> valueType) {
    	this.database = database;
    	this.valueType = valueType;
    }

    /**
     * Filter by a key value pair
     *
     * @param key         the key
     * @param value       the value
     *
     * @return ArangoDbQuery<T>          the query
     */

    public ArangoDbQuery<T> has(final String key, final Object value) {
    	propertyFilter.has(key, value, PropertyFilter.Compare.EQUAL);
        return this;
    }

    /**
     * Sort by a key with direction
     *
     * @param key         the key
     * @param direction       the direction
     *
     * @return ArangoDbQuery<T>          the query
     */

    public ArangoDbQuery<T> sort(final String key, final Direction direction) {
        propertySort.sort(key, direction);
        return this;
    }

    /**
     * Sort by a key (Ascending Direction)
     *
     * @param key         the key
     *
     * @return ArangoDbQuery<T>          the query
     */

    public ArangoDbQuery<T> sort(final String key) {
        propertySort.sort(key, Direction.ASCENDING);
        return this;
    }

    /**
     * Filter
     *
     * @param key         the key
     * @param value       the value
     * @param compare     a compare function
     *
     * @return ArangoDbQuery<T>          the query
     */

    public <S extends Comparable<S>> ArangoDbQuery<T> has(final String key, final S value, final Compare compare) {
        switch (compare) {
        case EQUAL:
        	propertyFilter.has(key, value, PropertyFilter.Compare.EQUAL);
        	break;
        case NOT_EQUAL:
        	propertyFilter.has(key, value, PropertyFilter.Compare.NOT_EQUAL);
        	break;
        case GREATER_THAN:
        	propertyFilter.has(key, value, PropertyFilter.Compare.GREATER_THAN);
        	break;
        case LESS_THAN:
        	propertyFilter.has(key, value, PropertyFilter.Compare.LESS_THAN);
        	break;
        case GREATER_THAN_EQUAL:
        	propertyFilter.has(key, value, PropertyFilter.Compare.GREATER_THAN_EQUAL);
        	break;
        case LESS_THAN_EQUAL:
        	propertyFilter.has(key, value, PropertyFilter.Compare.LESS_THAN_EQUAL);
        	break;
        }
        return this;
    }

    /**
     * Filter an interval
     *
     * @param key         the key
     * @param startValue  the start value
     * @param endValue    the end value
     *
     * @return ArangoDbQuery<T>          the query
     */

    public <S extends Comparable<S>> ArangoDbQuery<T> interval(final String key, final S startValue, final S endValue) {
    	propertyFilter.has(key, startValue, PropertyFilter.Compare.GREATER_THAN_EQUAL);
    	propertyFilter.has(key, endValue, PropertyFilter.Compare.LESS_THAN);
        return this;
    }


    /**
     * Limit the number of results
     *
     * @param max         the maximum number of results
     *
     * @return  ArangoDbQuery<T>         the query
     */

    public ArangoDbQuery<T> limit(final long max) {
        this.limit = max;
        return this;
    }

    /**
     * Limit the number of results with an offset
     *
     * @param max         the maximum number of results
     * @param offset      the results position to start from
     *
     * @return  ArangoDbQuery<T>         the query
     */

    public ArangoDbQuery<T> limit(final long offset, final long max) {
        this.limit = max;
        this.offset = offset;
        return this;
    }

    /**
     * Set the batch size
     *
     * @param batchSize         the batch size (max. number of documents per roundtrip)
     *
     * @return  ArangoDbQuery<T>         the query
     */

    public ArangoDbQuery<T> batchSize(long batchSize) {
        this.batchSize = batchSize;
        return this;
    }
    
    /**
     * Set the count attribute
     *
     * @param count         whether or not the server is asked to return the number of docs in the result
     *
     * @return  ArangoDbQuery<T>         the query
     */

    public ArangoDbQuery<T> count(boolean count) {
        this.count = count;
        return this;
    }

    /**
     * Get the AQL query as a map
     *
     * @return Map<String, Object>  the map
     */

    public Map<String, Object> getAsMap() {
    	HashMap<String, Object> result = new HashMap<String, Object>();

    	String limitString = (null == limit) ? "" : " LIMIT " + (null == offset ? "" : offset + ", ") + limit;

    	String query = "FOR x IN `" + Database.getCollectionName(valueType) + "` " + propertyFilter.getFilterString() + propertySort.getSortString() + limitString  + " RETURN x";

    	result.put("query", query);
    	result.put("count", count);
    	result.put("batchSize", batchSize);
    	result.put("bindVars", propertyFilter.getBindVars());

    	return result;
    }

    /**
     * Executes the query and returns a cursor (Iterator)
     *
     * @return Cursor<T>  the result iterator
     */

    public Cursor<T> execute() {
    	return new Cursor<T>(database, this);
    }

    /**
     * Returns the class type
     *
     * @return Class<T>  the class type
     */

    public Class<T> getValueType() {
		return valueType;
	}

}
