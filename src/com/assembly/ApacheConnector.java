package com.assembly;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.nodes.Document;

public class ApacheConnector {

    private static final long serialVersionUID = 0L;
   
    private static final CookieStore HTTPCOOKIESTORE = new BasicCookieStore();
    
    private static final String USER_AGENT          = Extractor.USER_AGENT;
    private static final String INDEX_URL           = Extractor.INDEX_URL;
    private static final String LOGIN_URL           = Extractor.LOGIN_URL;   
    private static final String TENDERS_OVER_URL    = Extractor.TENDERS_OVER_URL;
    private static final String START_TENDERS_URL   = Extractor.START_TENDERS_URL; 
    
    private static final HttpClient CLIENT = buildHttpClient();
    
    private static RequestConfig requestConfig;    

    private String indexPage;
    
    private List<Cookie> mCookies;
    
    private int login() {
        
        int responseCode = 404;
        
        HttpPost post = new HttpPost(LOGIN_URL);
        setPostHeaders(post);
        post.setHeader("Referer", INDEX_URL);
        post.setHeader("Content-Type", 
                "application/x-www-form-urlencoded;charset=windows-1251");
        
        List<NameValuePair> paramList = new ArrayList<NameValuePair>();
        paramList.add(new BasicNameValuePair("Username", "kaskad-ltd"));
        paramList.add(new BasicNameValuePair("Password", "master2019"));
        paramList.add(new BasicNameValuePair("RedirectURL", "/auction/guiding/list_auction/1-start"));
        
        try {
            post.setEntity(new UrlEncodedFormEntity(paramList));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ApacheConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        HttpResponse response = null;        
        try {
            response = CLIENT.execute(post);
            responseCode = response.getStatusLine().getStatusCode();
        } catch (IOException ex) {
            Logger.getLogger(ApacheConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        mCookies = HTTPCOOKIESTORE.getCookies();
        
        return responseCode;        
    }

    public String getIndexPage() {
        return indexPage;
    }

    public ApacheConnector() {
        
        BufferedReader rd = null;
        
        indexPage = "";
        
        try {
            CookieHandler.setDefault(new CookieManager());
            HttpGet request = new HttpGet(INDEX_URL);
            request.setHeader("Accept", "text/html,application/xhtml+xml,"
                    + "application/xml;q=0.9,image/webp,*/*;q=0.8");
            request.setHeader("Accept-Language", "ru-RU,ru;q=0.8,"
                    + "en-US;q=0.6,en;q=0.4");
            request.setHeader("Cache-Control", "max-age=0");
            request.setHeader("Connection", "keep-alive");
            request.setHeader("Host", "tender.x5.ru");
            HttpResponse response = null;
            try {
                response = CLIENT.execute(request);
            } catch (IOException ex) {
                Logger.getLogger(ApacheConnector.class.getName()).log(Level.SEVERE, null, ex);
            }
            int responseCode = response.getStatusLine().getStatusCode();
            rd = new BufferedReader(new InputStreamReader(response
                    .getEntity().getContent()));
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            
	HTTPCOOKIESTORE.getCookies();
            
        indexPage = result.toString();
        } catch (IOException ex) {
            Logger.getLogger(ApacheConnector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedOperationException ex) {
            Logger.getLogger(ApacheConnector.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                rd.close();
            } catch (IOException ex) {
                Logger.getLogger(ApacheConnector.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        login();
        
    }
    
    private static void createRequestConfig() {
        
        requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(10000)
                .setConnectTimeout(50000)
                .setRelativeRedirectsAllowed(true)
                .setCircularRedirectsAllowed(true)
                .setContentCompressionEnabled(true)
                .build();
        
    }    
    
    private static HttpClient buildHttpClient() {
        
        createRequestConfig();
        
        return HttpClientBuilder.create()
                .setUserAgent(USER_AGENT)
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCookieStore(HTTPCOOKIESTORE)
                .setDefaultConnectionConfig(ConnectionConfig.custom().
                        setCharset(Consts.ASCII).build())
                .setMaxConnTotal(100)
                .setMaxConnPerRoute(25)
                .build();
    } 

    private void setPostHeaders(HttpPost post) {
        
        post.setHeader("Accept", "text/html,application/xhtml+xml,"
                + "application/xml;q=0.9,image/webp,*/*;q=0.8");
        post.setHeader("Accept-Language", "ru-RU,ru;q=0.8,"
                + "en-US;q=0.6,en;q=0.4");
        post.setHeader("Cache-Control", "max-age=0");
        post.setHeader("Connection", "keep-alive");
        post.setHeader("Host", "tender.x5.ru");
        
    }

    private void setGetHeaders(HttpGet request) {
        
        request.setHeader("Accept", "text/html,application/xhtml+xml,"
                + "application/xml;q=0.9,image/webp,*/*;q=0.8");
        request.setHeader("Accept-Language", "ru-RU,ru;q=0.8,"
                + "en-US;q=0.6,en;q=0.4");
        request.setHeader("Cache-Control", "max-age=0");
        request.setHeader("Connection", "keep-alive");
        request.setHeader("Host", "tender.x5.ru");
        request.setHeader("Referer", START_TENDERS_URL);
        request.setHeader("Upgrade-Insecure-Requests", "1");
        
    }
    
    String getOverAuctions() throws IOException {
        
        HttpGet request = new HttpGet(TENDERS_OVER_URL);
        setGetHeaders(request);
        
        HttpResponse response = null;
        
        response = CLIENT.execute(request);
        int responseCode = response.getStatusLine().getStatusCode();

        return IOUtils.toString(response.getEntity().getContent(), "windows-1251");
               
    }
    
    String getLotBody(String lotURL) throws IOException {
        
        return fetchHtml(lotURL);
        
    }
    
    String getAuctionBody(String auctionUrl) throws IOException {
        
        return fetchHtml(auctionUrl);
    }    

    private String fetchHtml(String auctionUrl) throws UnsupportedOperationException, IOException {
        String html = "";
        
        HttpGet request = new HttpGet(auctionUrl);
        setGetHeaders(request);
        
        HttpResponse response = null;
        
        response = CLIENT.execute(request);
        int responseCode = response.getStatusLine().getStatusCode();

        return IOUtils.toString(response.getEntity().getContent(), "windows-1251");
    }    

    void downloadAppendix(Document lotDocument, String doc_type, String sURL, 
            String tmpDir, String docName) {
        
        File file = new File(tmpDir, docName);
        
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.addTextBody("lot_doc_name", "");
        entityBuilder.addTextBody("download_req_id", "");
        entityBuilder.addTextBody("doc_dem_type", doc_type);
        
        HttpPost downloadFile = new HttpPost(sURL);
        HttpEntity multipart = entityBuilder.build();
        downloadFile.setEntity(multipart);
        
        HttpEntity responseEntity = null;
        try {
            HttpResponse response = CLIENT.execute(downloadFile);
            responseEntity = response.getEntity();
        } catch (IOException ex) {
            Logger.getLogger(ApacheConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        InputStream content = null;        
        try {
            content = responseEntity.getContent();
        } catch (IOException | UnsupportedOperationException ex) {
            Logger.getLogger(ApacheConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ApacheConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
         
        try {
            IOUtils.copy(content, out);
            content.close();
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(ApacheConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
  
    }
}