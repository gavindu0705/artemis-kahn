package com.artemis.kahn.core;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.ProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;

import javax.net.ssl.SSLContext;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class HttpClientUtils {


    static CloseableHttpClient httpClient = null;

    static {
        try{
            SSLContext sslContext  = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] arg0, String arg1)
                        throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory( sslContext, new String[] { "TLSv1" }, null,
                    new NoopHostnameVerifier());
            Registry registry = RegistryBuilder
                    . create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", sslsf).build();
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(7000);
            cm.setDefaultMaxPerRoute(70);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(10 * 1000)
                    .setSocketTimeout(30 * 1000)
                    .setConnectionRequestTimeout(30 * 1000)
                    .setCircularRedirectsAllowed(false)
                    .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                    .setExpectContinueEnabled(false)
                    .build();

            httpClient = HttpClients.custom()
                    .setConnectionManager(cm)
                    .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
                    .setRedirectStrategy(new LaxRedirectStrategy(){
                        @Override
                        protected URI createLocationURI(String location) throws ProtocolException {
                            location = filterUrl(location);
                            return super.createLocationURI(location);
                        }
                    })
                    .setDefaultRequestConfig(requestConfig)
                    .build();


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String filterUrl(String url){
        try {
            new URI(url);
            return url;
        } catch (URISyntaxException e) {
            if(url.contains("?")){
                String baseUrl = url.substring(0, url.indexOf("?"));
                String paramsUrl = url.substring(url.indexOf("?") + 1);
                List<String> parList = new ArrayList<String>();
                for(String par : paramsUrl.split("&")){
                    String[] pramsPar = par.split("=");
                    if(pramsPar.length == 2){
                        try {
                            parList.add(pramsPar[0] + "=" + URLEncoder.encode(pramsPar[1], "UTF-8"));
                        } catch (UnsupportedEncodingException e1) {
                            e1.printStackTrace();
                        }
                    }

                }
                if(parList.size() > 0){
                    url = baseUrl + "?" + StringUtils.join(parList, "&");
                }


            }else{
                return url;
            }

        }
        return url;
    }

    public static CloseableHttpClient getHttpClient(){
        return httpClient;
    }

    public static void main(String[] args) {
        try {
            new URI("http://bj.5i5j.com/exchange/_%E5%BB%BA%E6%98%8E%E9%87%8C");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
