import apis.ews.EwsBaseRequest;
import apis.ews.MailRequests;
import microsoft.exchange.webservices.data.core.ExchangeService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class test {

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties"));

        Map<String, String> organizationAuthParameters = new HashMap<String, String>();
        organizationAuthParameters.put("tenantUuid", properties.getProperty("tenantUuid"));
        organizationAuthParameters.put("appUuid", properties.getProperty("appUuid"));
        organizationAuthParameters.put("appSecret", properties.getProperty("appSecret"));
        organizationAuthParameters.put("appCertInfo", properties.getProperty("appCertInfo"));
        organizationAuthParameters.put("region", properties.getProperty("region"));

        EwsBaseRequest ewsBaseRequest = new EwsBaseRequest(organizationAuthParameters);
        ewsBaseRequest.setEwsClient("AlexW@s22fb.onmicrosoft.com");
        ExchangeService ewsClient = ewsBaseRequest.getEwsClient();

        MailRequests mailRequests =new MailRequests(ewsClient);
        mailRequests.getMimeContent("AAMkAGVjYmJjMjY5LTE4OTQtNGExNi05Y2QwLTQyNWUzM2JkNThlMABGAAAAAADNEftcr0zeRrNKdNRVtg8hBwBEsbCet_yGTbX1M-Wcb6tPAAAAAAEMAABEsbCet_yGTbX1M-Wcb6tPAABJVFvNAAA=");
    }
}
