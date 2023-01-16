package apis.graph.exchange;

import apis.graph.GraphBaseRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.graph.models.MailFolder;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.MailFolderCollectionPage;
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

public class MailRequests extends GraphBaseRequest {
    public MailRequests(GraphServiceClient<Request> graphClientCache){
        graphClient = graphClientCache;
    }

    /**
     * 普通获取目录接口
     * @param rootFolder
     * @return
     * @throws Exception
     */
    public String getMailFolder(WellKnownFolderName rootFolder) throws Exception {


        return "";
    }


    /**
     *
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
     * 增量获取目录接口
     * @param mailFolderId
     * @param mailSyncState 增量token
     * @return
     * @throws Exception
     */
    public String syncGetMailFolder(String mailFolderId, String mailSyncState) throws Exception {



        return "";
    }


    public String getMailIndexInfo(String folderId, int count)
    {

        return "";
    }
}
