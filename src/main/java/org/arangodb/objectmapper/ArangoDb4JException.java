package org.arangodb.objectmapper;

//////////////////////////////////////////////////////////////////////////////////////////
//
//Object mapper for ArangoDB by triAGENS GmbH Cologne.
//
//Copyright triAGENS GmbH Cologne.
//
//////////////////////////////////////////////////////////////////////////////////////////

/**
 * ArangoDb exception
 * 
 * @author abrandt
 *
 */

public class ArangoDb4JException extends Exception {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -5301373222706274918L;

	/**
	 * Http status error code 
	 */
	
	private Integer statusCode;

	/**
	 * ArangoDB error number 
	 */
	
	private Integer errorNum;
	
	/**
	 * Constructor
	 * 
	 * @param t       Throwable
     */

	public ArangoDb4JException(Throwable t) {
		super(t);
	}
		
	/**
	 * Constructor
	 * 
	 * @param message       error message
     */

    public ArangoDb4JException(String message) {
        super(message);
        this.statusCode = 0;
        this.errorNum = 0;
    }

	/**
	 * Constructor
	 * 
	 * @param message      error message
	 * @param statusCode   http status error code (> 299)
	 * @param errorNum     ArangoDB error number
     */

    public ArangoDb4JException(String message, Integer statusCode, Integer errorNum) {
        super(message);
        this.statusCode = statusCode;
        this.errorNum = errorNum;
    }
    
    public Integer getErrorNumber () {
    	return this.errorNum;
    }

    public Integer getStatusCode () {
    	return this.statusCode;
    }
    
}
