package test;

import apis.ews.EwsBaseRequest;
import apis.ews.FolderRequests;
import apis.graph.GraphBaseRequest;
import apis.graph.common.UserRequests;
import apis.soap.SoapBaseRequest;
import com.microsoft.graph.requests.GraphServiceClient;
import microsoft.exchange.webservices.data.core.ExchangeService;
import okhttp3.Request;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class testExchangeOnlineApis {
    private static List<Map> soapClient;
    private static ExchangeService ewsClient;
    private static GraphServiceClient<Request> graphClient;

    public static void main(String[] args) throws Exception {

        String mailbox = "yunqi@s22fb.onmicrosoft.com";
        initClient(mailbox);
        System.out.printf(syncGetUserInfo());
    }


    public static void initClient(String mailbox) throws Exception {
        testOuth testOuth = new testOuth();
        //soapClient = testOuth.getExchangeOnlineSoapClient();
        ewsClient  = testOuth.getExchangeOnlineEwsClient(mailbox);
        graphClient = testOuth.getGraphClient();
    }


    /**
     * 增量获取用户
     * @return
     */
    public static String syncGetUserInfo(){
        String skipToken = "";
        String userDeltaToken = "";
        UserRequests userRequests = new UserRequests(graphClient);

        return userRequests.syncUserInfo(userDeltaToken, skipToken);
    }


    /**
     * 获取所有的顶级目录
     * @return
     * @throws Exception
     */
    public static String getAllTypeRootFolder() throws Exception {
        FolderRequests folderRequests = new FolderRequests(ewsClient);
        return folderRequests.getAllTypeRootFolder();
    }


}
