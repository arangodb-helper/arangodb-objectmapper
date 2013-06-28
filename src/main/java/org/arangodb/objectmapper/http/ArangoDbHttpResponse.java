package org.arangodb.objectmapper.http;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;
import org.arangodb.objectmapper.ArangoDb4JException;

/** 
 * Original file from "Java API for CouchDB http://www.ektorp.org"
 * 
 * @author henrik lundgren
 * 
 */

public class ArangoDbHttpResponse  {

	/**
	 * the logger
	 */

	private final static Logger LOG = Logger
			.getLogger(ArangoDbHttpResponse.class);

	/**
	 * an empty HttpEntity (for empty response)
	 */

	private final static HttpEntity NULL_ENTITY = new NullEntity();
	
	/**
	 * the result entity
	 */

	private final HttpEntity entity;
	
	/**
	 * http result status line
	 */

	private final StatusLine status;
	
	/**
	 * the request uri
	 */

	private final String requestURI;
	
	/**
	 * the request
	 */

	private final HttpUriRequest httpRequest;

	public static ArangoDbHttpResponse of(org.apache.http.HttpResponse rsp, HttpUriRequest httpRequest) {
		return new ArangoDbHttpResponse(rsp.getEntity(), rsp.getStatusLine(), httpRequest);
	}
	
	private ArangoDbHttpResponse(HttpEntity e, StatusLine status, HttpUriRequest httpRequest) {
		this.httpRequest = httpRequest;
		this.entity = e != null ? e : NULL_ENTITY;
		this.status = status;
		this.requestURI = httpRequest.getURI().toString();
	}
		
	public int getCode() {
		return status.getStatusCode();
	}

	public String getReason() {
		return status.getReasonPhrase();
	}
	
	public String getRequestURI() {
		return requestURI;
	}
	
	public long getContentLength() {
		return entity.getContentLength();
	}

	public String getContentType() {
		return entity.getContentType().getValue();
	}

	public InputStream getContentAsStream() throws ArangoDb4JException {
		try {
			return new ConnectionReleasingInputStream(entity.getContent());
		} catch (Exception e) {
			throw new ArangoDb4JException(e.getMessage());
		}
	}

	public String getContentAsString() throws ArangoDb4JException {		
		try {
			Reader tempStreamReader = new InputStreamReader(
					entity.getContent());
			int data = tempStreamReader.read();
			StringBuilder tempStringBuilder = new StringBuilder();
			while (data != -1) {
				char tempChar = (char) data;
				tempStringBuilder.append(tempChar);
				data = tempStreamReader.read();
			}

			String result = tempStringBuilder.toString();
			
			LOG.debug("Result-body:  " + result);
			return result;
			
		} catch (IllegalStateException e) {
			e.printStackTrace();
			throw new ArangoDb4JException(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new ArangoDb4JException(e.getMessage());
		}		
	}

	public boolean isSuccessful() {
		return getCode() < 300;
	}

	
	public void releaseConnection() {
		try {
			if (entity.getContent() != null) {
				entity.getContent().close();
			}; 
		} catch (IOException e) {
			// ignore
		}
	}
	
	public void abort() {
		httpRequest.abort();
	}
	
	
	public String toString() {
		return status.getStatusCode() + ":" + status.getReasonPhrase();
	}
	
	private class ConnectionReleasingInputStream extends FilterInputStream {
		
		private ConnectionReleasingInputStream(InputStream src) {
			super(src);
		}

		
		public void close() throws IOException {
			releaseConnection();
		}
		
	}
	
	/**
	 * the empty HttpEntity
	 */
	
	private static class NullEntity implements HttpEntity {

		final static Header contentType = new BasicHeader(HTTP.CONTENT_TYPE, "null");
		final static Header contentEncoding = new BasicHeader(HTTP.CONTENT_ENCODING, "UTF-8");
		
		
		public void consumeContent() throws IOException {
			
		}

		
		public InputStream getContent() throws IOException,
				IllegalStateException {
			return null;
		}

		
		public Header getContentEncoding() {
			return contentEncoding;
		}

		
		public long getContentLength() {
			return 0;
		}

		
		public Header getContentType() {
			return contentType;
		}

		
		public boolean isChunked() {
			return false;
		}

		
		public boolean isRepeatable() {
			return true;
		}

		
		public boolean isStreaming() {
			return false;
		}

		
		public void writeTo(OutputStream outstream) throws IOException {
			throw new UnsupportedOperationException("NullEntity cannot write");
		}
		
	}
}
