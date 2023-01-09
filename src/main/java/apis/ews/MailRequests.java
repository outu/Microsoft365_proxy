package apis.ews;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.BasePropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.request.FindFolderRequest;
import microsoft.exchange.webservices.data.core.request.SyncFolderHierarchyRequest;
import microsoft.exchange.webservices.data.core.request.SyncFolderItemsRequest;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.search.FindFoldersResults;
import microsoft.exchange.webservices.data.search.FolderView;
import microsoft.exchange.webservices.data.sync.ChangeCollection;
import microsoft.exchange.webservices.data.sync.FolderChange;

public class MailRequests extends EwsBaseRequest{

    public MailRequests(ExchangeService ewsClientCache){
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


    public String getMailRootFolder() throws Exception {
        Folder folder = Folder.bind(ewsClient, WellKnownFolderName.MsgFolderRoot);
        FindFoldersResults findFolderResults = folder.findFolders(new FolderView(30));

        for (Folder item : findFolderResults.getFolders()){
            System.out.println("****************************");
            System.out.println(item.getDisplayName());
            System.out.println(item.getId());
        }

        return "";

    }


    /**
     * 普通获取目录接口
     * @param rootFolder
     * @return
     * @throws Exception
     */
    public String getMailFolder(WellKnownFolderName rootFolder) throws Exception {
        Folder folder = Folder.bind(ewsClient, rootFolder);
        FindFoldersResults findFolderResults = folder.findFolders(new FolderView(1));

        for (Folder item : findFolderResults.getFolders()){
            System.out.println(item.getDisplayName());
        }

        return "";
    }


    /**
     * 增量获取目录接口
     * @param mailFolderId
     * @param mailSyncState 增量token
     * @return
     * @throws Exception
     */
    public String syncGetMailFolder(String mailFolderId, String mailSyncState) throws Exception {
        FolderId folderId = new FolderId(mailFolderId);

        SyncFolderHierarchyRequest syncFolderHierarchyRequest = new SyncFolderHierarchyRequest(ewsClient);
        ChangeCollection<FolderChange> folderChangeChangeCollection =  syncFolderHierarchyRequest.getService().syncFolderHierarchy(folderId, PropertySet.FirstClassProperties, mailSyncState);

        int changeCount = folderChangeChangeCollection.getCount();

        if(changeCount == 0){
            return "";
        } else {
            for (int i = 0; i < changeCount; i++){
                FolderChange folderChange = folderChangeChangeCollection.getChangeAtIndex(i);
                System.out.println(folderChange.getFolder().getDisplayName());
            }
        }


        return "";
    }


    public String getMailIndexInfo(String folderId, int count)
    {

        return "";
    }
}
