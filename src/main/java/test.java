import apis.ews.EwsBaseRequest;
import apis.ews.MailRequests;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class test {

    public static void main(String[] args) throws Exception {

        testEwsConnectExchangeServer();
    }


    private int testEwsConnectExchangeOnline() throws Exception {
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
        mailRequests.getMimeContent("AAMkAGVjYmJjMjY5LTE4OTQtNGExNi05Y2QwLTQyNWUzM2JkNThlMABGAAAAAADNEftcr0zeRrNKdNRVtg8hBwBEsbCet_yGTbX1M-Wcb6tPAAAAAAEMAABEsbCet_yGTbX1M-Wcb6tPAABJVFvNAAA=");


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


    private int testGraphConnectExchangeOnline()
    {
        int ret = 0;

        return ret;
    }
}
