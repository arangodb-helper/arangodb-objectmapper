package org.arangodb.objectmapper.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DecompressingHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.log4j.Logger;
import org.arangodb.objectmapper.ArangoDb4JException;

/**
 * Original file from "Java API for CouchDB http://www.ektorp.org"
 * 
 * @author henrik lundgren
 * 
 */

public class ArangoDbHttpClient {

	/**
	 * http default character set
	 */

	private static final String DEFAULT_CHARSET = "UTF-8";

	/**
	 * http mime type
	 */

	private static final String MIME_TYPE_JSON = "application/json";
	
	/**
	 * apache http client
	 */

	private final org.apache.http.client.HttpClient client;
	
	/**
	 * the logger
	 */

	private final static Logger LOG = Logger.getLogger(ArangoDbHttpClient.class);

        private String apiCompatibility;
	
	/**
	 * constructor
	 */

	public ArangoDbHttpClient(org.apache.http.client.HttpClient hc) {
		this.client = hc;
	}

        public void setApiCompatibility(String s) {
                apiCompatibility = s;
        }
        
        public void setApiCompatibility(int s) {
                apiCompatibility = String.valueOf(s);
        }

	public ArangoDbHttpResponse post(String uri, String content) throws ArangoDb4JException {
		return executeWithBody(new HttpPost(uri), content);
	}

	public ArangoDbHttpResponse post(String uri, InputStream content) throws ArangoDb4JException {
		InputStreamEntity e = new InputStreamEntity(content, -1);
		e.setContentType(MIME_TYPE_JSON);
		HttpPost post = new HttpPost(uri);
		post.setEntity(e);
		return executeRequest(post);
	}

	public ArangoDbHttpResponse patch(String uri, String content) throws ArangoDb4JException {
		return executeWithBody(new HttpPatch(uri), content);
	}

	public ArangoDbHttpResponse patch(String uri, InputStream content) throws ArangoDb4JException {
		InputStreamEntity e = new InputStreamEntity(content, -1);
		e.setContentType(MIME_TYPE_JSON);
		HttpPatch patch = new HttpPatch(uri);
		patch.setEntity(e);
		return executeRequest(patch);
	}

	public ArangoDbHttpResponse get(String uri) throws ArangoDb4JException {
		return executeRequest(new HttpGet(uri));
	}

	public ArangoDbHttpResponse get(String uri, Map<String, String> headers) throws ArangoDb4JException {
		return executeRequest(new HttpGet(uri), headers);
	}

	public ArangoDbHttpResponse delete(String uri) throws ArangoDb4JException {
		return executeRequest(new HttpDelete(uri));
	}

	public ArangoDbHttpResponse put(String uri, String content) throws ArangoDb4JException {
		return executeWithBody(new HttpPut(uri), content);
	}

	public ArangoDbHttpResponse put(String uri) throws ArangoDb4JException {
		return executeRequest(new HttpPut(uri));
	}

	public ArangoDbHttpResponse put(String uri, InputStream data, String contentType,
			long contentLength) throws ArangoDb4JException {
		InputStreamEntity e = new InputStreamEntity(data, contentLength);
		e.setContentType(contentType);

		HttpPut hp = new HttpPut(uri);
		hp.setEntity(e);
		return executeRequest(hp);
	}

	public ArangoDbHttpResponse head(String uri) throws ArangoDb4JException {
		return executeRequest(new HttpHead(uri));
	}

	private ArangoDbHttpResponse executeWithBody(
			HttpEntityEnclosingRequestBase request, String content) throws ArangoDb4JException {
		
		LOG.debug("Request-body: " + content);
		try {
			request.setEntity(new StringEntity(content, DEFAULT_CHARSET));
		} catch (UnsupportedCharsetException e) {
			LOG.error(DEFAULT_CHARSET + " is not supported");
		}
		request.setHeader(new BasicHeader("Content-Type", MIME_TYPE_JSON));

		return executeRequest(request);
	}


	private ArangoDbHttpResponse executeRequest(HttpRequestBase request, Map<String, String> headers) throws ArangoDb4JException {
		for(Map.Entry<String, String> header : headers.entrySet()) {
			request.setHeader(header.getKey(), header.getValue());
		}
		return executeRequest(request);
	}

	private ArangoDbHttpResponse executeRequest(HttpUriRequest request) throws ArangoDb4JException {
		org.apache.http.HttpResponse rsp;
		
		try {
                        if (apiCompatibility != null) {
		                request.setHeader("X-Arango-Version", apiCompatibility);	
                        }
			rsp = client.execute((HttpHost)client.getParams().getParameter(ClientPNames.DEFAULT_HOST), request);
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new ArangoDb4JException(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new ArangoDb4JException(e.getMessage());
		}				

		LOG.debug("Request:" + request.getMethod() + " " + request.getURI() + " -> " +
				rsp.getStatusLine().getStatusCode() + " " +  rsp.getStatusLine().getReasonPhrase());
		return ArangoDbHttpResponse.of(rsp, request);
	}
	
	public void shutdown() {
		client.getConnectionManager().shutdown();
	}

	/**
	 * Helper class
	 */

	public static class Builder {
		String host = "localhost";
		int port = 8529;
		int maxConnections = 20;
		int connectionTimeout = 3000;
		int socketTimeout = 10000;
		ClientConnectionManager conman;
		int proxyPort = -1;
		String proxy = null;

		boolean enableSSL = false;
		boolean relaxedSSLSettings = false;
		SSLSocketFactory sslSocketFactory;

		String username;
		String password;

		boolean cleanupIdleConnections = true;
		boolean useExpectContinue = false;
		boolean staleConnectionCheck = false;
		boolean caching = true;
		boolean compression; // Default is false;
		long keepAliveTimeout = 90;
		int maxObjectSizeBytes = 8192;
		int maxCacheEntries = 1000;

		public Builder url(String s) throws MalformedURLException {
			if (s == null) return this;
			return this.url(new URL(s));
		}
		/**
		 * Will set host, port and possible enables SSL based on the properties if the supplied URL.
		 * This method overrides the properties: host, port and enableSSL. 
		 * @param url
		 * @return the builder
		 */
		public Builder url(URL url){
			this.host = url.getHost();
			this.port = url.getPort();
			if (url.getUserInfo() != null) {
				String[] userInfoParts = url.getUserInfo().split(":");
				if (userInfoParts.length == 2) {
					this.username = userInfoParts[0];
					this.password = userInfoParts[1];
				}
			}
			enableSSL("https".equals(url.getProtocol()));
			if (this.port == -1) {
				if (this.enableSSL) {
					this.port = 443;
				} else {
					this.port = 80;
				}
			}
			return this;
		}
		
		public Builder host(String s) {
			host = s;
			return this;
		}

		public Builder proxyPort(int p) {
			proxyPort = p;
			return this;
		}

		public Builder proxy(String s) {
			proxy = s;
			return this;
		}
		
		/**
		 * Controls if the http client should send Accept-Encoding: gzip,deflate
		 * header and handle Content-Encoding responses. This enable compression
		 * on the server; although not supported natively by CouchDB, you can
		 * use a reverse proxy, such as nginx, in front of CouchDB to achieve
		 * this.
		 * <p>
		 * Disabled by default (for backward compatibility).
		 * 
		 * @param b
		 * @return This builder
		 */
		public Builder compression(boolean b){
			compression = b;
			return this;
		}
		/**
		 * Controls if the http client should cache response entities.
		 * Default is true.
		 * @param b
		 * @return the builder
		 */
		public Builder caching(boolean b) {
			caching = b;
			return this;
		}
		
		public Builder maxCacheEntries(int m) {
			maxCacheEntries = m;
			return this;
		}
		public Builder maxObjectSizeBytes(int m) {
			maxObjectSizeBytes = m;
			return this;
		}

		public ClientConnectionManager configureConnectionManager(
				HttpParams params) throws ArangoDb4JException {
			if (conman == null) {
				SchemeRegistry schemeRegistry = new SchemeRegistry();
				schemeRegistry.register(configureScheme());

				PoolingClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);
				cm.setMaxTotal(maxConnections);
				cm.setDefaultMaxPerRoute(maxConnections);
				conman = cm;
			}

			if (cleanupIdleConnections) {
				IdleConnectionMonitor.monitor(conman);
			}
			return conman;
		}

		private Scheme configureScheme() throws ArangoDb4JException {
			if (enableSSL) {
				try {
					if (sslSocketFactory == null) {
						SSLContext context = SSLContext.getInstance("TLS");

						if (relaxedSSLSettings) {
							context.init(
									null,
									new TrustManager[] { new X509TrustManager() {
										public java.security.cert.X509Certificate[] getAcceptedIssuers() {
											return null;
										}

										public void checkClientTrusted(
												java.security.cert.X509Certificate[] certs,
												String authType) {
										}

										public void checkServerTrusted(
												java.security.cert.X509Certificate[] certs,
												String authType) {
										}
									} }, null);
						} else {
							context.init(null, null, null);
						}

						sslSocketFactory = relaxedSSLSettings ? new SSLSocketFactory(context, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER) : new SSLSocketFactory(context);

					}
					return new Scheme("https", port, sslSocketFactory);
                } catch (Exception e) {
					throw new ArangoDb4JException(e.getMessage());
				}
			} else {
				return new Scheme("http", port, PlainSocketFactory.getSocketFactory());
			}
		}

		public org.apache.http.client.HttpClient configureClient() throws ArangoDb4JException {
			HttpParams params = new BasicHttpParams();
                        params.setParameter(HttpConnectionParams.STALE_CONNECTION_CHECK, staleConnectionCheck);
                        params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, useExpectContinue);
                        params.setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, connectionTimeout);
                        params.setParameter(HttpConnectionParams.SO_TIMEOUT, socketTimeout);
                        params.setParameter(HttpConnectionParams.TCP_NODELAY, true);
                        params.setParameter(HttpConnectionParams.SO_KEEPALIVE, false); // keep-alive on TCP level

                        ConnectionKeepAliveStrategy customKeepAliveStrategy = new ConnectionKeepAliveStrategy() {
                          public long getKeepAliveDuration(org.apache.http.HttpResponse response, org.apache.http.protocol.HttpContext context) {
                            return keepAliveTimeout * 1000;
                          }
                        };
                        
                        String protocol = "http";

                        if (enableSSL) {
                          protocol = "https";
                        }

			params.setParameter(ClientPNames.DEFAULT_HOST, new HttpHost(host, port, protocol));
			if (proxy != null) {
				params.setParameter(ConnRoutePNames.DEFAULT_PROXY,
						new HttpHost(proxy, proxyPort, protocol));
			}
			ClientConnectionManager connectionManager = configureConnectionManager(params);
			DefaultHttpClient client = new DefaultHttpClient(connectionManager, params);
			if (username != null && password != null) {
				client.getCredentialsProvider().setCredentials(
						new AuthScope(host, port, AuthScope.ANY_REALM),
						new UsernamePasswordCredentials(username, password));
				client.addRequestInterceptor(
						new PreemptiveAuthRequestInterceptor(), 0);
			}
                        client.setKeepAliveStrategy(customKeepAliveStrategy);
			
			if (compression) {
				return new DecompressingHttpClient(client);
			}
			return client;
		}

		public Builder port(int i) {
			port = i;
			return this;
		}

		public Builder username(String s) {
			username = s;
			return this;
		}

		public Builder password(String s) {
			password = s;
			return this;
		}

		public Builder maxConnections(int i) {
			maxConnections = i;
			return this;
		}

		public Builder connectionTimeout(int i) {
			connectionTimeout = i;
			return this;
		}

		public Builder socketTimeout(int i) {
			socketTimeout = i;
			return this;
		}

		/**
		 * If set to true, a monitor thread will be started that cleans up idle
		 * connections every 30 seconds.
		 * 
		 * @param b
		 * @return the builder
		 */
		public Builder cleanupIdleConnections(boolean b) {
			cleanupIdleConnections = b;
			return this;
		}

		/**
		 * Bring your own Connection Manager. If this parameters is set, the
		 * parameters port, maxConnections, connectionTimeout and socketTimeout
		 * are ignored.
		 * 
		 * @param cm
		 * @return the builder
		 */
		public Builder connectionManager(ClientConnectionManager cm) {
			conman = cm;
			return this;
		}

		/**
		 * Set to true in order to enable SSL sockets. Note that the CouchDB
		 * host must be accessible through a https:// path Default is false.
		 * 
		 * @param b
		 * @return the builder
		 */
		public Builder enableSSL(boolean b) {
			enableSSL = b;
			return this;
		}

		/**
		 * Bring your own SSLSocketFactory. Note that schemeName must be also be
		 * configured to "https". Will override any setting of
		 * relaxedSSLSettings.
		 * 
		 * @param f
		 * @return the builder
		 */
		public Builder sslSocketFactory(SSLSocketFactory f) {
			sslSocketFactory = f;
			return this;
		}

		/**
		 * If set to true all SSL certificates and hosts will be trusted. This
		 * might be handy during development. default is false.
		 * 
		 * @param b
		 * @return the builder
		 */
		public Builder relaxedSSLSettings(boolean b) {
			relaxedSSLSettings = b;
			return this;
		}

		public Builder useExpectContinue(boolean b) {
			useExpectContinue = b;
			return this;
		}
		
                public Builder staleConnectionCheck(boolean b) {
			staleConnectionCheck = b;
			return this;
		}
                
                public Builder keepAliveTimeout(long t) {
		        keepAliveTimeout = t;
			return this;
		}

		public ArangoDbHttpClient build() throws ArangoDb4JException {
			org.apache.http.client.HttpClient client = configureClient();
			return new ArangoDbHttpClient(client);
		}

	}

}
