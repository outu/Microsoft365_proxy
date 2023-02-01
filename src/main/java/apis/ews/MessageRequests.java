package apis.ews;

import com.alibaba.fastjson.JSONObject;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.BasePropertySet;
import microsoft.exchange.webservices.data.core.request.SyncFolderHierarchyRequest;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.sync.ChangeCollection;
import microsoft.exchange.webservices.data.sync.FolderChange;

import java.util.ArrayList;
import java.util.List;

public class MessageRequests extends EwsBaseRequest {

    public MessageRequests(ExchangeService ewsClientCache){
        ewsClient = ewsClientCache;
    }


    public byte[] getMimeContent(String mailId) throws Exception {
        mailId = mailId.replace("-", "/");
        mailId = mailId.replace("_", "+");

        try {
            ItemId itemId = new ItemId(mailId);
            PropertySet propSet = new PropertySet(BasePropertySet.FirstClassProperties);
            propSet.add(ItemSchema.MimeContent);

            EmailMessage message = EmailMessage.bind(ewsClient, itemId, propSet);

            return message.getMimeContent().getContent();
        } catch (Exception e){
            throw new Exception(e);
        }
    }


    /**
     * 增量获取目录接口
     * @param mailFolderId
     * @param mailSyncState 增量token
     * @return
     * @throws Exception
     */
    public String syncGetMailFolder(String mailFolderId, String mailSyncState) throws Exception {
        String syncMailFolderJson = "";
        List<JSONObject> syncMailFolderList = new ArrayList<>();

        FolderId folderId = new FolderId(mailFolderId);

        SyncFolderHierarchyRequest syncFolderHierarchyRequest = new SyncFolderHierarchyRequest(ewsClient);
        ChangeCollection<FolderChange> folderChangeChangeCollection =  syncFolderHierarchyRequest.getService().syncFolderHierarchy(folderId, PropertySet.FirstClassProperties, mailSyncState);

        int changeCount = folderChangeChangeCollection.getCount();

        if (changeCount > 0){
            for (int i = 0; i < changeCount; i++){
                FolderChange folderChange = folderChangeChangeCollection.getChangeAtIndex(i);
                JSONObject oneMailFolder = new JSONObject();
                oneMailFolder.put("folder_id", folderChange.getFolder().getId().getUniqueId());
                oneMailFolder.put("parent_folder_id", folderChange.getFolder().getParentFolderId().getUniqueId());
                oneMailFolder.put("display_name", folderChange.getFolder().getDisplayName());
                syncMailFolderList.add(oneMailFolder);
            }
        }

        JSONObject syncMailFolderJsonObject= new JSONObject();

        syncMailFolderJsonObject.put("folder_delta_token", folderChangeChangeCollection.getSyncState());
        syncMailFolderJsonObject.put("sync_mail_folder_list", syncMailFolderList);
        syncMailFolderJson = syncMailFolderJsonObject.toString();

        return syncMailFolderJson;
    }


    public String getMailIndexInfo(String folderId, int count)
    {

        return "";
    }
}
