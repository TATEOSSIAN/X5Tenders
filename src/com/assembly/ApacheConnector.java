package com.assembly;

import java.net.CookieHandler;
import java.net.CookieManager;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class ApacheConnector {
    
    private String cookies;
    private final HttpClient client = HttpClientBuilder.create().build();
    private final String USER_AGENT = Extractor.USER_AGENT;
    private final String INDEX_URL = Extractor.INDEX_URL;
    
    private RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(10000)
            .setRelativeRedirectsAllowed(true)
            .setCircularRedirectsAllowed(true)
            .build();

    public ApacheConnector() {
        
        CookieHandler.setDefault(new CookieManager());
        
        HttpGet request = new HttpGet(INDEX_URL);
        request.setConfig(requestConfig);       
        
    }
    
}
