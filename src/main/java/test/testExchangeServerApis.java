package test;

import apis.ews.EwsBaseRequest;
import apis.ews.FolderRequests;
import apis.ews.MailRequests;
import apis.graph.GraphBaseRequest;
import apis.powershell.PowershellExchangeOperation;
import apis.soap.SoapBaseRequest;
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
        System.out.printf(getUserInfo());
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
    public static String syncGetMailFolder() throws Exception {
        String folder_delta_token = "";
        String mailId = "";
        MailRequests mailRequests = new MailRequests(ewsClient);
        return mailRequests.syncGetMailFolder(mailId, folder_delta_token);
    }
}
