package apis.ews;

import apis.BaseUtil;
import apis.BaseRequest;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ConnectingIdType;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.misc.ImpersonatedUserId;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

import static apis.BaseUtil.RegionEnum.LOCAL;

public class EwsBaseRequest extends BaseRequest {
    private ExchangeService ewsClient;
    private String tokenEndPoint = null;
    private int region = 0;
    private Map<String, String> authParameters;

    public EwsBaseRequest(){

    }

    public EwsBaseRequest(int regionParam, Map<String, String> organizationRegionAuthParameters){
        switch (BaseUtil.RegionEnum.getRegionEnumByRegion(regionParam)){
            case  GLOBALCLOUD:
                tokenEndPoint = "https://outlook.office365.com/EWS/Exchange.asmx";
                break;
            case CHINACLOUD:
                tokenEndPoint = "https://partnet.outlook.cn/EWS/Exchange.asmx";
                break;
            case LOCAL:
                String domain = organizationRegionAuthParameters.get("domain");
                tokenEndPoint = "https://" + domain + "/EWS/Exchange.asmx";
                break;
            default:
                break;
        }

        region = regionParam;
        authParameters = organizationRegionAuthParameters;
    }


    public void setEwsClient(String mailbox)
    {
        try {
            if (BaseUtil.RegionEnum.getRegionEnumByRegion(region) == LOCAL){
                ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2007_SP1);
                String username = authParameters.get("username");
                String password = authParameters.get("password");
                ExchangeCredentials credentials = new WebCredentials(username, password);
                service.setCredentials(credentials);
                service.setUrl(new URI(tokenEndPoint));
                service.setImpersonatedUserId(new ImpersonatedUserId(ConnectingIdType.SmtpAddress, mailbox));
                service.getHttpHeaders().put("X-AnchorMailbox", mailbox);

                ewsClient = service;
            } else {
                initAuthParameters(authParameters);
                String token = getAccessToken();
                ExchangeService service = new ExchangeService();
                service.setUrl(new URI(tokenEndPoint));
                service.getHttpHeaders().put("Authorization", "Bearer " + token);
                service.setImpersonatedUserId(new ImpersonatedUserId(ConnectingIdType.SmtpAddress, mailbox));
                service.getHttpHeaders().put("X-AnchorMailbox", mailbox);

                ewsClient = service;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public ExchangeService getEwsClient()
    {
        return ewsClient;
    }
}
