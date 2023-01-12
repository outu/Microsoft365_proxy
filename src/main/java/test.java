import apis.ews.EwsBaseRequest;
import apis.ews.MailRequests;
import apis.graph.GraphBaseRequest;
import apis.graph.common.UserRequests;
import apis.powershell.PowershellExchangeOperation;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class test {

    public static void main(String[] args) throws Exception {

        testPowershell();
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

        return  ret;
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
        mailRequests.getMimeContent("AAQkADE0ODViMDdkLWQ3MGItNDMyMi1hYzAyLWY0NDlhYTdjMjExMgMkABAAANMFII6LxUqBDnol2Q-6hBAAANMFII6LxUqBDnol2Q-6hA==");


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
