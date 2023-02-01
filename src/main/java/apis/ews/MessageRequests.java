package apis.ews;

import com.alibaba.fastjson.JSONObject;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.Recipient;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.BasePropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.enumeration.service.SyncFolderItemsScope;
import microsoft.exchange.webservices.data.core.request.SyncFolderHierarchyRequest;
import microsoft.exchange.webservices.data.core.request.SyncFolderItemsRequest;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.EmailAddressCollection;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.sync.ChangeCollection;
import microsoft.exchange.webservices.data.sync.FolderChange;
import microsoft.exchange.webservices.data.sync.ItemChange;

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
     * @param mailFolderSyncState 增量token
     * @return
     * @throws Exception
     */
    public String syncGetMailFolder(String mailFolderId, String mailFolderSyncState) throws Exception {
        String syncMailFolderJson = "";
        List<JSONObject> syncMailFolderList = new ArrayList<>();

        FolderId folderId = new FolderId(mailFolderId);

        SyncFolderHierarchyRequest syncFolderHierarchyRequest = new SyncFolderHierarchyRequest(ewsClient);
        ChangeCollection<FolderChange> folderChangeChangeCollection =  syncFolderHierarchyRequest.getService().syncFolderHierarchy(folderId, PropertySet.FirstClassProperties, mailFolderSyncState);

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


    /**
     * 增量获取邮件索引信息
     * @param mailFolderId
     * @param count 返回大小
     * @param messageSyncState
     * @return
     */
    public String syncGetMessageInfo(String mailFolderId, int count, String messageSyncState) throws Exception {
        String syncMessageInfoJson = "";
        List<JSONObject> messageInfoList = new ArrayList<>();

        FolderId folderId = new FolderId(mailFolderId);
        SyncFolderItemsRequest syncFolderItemsRequest = new SyncFolderItemsRequest(ewsClient);
        ChangeCollection<ItemChange> messageChangeCollection = syncFolderItemsRequest.getService().syncFolderItems(folderId, PropertySet.FirstClassProperties, null, count, SyncFolderItemsScope.NormalItems, messageSyncState);

        int changeCount = messageChangeCollection.getCount();
        for (int i=0; i<changeCount; i++){
            JSONObject messageInfo = new JSONObject();
            ItemChange itemChange = messageChangeCollection.getChangeAtIndex(i);
            PropertySet propSet = new PropertySet(BasePropertySet.FirstClassProperties);
            propSet.setRequestedBodyType(BodyType.Text);
            EmailMessage message = EmailMessage.bind(ewsClient, itemChange.getItem().getId(), propSet);
            messageInfo.put("message_id", message.getId().getUniqueId());
            messageInfo.put("parent_folder_id", message.getParentFolderId().getUniqueId());
            messageInfo.put("subject", message.getSubject());
            messageInfo.put("body", message.getBody().toString().replace("\r\n", ""));
            messageInfo.put("recv_date", message.getDateTimeReceived());
            messageInfo.put("recipents", getEmailAddressFromCollection(message.getToRecipients()));
            messageInfo.put("sender", message.getSender().getAddress());
            messageInfo.put("cc", getEmailAddressFromCollection(message.getCcRecipients()));

            messageInfoList.add(messageInfo);
        }

        JSONObject syncMessageInfoJsonObject= new JSONObject();

        syncMessageInfoJsonObject.put("sync_message_info_list", messageInfoList);
        syncMessageInfoJsonObject.put("message_info_skip_token", messageChangeCollection.getSyncState());
        syncMessageInfoJsonObject.put("is_finished", messageChangeCollection.getMoreChangesAvailable() == false ? "1" : "0");
        syncMessageInfoJson = syncMessageInfoJsonObject.toString();

        return syncMessageInfoJson;
    }


    private String getEmailAddressFromCollection(EmailAddressCollection emailAddressCollection){
        if (emailAddressCollection.getCount() == 0){
            return "";
        }
        List<String> emailAddressValidDataList = new ArrayList<>();
        List<EmailAddress> emailAddressList = emailAddressCollection.getItems();

        for (int i=0; i < emailAddressList.size(); i++){
            emailAddressValidDataList.add(emailAddressList.get(i).getAddress());
        }

        return emailAddressValidDataList.toString();
    }


    public String getMailIndexInfo(String folderId, int count)
    {

        return "";
    }
}
