package com.charles.eakins.hdhomerun.coms;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Initializes the HTTPClient with a pooling client connection manager to
 * allow for MultiThread request executions. Default maxTotal is 20 and the
 * default max per route is 2 per PoolingClinetConnectionManager
 * implementation.
 * @see {@link org.apache.http.impl.conn.PoolingHttpClientConnectionManager()}
 */
public class HTTPClient {
    private static final String CHARSET = ";charset=";
    private static final String UTF8 = "UTF-8";
    private static final Logger LOG = LoggerFactory.getLogger(HTTPClient.class);

    private HttpClient client;
    private static HTTPClient wrapperClient;
    private PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;
    private static final int MAX_TOTAL = 20;
    private static final int MAX_PER_ROUTE = 10;
    private static final long MAX_KEEP_ALIVE = 30;

    private static final String USER_AGENT = "hdhomerun-client";
    private static final String HEADER_USER_AGENT = "User-Agent";
    private static final String HEADER_X_HTTP_CALLER_ID = "X-Http-Caller-Id";
    private static final String HEADER_X_HTTP_REQUEST_ID = "X-Http-Request-Id";
    private String callerId = USER_AGENT;
    private static final CookieStore cookieStore = new BasicCookieStore();
    private static final HttpContext localContext = new BasicHttpContext();

    private HTTPClient() {
        this(MAX_TOTAL, MAX_PER_ROUTE, MAX_KEEP_ALIVE);
    }

    private HTTPClient(int maxTotal, int maxPerRoute, final long maxKeepAlive) {
        this(maxTotal, maxPerRoute, maxKeepAlive, false, 0, null, null);
    }

    private HTTPClient(int maxTotal, int maxPerRoute, final long maxKeepAlive, Boolean useProxy, int proxyPort,
                       String proxyHost, String proxyScheme) {
        HttpHost proxy = null;
        if (useProxy) {
            proxy = new HttpHost(proxyHost, proxyPort, proxyScheme);
        }

        ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                // Honor 'keep-alive' header
                HeaderElementIterator it = new BasicHeaderElementIterator(
                        response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext()) {
                    HeaderElement he = it.nextElement();
                    String param = he.getName();
                    String value = he.getValue();
                    if (value != null && param.equalsIgnoreCase("timeout")) {
                        try {
                            return Long.parseLong(value) * 1000;
                        } catch(NumberFormatException ignore) {
                        }
                    }
                }
                // otherwise keep alive for set number of seconds
                return maxKeepAlive * 1000;
            }
        };
        this.poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        this.poolingHttpClientConnectionManager.setMaxTotal(maxTotal);
        this.poolingHttpClientConnectionManager.setDefaultMaxPerRoute(maxPerRoute);

        RequestConfig.Builder requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY);
        if (useProxy) {
            requestConfig.setMaxRedirects(99).
                    setCircularRedirectsAllowed(true).
                    setRedirectsEnabled(true).
                    setExpectContinueEnabled(true).
                    setProxy(proxy);
        } else {
            requestConfig.setMaxRedirects(99).
                    setCircularRedirectsAllowed(true).
                    setRedirectsEnabled(true).
                    setExpectContinueEnabled(true);
        }
        this.client = HttpClientBuilder.create().setConnectionManager(
                poolingHttpClientConnectionManager).setDefaultRequestConfig(requestConfig.build()

        ).setKeepAliveStrategy(myStrategy).build();

        localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
    }

    /**
     * Static factory to create HTTPWrapperClient. This ensures that we only have
     * one instance of HttpWrapperClient and thus one instance of the HTTPClient.
     * Generally it is recommended to have a single instance of HttpClient per
     * communication component or even per application. It is highly recommended
     * to explicitly shut down the multithreaded connection manager prior to
     * disposing the HttpClient instance. This will ensure proper closure of all
     * HTTP connections in the connection pool.
     *
     * @return an instance of HTTPWrapperClient
     */
    public static synchronized HTTPClient getClient() {
        if (wrapperClient == null) {
            wrapperClient = new HTTPClient();
        }
        return wrapperClient;
    }

    /**
     * Initializes the HTTPClient with a pooling client connection manager to
     * allow for MultiThreaded request executions.
     * @see this.getClient()
     * @param connMaxTotal maximum limit of connections in total
     * @param connMaxPerRoute maximum limit of connections on a per route basis
     * @param connMaxKeepAlive maximum time to keep connection alive
     *
     * @return an instance of HTTPWrapperClient
     */
    public static synchronized HTTPClient getClient(int connMaxTotal, int connMaxPerRoute, long connMaxKeepAlive)
    {
        if (wrapperClient == null) {
            wrapperClient = new HTTPClient(connMaxTotal, connMaxPerRoute, connMaxKeepAlive);
        }
        return wrapperClient;
    }
    /**
     * Initializes the HTTPClient with a pooling client connection manager to
     * allow for MultiThreaded request executions.
     * @see this.getClient()
     * @param connMaxTotal maximum limit of connections in total
     * @param connMaxPerRoute maximum limit of connections on a per route basis
     * @param connMaxKeepAlive maximum time to keep connection alive
     * @param useProxy use local proxy
     * @param proxyPort port local proxy running on
     *
     * @return an instance of HTTPWrapperClient
     */
    public static synchronized HTTPClient getClient(int connMaxTotal, int connMaxPerRoute, long connMaxKeepAlive,
                                                           boolean useProxy, int proxyPort,String proxyHost,
                                                           String proxyScheme)
    {
        if (wrapperClient == null) {
            wrapperClient = new HTTPClient(connMaxTotal, connMaxPerRoute, connMaxKeepAlive, useProxy, proxyPort,
                    proxyHost, proxyScheme);
        }
        return wrapperClient;
    }

    public CookieStore getCookies() {
        return cookieStore;
    }

    /**
     * Does a GET to a webservice
     *
     * @param URL path to webservice in a String
     * @return HttpResponse
     * @throws org.apache.http.client.ClientProtocolException
     * @throws java.io.IOException
     */
    public HttpResponse httpGet(String URL) throws ClientProtocolException, IOException {
        return httpGet(URL, "", "");
    }

    /**
     * httpGet method
     *
     * @param URL path to webservice in a String
     * @param header add header to request, takes a string, must be a valid header
     * @param headerValue value of the header, takes a string
     * @return HttpResponse
     * @throws org.apache.http.client.ClientProtocolException
     * @throws java.io.IOException
     * @throws org.apache.http.conn.ConnectTimeoutException
     */
    public HttpResponse httpGet(String URL, String header, String headerValue)
            throws ClientProtocolException, IOException, ConnectTimeoutException {
        LOG.debug("URL : {}", URL);
        HttpGet httpget = new HttpGet(URL);
        String requestId = UUID.randomUUID().toString();
        httpget.addHeader(HEADER_USER_AGENT, USER_AGENT);
        httpget.addHeader(HEADER_X_HTTP_CALLER_ID, callerId);
        httpget.addHeader(HEADER_X_HTTP_REQUEST_ID, requestId);

        if (StringUtils.isNotEmpty(header) && StringUtils.isNotEmpty(headerValue)) {
            httpget.addHeader(header, headerValue);
        }
        HttpResponse response = client.execute(httpget, localContext);
        LOG.debug("return: {}", response);
        return response;
    }

    /**
     * setCallerId method allows you to set the 'X-Http-Caller-Id' header to a custom caller id default is 'dtss-test'
     *
     * @param callerId
     */
    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    /**
     * clearCookies method clears all cookies
     */
    public void clearCookies() {
        LOG.debug("clearing cookies...");
        cookieStore.clear();
    }

    /**
     * @see {@link org.apache.http.impl.conn.PoolingHttpClientConnectionManager#closeExpiredConnections()}
     */
    public void closeExpiredConnections() {
        poolingHttpClientConnectionManager.closeExpiredConnections();
    }

    /**
     * @see {@link org.apache.http.impl.conn.PoolingHttpClientConnectionManager#close()}
     */
    public void close() {
        poolingHttpClientConnectionManager.close();
    }

    /**
     * @see {@link org.apache.http.impl.conn.PoolingHttpClientConnectionManager#closeIdleConnections(long, java.util.concurrent.TimeUnit)}
     * @param idleTimeout
     * @param tunit
     */
    public void closeIdleConnections(long idleTimeout, TimeUnit tunit){
        poolingHttpClientConnectionManager.closeIdleConnections(idleTimeout, tunit);
    }

    /**
     * Reads the response out to String
     */
    public String readResponse(HttpResponse response) throws ParseException, IOException {
        String responseEntity = EntityUtils.toString(response.getEntity());
        LOG.debug("readResponse entity : {}", responseEntity);
        return responseEntity;
    }
}
