package org.arangodb.objectmapper.http;

import java.io.InputStream;

import org.arangodb.objectmapper.ArangoDb4JException;

/** 
 * Original file from "Java API for CouchDB http://www.ektorp.org"
 * 
 * @author henrik lundgren
 * 
 */

public class RestHandler {

	private final ArangoDbHttpClient client;

	public RestHandler(ArangoDbHttpClient client) {
		this.client = client;
	}

	public <T> T get(String path, ResponseCallback<T> callback) throws ArangoDb4JException {
		ArangoDbHttpResponse hr = client.get(path);
		return handleResponse(callback, hr);
	}

	public void put(String path) throws ArangoDb4JException {
		handleVoidResponse(client.put(path));
	}

	public <T> T put(String path, String content, ResponseCallback<T> callback) throws ArangoDb4JException {
		return handleResponse(callback, client.put(path, content));
	}
	
	public void put(String path, String content) throws ArangoDb4JException {
		handleVoidResponse(client.put(path, content));
	}

	public void put(String path, InputStream data, String contentType,
			long contentLength) throws ArangoDb4JException {
		handleVoidResponse(client.put(path, data, contentType, contentLength));
	}

	public <T> T put(String path, InputStream data, String contentType,
			long contentLength, ResponseCallback<T> callback) throws ArangoDb4JException {
		return handleResponse(callback, client.put(path, data, contentType, contentLength));
	}

	public <T> T post(String path, String content, ResponseCallback<T> callback) throws ArangoDb4JException {
		return handleResponse(callback, client.post(path, content));
	}

	public <T> T post(String path, InputStream content, ResponseCallback<T> callback) throws ArangoDb4JException {
		return handleResponse(callback, client.post(path, content));
	}
	
	public <T> T delete(String path, ResponseCallback<T> callback) throws ArangoDb4JException {
		return handleResponse(callback, client.delete(path));
	}
	
	public void delete(String path) throws ArangoDb4JException {
		handleVoidResponse(client.delete(path));
	}

	public <T> T head(String path, ResponseCallback<T> callback) throws ArangoDb4JException {
		return handleResponse(callback, client.head(path));
	}
	
	private void handleVoidResponse(ArangoDbHttpResponse hr) throws ArangoDb4JException {
		if (hr == null)
			return;
		try {
			if (!hr.isSuccessful()) {
				new ResponseCallback<Void>().error(hr);
			}
		} finally {
			hr.releaseConnection();
		}
	}
	
	private <T> T handleResponse(ResponseCallback<T> callback, ArangoDbHttpResponse hr) throws ArangoDb4JException {
		try {
			return hr.isSuccessful() ? callback.success(hr) : callback.error(hr);
		} catch (ArangoDb4JException e) {
			throw e;
		} finally {
			hr.releaseConnection();
		}
	}
}
