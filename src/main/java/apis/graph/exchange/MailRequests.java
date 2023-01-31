package apis.graph.exchange;

import apis.graph.GraphBaseRequest;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.graph.models.MailFolder;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.MailFolderCollectionPage;
import com.microsoft.graph.requests.MailFolderDeltaCollectionPage;
import com.microsoft.graph.requests.UserDeltaCollectionPage;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.request.SyncFolderHierarchyRequest;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.search.FindFoldersResults;
import microsoft.exchange.webservices.data.search.FolderView;
import microsoft.exchange.webservices.data.sync.ChangeCollection;
import microsoft.exchange.webservices.data.sync.FolderChange;
import okhttp3.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MailRequests extends GraphBaseRequest {
    public MailRequests(GraphServiceClient<Request> graphClientCache, String userId){
        graphClient = graphClientCache;
        backupUserId = userId;
    }

    /**
     * 普通获取子目录接口
     * @param rootFolderId
     * @return
     * @throws Exception
     */
    public String getMailChildFolder(String rootFolderId) throws Exception {
        String mailChildFolderJson = "";
        List<JSONObject> mailChildFolderList = new ArrayList<>();

        rootFolderId = GraphUtil.graphIdConvertToEwsId(rootFolderId);
        MailFolderCollectionPage childFolders = graphClient.users(backupUserId).mailFolders(rootFolderId).childFolders()
                .buildRequest()
                .select("id,displayName,parentFolderId")
                .get();
        int mailChildFoldersCount = childFolders.getCurrentPage().size();
        if (mailChildFoldersCount > 0){
            for (int i = 0; i < mailChildFoldersCount; i++){
                MailFolder mailFolder = childFolders.getCurrentPage().get(i);

                JSONObject oneMailFolder = new JSONObject();
                oneMailFolder.put("folder_id", mailFolder.id);
                oneMailFolder.put("parent_folder_id", mailFolder.parentFolderId);
                oneMailFolder.put("display_name", mailFolder.displayName);
                mailChildFolderList.add(oneMailFolder);
            }

        }

        mailChildFolderJson = mailChildFolderList.toString();

        return mailChildFolderJson;
    }


    /**
     *获取邮件类型的顶级目录
     * @return
     */
    public String getRootMailFolder(){
        String rootMailFolderJson = "";
        List<String> rootMailFolderList = new ArrayList<>();

        MailFolderCollectionPage rootMailFolders = graphClient.me().mailFolders()
                .buildRequest()
                .select("id,displayName,parentFolderId")
                .get();
        int rootMailFoldersCount = rootMailFolders.getCurrentPage().size();

        if (rootMailFoldersCount > 0){
            for (int i = 0; i < rootMailFoldersCount; i++){
                MailFolder rootMailFolder = rootMailFolders.getCurrentPage().get(i);

                JsonObject oneRootMailFolder = new JsonObject();
                oneRootMailFolder.addProperty("folder_id", rootMailFolder.id);
                oneRootMailFolder.addProperty("parent_folder_id", rootMailFolder.parentFolderId);
                oneRootMailFolder.addProperty("display_name", rootMailFolder.displayName);
                rootMailFolderList.add(oneRootMailFolder.toString());
            }

        }

        Gson gson = new Gson();
        rootMailFolderJson = gson.toJson(rootMailFolderList);

        return rootMailFolderJson;
    }


    /**
     * 增量获取邮件子目录接口
     * @param rootFolderId
     * @param deltaLink
     * @param skipToken
     * @return
     * @throws Exception
     */
    public String syncGetMailFolder(String rootFolderId, String deltaLink, String skipToken) throws Exception {
        String syncMailChildFolderJson = "";
        List<JSONObject> mailChildFolderList = new ArrayList<>();
        MailFolderDeltaCollectionPage mailFolderDeltaCollectionPage;

        rootFolderId = GraphUtil.graphIdConvertToEwsId(rootFolderId);
        if(Objects.equals(deltaLink, "")){
            if (!Objects.equals(skipToken, "")){
                mailFolderDeltaCollectionPage = graphClient.users(backupUserId).mailFolders(rootFolderId).childFolders()
                        .delta()
                        .buildRequest()
                        .select("id,displayName,parentFolderId")
                        .skipToken(skipToken)
                        .get();
            } else {
                mailFolderDeltaCollectionPage = graphClient.users(backupUserId).mailFolders(rootFolderId).childFolders()
                        .delta()
                        .buildRequest()
                        .select("id,displayName,parentFolderId")
                        .get();
            }
        } else {
            mailFolderDeltaCollectionPage = graphClient.users(backupUserId).mailFolders(rootFolderId).childFolders()
                    .delta()
                    .buildRequest()
                    .select("id,displayName,parentFolderId")
                    .deltaLink(deltaLink)
                    .get();
        }
        int size = mailFolderDeltaCollectionPage.getCurrentPage().size();

        if(size > 0){
            for (int i = 0; i < size; i++){
                MailFolder mailFolder = mailFolderDeltaCollectionPage.getCurrentPage().get(i);

                JSONObject mailFolderInfo = new JSONObject();

                mailFolderInfo.put("mail_id", mailFolder.id);
                mailFolderInfo.put("display_name", mailFolder.displayName);
                mailFolderInfo.put("parent_folder_id", mailFolder.parentFolderId);
                mailChildFolderList.add(mailFolderInfo);
            }
        }

        JSONObject syncMailFolderInfoJsonObject = new JSONObject();

        String newSkipToken = "";
        if (mailFolderDeltaCollectionPage.deltaLink() == null){
            String nextPageUrl = mailFolderDeltaCollectionPage.getNextPage().getRequestUrl();
            String[] splitNextPageUrl = nextPageUrl.split("\\?");
            newSkipToken = splitNextPageUrl[1].replace("$skiptoken=", "");
            syncMailFolderInfoJsonObject.put("mail_folder_delta_token", "");
        } else {
            syncMailFolderInfoJsonObject.put("mail_folder_delta_token", mailFolderDeltaCollectionPage.deltaLink());
        }

        syncMailFolderInfoJsonObject.put("sync_mail_folder_list", mailChildFolderList);
        syncMailFolderInfoJsonObject.put("mail_folder_skip_token", newSkipToken);

        syncMailChildFolderJson = syncMailFolderInfoJsonObject.toString();

        return syncMailChildFolderJson;
    }


    public String getMailIndexInfo(String folderId, int count)
    {

        return "";
    }
}
