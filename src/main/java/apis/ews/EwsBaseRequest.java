package apis.ews;

import apis.BaseUtil;
import apis.BaseRequest;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ConnectingIdType;
import microsoft.exchange.webservices.data.misc.ImpersonatedUserId;

import java.net.URI;
import java.util.Map;

public class EwsBaseRequest extends BaseRequest {
    public ExchangeService ewsClient = null;
    private String tokenEndPoint;
    private Map<String, String> authParameters;

    public EwsBaseRequest(Map<String, String> organizationRegionAuthParameters){
        tokenEndPoint = "https://outlook.office365.com/EWS/Exchange.asmx";

        authParameters = organizationRegionAuthParameters;
    }


    public void setEwsClient(String mailbox)
    {
        try {
            initAuthParameters(authParameters);
            String token = getAccessToken();
            ExchangeService service = new ExchangeService();
            service.setUrl(new URI(tokenEndPoint));
            service.getHttpHeaders().put("Authorization", "Bearer " + token);
            service.setImpersonatedUserId(new ImpersonatedUserId(ConnectingIdType.SmtpAddress, mailbox));
            service.getHttpHeaders().put("X-AnchorMailbox", mailbox);

            ewsClient = service;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
