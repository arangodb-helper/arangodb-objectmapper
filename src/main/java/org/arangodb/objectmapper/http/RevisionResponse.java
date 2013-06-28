package org.arangodb.objectmapper.http;

//////////////////////////////////////////////////////////////////////////////////////////
//
//Object mapper for ArangoDB by triAGENS GmbH Cologne.
//
//Copyright triAGENS GmbH Cologne.
//
//////////////////////////////////////////////////////////////////////////////////////////

import org.arangodb.objectmapper.jackson.ArangoDbDocument;

public class RevisionResponse extends ArangoDbDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8476546130366398053L;
	
	/**
	 * error
	 */

	private Boolean error;

	
	public RevisionResponse () {
		super();
	}
	
	public Boolean getError() {
		return error;
	}

	public void setError(Boolean error) {
		this.error = error;
	}
}
