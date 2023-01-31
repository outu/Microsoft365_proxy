package test;

import apis.ews.EwsBaseRequest;
import apis.ews.FolderRequests;
import apis.graph.GraphBaseRequest;
import apis.graph.common.UserRequests;
import apis.graph.exchange.MailRequests;
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
        System.out.printf("%s", syncGetMailIndexInfo("AQMkAGI1ZmRjZWUAZC0yZTVlLTQyMzctYTc4Ni0yYjE3NDMxMjdhOGYALgAAAy5x2W1SrcBDvroL1Asx4J8BAKuvciembiNLk9i11WPD-4EAAAIBCQAAAA==", "yunqi@s22fb.onmicrosoft.com", "", ""));
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
     * 增量获取用户组
     * @return
     */
    public static String syncGetGroupInfo(){
        String skipToken = "";
        String userDeltaToken = "";
        UserRequests userRequests = new UserRequests(graphClient);

        return userRequests.syncGroupInfo(userDeltaToken, skipToken);
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


    /**
     * 增量获取子目录
     * @param rootFolderId
     * @param userId
     * @param deltaLink
     * @param skipToken
     * @return
     * @throws Exception
     */
    public static String syncGetMailChildFolder(String rootFolderId, String userId, String deltaLink, String skipToken) throws Exception {
        MailRequests mailRequests = new MailRequests(graphClient, userId);
        return mailRequests.syncGetMailFolder(rootFolderId, deltaLink, skipToken);
    }


    /**
     * 普通获取子目录
     * @param rootFolderId
     * @param userId
     * @return
     * @throws Exception
     */
    public static String getMailChildFolder(String rootFolderId, String userId) throws Exception {
        MailRequests mailRequests = new MailRequests(graphClient, userId);
        return mailRequests.getMailChildFolder(rootFolderId);
    }


    public static String syncGetMailIndexInfo(String folderId, String userId, String deltaLink, String skipToken){
        MailRequests mailRequests = new MailRequests(graphClient, userId);
        return mailRequests.syncGetMailIndexInfo(folderId, deltaLink, skipToken, 10);
    }


}
