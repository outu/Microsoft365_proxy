package test;

import apis.ews.FolderRequests;
import apis.ews.MessageRequests;
import apis.powershell.PowershellExchangeOperation;
import microsoft.exchange.webservices.data.core.ExchangeService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class testExchangeServerApis {
    private static List<Map> soapClient;
    private static ExchangeService ewsClient;
    private static PowershellExchangeOperation powershellClient;

    public static void main(String[] args) throws Exception {
        String mailbox = "Administrator@exch.com.cn";
        initClient(mailbox);
        System.out.printf(syncGetMessageInfo("AAMkAGE5NzcxZjBiLWI0Y2MtNDhlNy1hZjViLTQ0NzZiMmQzN2Q1ZAAuAAAAAACC2Y8PhSFoQo3NQPbM2L49AQBcaT0SLAv6S6PqbrxnTa5XAAAAAAEMAAA=", 10, "H4sIAAAAAAAEAGNgYGcAAotqE0tHE2NTA0ddZ3NHC10TR2djXSdnJ2ddNyMnC2cnczdLU1OD2vBgveDKvOTgksSSVOfEvMSiSgYr0nW65eekpBZ5pjBYkq43LLWoODM/j8GaaK3+QMuKS4JSk1Mzy1JTQjJzU0nwrU9icYlnXnFJYl5yqncqKb71zS9K9SxJzS32zwtOLSpLLSLByXDfhgNxUW5iUTYklrgYGISA0tDwAxkOUskgCJQyAGI9kJqmm/38rYoZTr1nHb6dubHPlpEhJtNWSIf7l/fiV3l70n3XhYNUMfIAMQMfAzOIw80gUOAse4P5tgeDEFCUF4iB1v0Dyvg6Bnj6OvqBFDG4mbqFgZWjgTYglkPiLwFiCSzq9gCxGRIf3VlMDAxBIJfpT53gG8BwL4R9t1o9h+9k/X9Vc5V+7GUD68H0TBBUnAGoLQBDFqhLv+e/HV5dvjjtSmNgaAAAqDX1ryADAAA="));
    }


    public static void initClient(String mailbox){
        testOuth testOuth = new testOuth();
        soapClient = testOuth.getExchangeServerSoapClient(mailbox);
        ewsClient  = testOuth.getExchangeServerEwsClient(mailbox);
        powershellClient = testOuth.getPowershellClient();
    }


    /**
     * 获取所有的用户信息
     * @return
     * @throws IOException
     */
    public static String getUserInfo() throws IOException {
        return powershellClient.getUserInfo();
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
     * 增量获取目录
     * @return
     * @throws Exception
     */
    public static String syncGetMailFolder(String rootFolderId, String folderDeltaToken) throws Exception {
        MessageRequests messageRequests = new MessageRequests(ewsClient);
        return messageRequests.syncGetMailFolder(rootFolderId, folderDeltaToken);
    }


    public static String syncGetMessageInfo(String folderId, int count, String messageDeltaToken) throws Exception {
        MessageRequests messageRequests = new MessageRequests(ewsClient);
        return messageRequests.syncGetMessageInfo(folderId, count, messageDeltaToken);
    }
}
