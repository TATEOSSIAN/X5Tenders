package com.assembly;

import com.support.Params;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.jsoup.Connection.*;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Extractor {
    
    private static final String                      INDEX_URL = "https://tender.x5.ru";
    private static final String LOGIN_URL            = INDEX_URL.concat("/user/login/login/");
    private static final String HALLS_URL            = INDEX_URL.concat("/auction/guiding/halls");
    private static final String START_TENDERS_URL    = INDEX_URL.concat("/auction/guiding/list_auction/2-start");
    
    /**
     *  Статус завершенных тендеров 
     */
    private static final String STATUS_ENDED         = "2-over"; 
    private static final String TENDERS_OVER_URL     = INDEX_URL.concat("/auction/guiding/list_auction/")
                                                            .concat(STATUS_ENDED); 
    private static final String VIEW_CMD             = INDEX_URL.concat("/auction/guiding/view_auction");
    
    private static final int TIMEOUT = 20 * 1000;
    private static final long SLEEP_T = 1500L;
    
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64)";
    
    private static final ParsedTenderTable TABLE = ParsedTenderTable.getInstance();

    private static Map<String, String> MAP_LOGINPAGE_COOKIES;
    
    static void getLotData(Lot lot) {
        
        String id = "";        
        URL url = null;
        Response lotForm = null;
        
        String rawNo = lot.getNumber();
        Pattern regex = Pattern.compile("[\\d]+");
        Matcher matcher = regex.matcher(rawNo);
        String match = "";
        
        File fileR = new File("localR.dat");
        try {
            FileInputStream fs = new FileInputStream(fileR);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        while (matcher.find())
        {
            int start = matcher.start();
            int end = matcher.end();
            match = rawNo.substring(start, end);
            break;
        }
        id = match;
        
        try {
            url = new URL(VIEW_CMD.concat("?").concat(Params.ID.toString())
                    .concat("=").concat(id).concat("&").concat(Params.FLD_STATUS.toString())
                    .concat("=").concat(STATUS_ENDED));
        } catch (MalformedURLException ex) {
            Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
        }

        Document lotDocument = null;
                
        try {
            lotForm = Jsoup.connect(url.toString())
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT)
                    .cookies(MAP_LOGINPAGE_COOKIES)
                    .method(Method.POST)
                    .followRedirects(true)
                    .header("Host", "tender.x5.ru")
                    .header("Upgrade-Insecure-Requests", "1")
                    .header("Referer", TENDERS_OVER_URL)
                    //.data(id, VIEW_CMD, inputStream)
                    .execute();
        } catch (IOException ex) {
            Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            lotDocument = lotForm.parse();         
        } catch (IOException ex) {
            Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String name = "";
        Elements el = lotDocument.select("td#td_Name");
        name = el == null ? "" : el.get(0).ownText();
        
        String number = "";
        el = lotDocument.select("span#td_Number");
        number = el == null ? "" : el.get(0).ownText();

        String date_begin_reg = "";
        el = lotDocument.select("td#td_date_begin_reg");
        date_begin_reg = el == null ? "" : el.get(0).ownText();

        String date_begin = "";
        el = lotDocument.select("td#td_date_begin");
        date_begin = el == null ? "" : el.get(0).ownText();

        String finish_time = "";
        el = lotDocument.select("td#td_finish_time");
        finish_time = el == null ? "" : el.get(0).ownText();

        el = lotDocument.select("h2:containsOwn(Конкурсная документация) ~ table");
        Elements appendix = el == null ? null : el.get(0).select("tr:gt(0)");
        if (appendix.size() != 0) {
            
            for (Element e : appendix) {

                Elements res = e.select("tr > td:eq(0)");
                int npp = res.size() == 0 ? 0 : Integer.parseInt(res.get(0).text());
                
                res = e.select("tr > td:eq(1)");
                String fName = res.size() == 0 ? "no_name" : res.get(0).text();
                File file = new File(fName);
                
                res = e.select("tr > td:eq(2)");
                String sDate = res.size() == 0 ? "" : res.get(0).text();
                Date d = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("dd.mm.yyy HH:mm:00");
                try {
                    d = sdf.parse(sDate);
                } catch (ParseException ex) {
                    Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                res = e.select("tr > td:eq(3)");
                String sLength = res.size() == 0 ? "0" : res.get(0).text();
                long l = 0L;
                l = Long.parseLong(sLength);                
                
            }
           
        }   
                
    }

    public Extractor() throws IOException  {
        
        Response resp = Jsoup.connect(INDEX_URL)
                .userAgent(USER_AGENT)
                .followRedirects(true)
                .timeout(TIMEOUT)
                .execute();
        
        try {
            Thread.sleep(SLEEP_T);
        } catch (InterruptedException ex) {
            X5Tenders.logThrownRecord(Extractor.class.getName(), ex);
        }
        
        Response loginPageResponse = Jsoup.connect(LOGIN_URL)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT)
                .cookies(resp.cookies())
                .headers(resp.headers())
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Content-Type", "application/x-www-form-urlencoded;charset=windows-1251")
                .data("Username", "kaskad-ltd")
                .data("Password", "master2019")
                .data("RedirectURL", "/auction/guiding/list_auction/1-start")
                .method(Method.POST)
                .followRedirects(true)
                .execute();
        
        try {
            Thread.sleep(SLEEP_T);
        } catch (InterruptedException ex) {
            X5Tenders.logThrownRecord(Extractor.class.getName(), ex);
        }
        
        MAP_LOGINPAGE_COOKIES = loginPageResponse.cookies();
        
        ArrayList<Document> documents = new ArrayList<>();

        resp = Jsoup.connect(TENDERS_OVER_URL)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT)
                //.cookies(resp.cookies())
                .cookies(MAP_LOGINPAGE_COOKIES)
                .method(Method.GET)
                .followRedirects(true)
                .header("Host", "tender.x5.ru")
                .header("Upgrade-Insecure-Requests", "1")
                .execute();
        
        try {
            Thread.sleep(SLEEP_T);
        } catch (InterruptedException ex) {
            X5Tenders.logThrownRecord(Extractor.class.getName(), ex);
        }
        
        Document doc = resp.parse();
        documents.add(doc);
        
        Elements links = doc.getElementsByClass("path");
        
        ArrayList<String> hrefs = new ArrayList<>();
        
        for (Element link : links) {
            
            String linkHref = link.attr("href");
            hrefs.add(linkHref);            
        }
        
        List<String> deduped = hrefs.stream().distinct().collect(Collectors.toList());
        
        for (String url : deduped) {
            
            resp = Jsoup.connect(INDEX_URL.concat(url))
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT)
                    .cookies(MAP_LOGINPAGE_COOKIES)
                    .method(Method.GET)
                    .execute();
            
            doc = resp.parse();
            documents.add(doc); 
        }
        
        for (Document lDoc : documents) {
            Elements tables = lDoc.getElementsByClass("list");
            Element table = tables.get(0);
            Element tbody = table.child(0);
                       
            Elements rows = tbody.getElementsByTag("tr");
            TenderRow tenderRow; 
            for (Element row : rows) {
                if (row.elementSiblingIndex() == 0)
                    continue;
                
                Elements cols = row.getElementsByTag("td");
                
                String number = cols.get(TABLE.columns().NUMBER).text();
                String descr = cols.get(TABLE.columns().DESCRIPTION).text();
                
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                
                GregorianCalendar GC = new GregorianCalendar(1, 1, 1);

                Date startReq = GC.getTime();
                try {
                    startReq = dateFormat.parse(cols.get(TABLE.columns().START_REQ).text());
                } catch (ParseException ex) {
                    X5Tenders.logThrownRecord("Date parse error: ".
                            concat(cols.get(TABLE.columns().START_REQ).text()), ex);
                }

                Date startDate = GC.getTime();
                try {
                    startDate = dateFormat.parse(cols.get(TABLE.columns().START_DATE).text());
                } catch (ParseException ex) {
                    X5Tenders.logThrownRecord("Date parse error: ".
                            concat(cols.get(TABLE.columns().START_DATE).text()), ex);
                }                

                Date finishDate = GC.getTime();
                try {
                    finishDate = dateFormat.parse(cols.get(TABLE.columns().FINISH_DATE).text());
                } catch (ParseException ex) {
                    X5Tenders.logThrownRecord("Date parse error: ".
                            concat(cols.get(TABLE.columns().FINISH_DATE).text()), ex);
                }
                
                if (number.isEmpty())
                    continue;
                
                tenderRow = TABLE.add();
                tenderRow.setNumber(number);
                tenderRow.setDescription(descr);
                tenderRow.setStartReq(startReq);
                tenderRow.setStartDate(startDate);
                tenderRow.setFinishDate(finishDate); 
                                    
            }            
        }
        
        TABLE.print();        
    }    
}