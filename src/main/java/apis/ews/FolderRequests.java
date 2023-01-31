package apis.ews;

import com.alibaba.fastjson.JSONObject;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.search.FindFoldersResults;
import microsoft.exchange.webservices.data.search.FolderView;

import java.util.ArrayList;
import java.util.List;

public class FolderRequests extends EwsBaseRequest{
    public FolderRequests(ExchangeService ewsClientCache){
        ewsClient = ewsClientCache;
    }


    /**
     * 获取Exchange Server and Exchange Online各种类型顶级目录
     * @return
     * @throws Exception
     */
    public String getAllTypeRootFolder() throws Exception {
        String rootMailFolderJson = "";
        List<JSONObject> rootMailFolderListObject = new ArrayList<>();

        Folder folder = Folder.bind(ewsClient, WellKnownFolderName.MsgFolderRoot);
        FindFoldersResults findFolderResults = folder.findFolders(new FolderView(EwsUtil.MAX_ROOT_FOLDER_COUNT));

        for (Folder item : findFolderResults.getFolders()){
            JSONObject oneRootMailFolder = new JSONObject();
            oneRootMailFolder.put("folder_id", item.getId().getUniqueId());
            oneRootMailFolder.put("parent_folder_id", item.getParentFolderId().getUniqueId());
            oneRootMailFolder.put("display_name", item.getDisplayName());
            rootMailFolderListObject.add(oneRootMailFolder);
        }

        rootMailFolderJson = rootMailFolderListObject.toString();

        return rootMailFolderJson;
    }
}
