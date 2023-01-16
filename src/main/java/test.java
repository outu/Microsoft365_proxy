import apis.ews.EwsBaseRequest;
import apis.ews.MailRequests;
import apis.graph.GraphBaseRequest;
import apis.graph.common.UserRequests;
import apis.powershell.PowershellExchangeOperation;
import apis.soap.SoapBaseRequest;
import apis.soap.XmlRequestData;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.*;

public class test {

    public static void main(String[] args) throws Exception {

        testEwsConnectExchangeOnline();
    }


    private static int testSoapConnectExchangeServer() throws IOException {
        int ret = 0;

        Map<String, String> organizationAuthParameters = new HashMap<String, String>();
        organizationAuthParameters.put("username", "Administrator@exch.com.cn");
        organizationAuthParameters.put("password", "backup@1234567890");
        organizationAuthParameters.put("domain", "WIN-TT7P7PN7QHJ.exch.com.cn");
        organizationAuthParameters.put("region", "100");

        SoapBaseRequest soapBaseRequest = new SoapBaseRequest(organizationAuthParameters);
        soapBaseRequest.setSoapClient("Administrator@exch.com.cn");
        soapBaseRequest.setHttpContext();

        Map<String, HttpPost> soapClientMap = new HashMap<>();
        soapClientMap.put("soapClient", soapBaseRequest.getSoapClient());
        Map<String, HttpClientContext> httpContextMap = new HashMap<>();
        httpContextMap.put("httpContext", soapBaseRequest.getHttpContext());

        List<Map> soapClientCache = new ArrayList<>();
        soapClientCache.add(soapClientMap);
        soapClientCache.add(httpContextMap);

        apis.soap.MailRequests mailRequests = new apis.soap.MailRequests(soapClientCache);
        XmlRequestData xmlRequestData = new XmlRequestData();
        String xmlToGetMailMessage = xmlRequestData.buildXmlToGetMailMimeContent("test1@exch.com.cn", "AQMkAGJkZmFlNGJkLWM0NjEtNDU4Zi04NzhmLTNhNWE3OWYxMDFkOABGAAAD/21pFKhtgUqKvYTQLkeOAAcAqbn2gdfe6UikHCgMkkbpBQAAAgEPAAAAqbn2gdfe6UikHCgMkkbpBQAAAhjdAAAA");


        HttpResponse httpResponse = mailRequests.getResponseWithMimeContent(xmlToGetMailMessage);
        if(httpResponse.getStatusLine().getStatusCode() == 200){
            HttpEntity entity1 = httpResponse.getEntity();

            char[] readbuffer = new char[1024];

            BufferedReader xmlStreamReaderCache = new BufferedReader(new InputStreamReader(entity1.getContent()));
            int count =  xmlStreamReaderCache.read(readbuffer, 0, 1024);
            File txt = new File("F:\\soap_big2.xml");
            if(!txt.exists()){
                boolean result = txt.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(txt);
            while (count > 0) {
                String tmpString = String.copyValueOf(readbuffer);
                char[] tmpChar = new char[count];
                tmpString.getChars(0, count, tmpChar, 0);
                byte[] byteData = toBytes(tmpChar);
                fos.write(byteData, 0, byteData.length);
                fos.flush();
                count =  xmlStreamReaderCache.read(readbuffer, 0, 1024);
            }
            fos.close();
        } else {
            //System.out.println("error");
        }



        System.out.println(httpResponse.getStatusLine().getProtocolVersion() + " " + httpResponse.getStatusLine().getStatusCode());
        return ret;
    }


    private static byte[] toBytes(char[] chars) {
        CharBuffer charBuffer = CharBuffer.wrap(chars);
        ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
                byteBuffer.position(), byteBuffer.limit());
        Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
        return bytes;
    }

    private static int testPowershell() throws IOException {
        int ret = 0;

        Map<String, String> organizationAuthParameters = new HashMap<String, String>();
        organizationAuthParameters.put("username", "Administrator@exch.com.cn");
        organizationAuthParameters.put("password", "backup@1234567890");
        organizationAuthParameters.put("protocol", "http");
        organizationAuthParameters.put("domain", "WIN-TT7P7PN7QHJ.exch.com.cn");
        PowershellExchangeOperation powershellExchangeOperation = new PowershellExchangeOperation(organizationAuthParameters);
        String userInfo = powershellExchangeOperation.getUserInfo();
        System.out.printf(userInfo);

        return ret;
    }


    private static int testEwsConnectExchangeOnline() throws Exception {
        int ret = 0;

        Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties"));

        Map<String, String> organizationAuthParameters = new HashMap<String, String>();
        organizationAuthParameters.put("tenantUuid", properties.getProperty("tenantUuid"));
        organizationAuthParameters.put("appUuid", properties.getProperty("appUuid"));
        organizationAuthParameters.put("appSecret", properties.getProperty("appSecret"));
        organizationAuthParameters.put("appCertInfo", properties.getProperty("appCertInfo"));
        organizationAuthParameters.put("region", properties.getProperty("region"));

        EwsBaseRequest ewsBaseRequest = new EwsBaseRequest(organizationAuthParameters);
        ewsBaseRequest.setEwsClient("yunqi@s22fb.onmicrosoft.com");


        MailRequests mailRequests = new MailRequests(ewsBaseRequest.getEwsClient());
        //mailRequests.getMailRootFolder();
        System.out.printf(mailRequests.getRootMailFolder());
       // mailRequests.getMimeContent("AAQkADE0ODViMDdkLWQ3MGItNDMyMi1hYzAyLWY0NDlhYTdjMjExMgMkABAAANMFII6LxUqBDnol2Q-6hBAAANMFII6LxUqBDnol2Q-6hA==");


        return ret;
    }


    private static int testEwsConnectExchangeServer() throws Exception {
        int ret = 0;

        Map<String, String> organizationAuthParameters = new HashMap<String, String>();
        organizationAuthParameters.put("username", "Administrator@exch.com.cn");
        organizationAuthParameters.put("password", "backup@1234567890");
        organizationAuthParameters.put("domain", "WIN-TT7P7PN7QHJ.exch.com.cn");
        organizationAuthParameters.put("region", "100");

        EwsBaseRequest ewsBaseRequest = new EwsBaseRequest(organizationAuthParameters);
        ewsBaseRequest.setEwsClient("Administrator@exch.com.cn");
        MailRequests mailRequests = new MailRequests(ewsBaseRequest.getEwsClient());
        mailRequests.syncGetMailFolder("AAMkAGE5NzcxZjBiLWI0Y2MtNDhlNy1hZjViLTQ0NzZiMmQzN2Q1ZAAuAAAAAACC2Y8PhSFoQo3NQPbM2L49AQBcaT0SLAv6S6PqbrxnTa5XAAAAAAEMAAA=", "");

        return ret;
    }


    private static int testGraphConnectExchangeOnline() throws Exception {
        int ret = 0;

        Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties"));

        Map<String, String> organizationAuthParameters = new HashMap<String, String>();

        organizationAuthParameters.put("tenantUuid", properties.getProperty("tenantUuid"));
        organizationAuthParameters.put("appUuid", properties.getProperty("appUuid"));
        organizationAuthParameters.put("appSecret", properties.getProperty("appSecret"));
        organizationAuthParameters.put("appCertInfo", properties.getProperty("appCertInfo"));
        organizationAuthParameters.put("region", "0");
        organizationAuthParameters.put("username", properties.getProperty("yunqi@s22fb.onmicrosoft.com"));

        GraphBaseRequest graphBaseRequest = new GraphBaseRequest(organizationAuthParameters);
        graphBaseRequest.setGraphClient();
        UserRequests userRequests = new UserRequests(graphBaseRequest.getGraphClient());
        userRequests.getUserInfoByDeltaLink("");

        return ret;
    }
}
