package apis.graph.exchange;

import apis.graph.GraphBaseRequest;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.graph.models.MailFolder;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.Recipient;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.requests.*;
import okhttp3.Request;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class MessageRequests extends GraphBaseRequest {
    public MessageRequests(GraphServiceClient<Request> graphClientCache, String userId){
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

                mailFolderInfo.put("folder_id", mailFolder.id);
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


    /**
     * 增量获取邮件索引信息
     * @param folderId
     * @param deltaLink
     * @param skipToken
     * @param count
     * @return
     */
    public String syncGetMessageInfo(String folderId, String deltaLink, String skipToken, int count){
        String syncMessageInfoJson = "";
        List<JSONObject> messageInfoList = new ArrayList<>();
        MessageDeltaCollectionPage messageDeltaCollectionPage;

        folderId = GraphUtil.graphIdConvertToEwsId(folderId);
        LinkedList<Option> requestOptions = new LinkedList<Option>();
        requestOptions.add(new HeaderOption("Prefer", "outlook.body-content-type=\"text\""));
        if(Objects.equals(deltaLink, "")){
            if (!Objects.equals(skipToken, "")){
                messageDeltaCollectionPage = graphClient.users(backupUserId).mailFolders(folderId).messages()
                        .delta()
                        .buildRequest(requestOptions)
                        .select("id,parentFolderId,subject,body,receivedDateTime,toRecipients,sender,ccRecipients")
                        .skipToken(skipToken)
                        .get();
            } else {
                messageDeltaCollectionPage = graphClient.users(backupUserId).mailFolders(folderId).messages()
                        .delta()
                        .buildRequest(requestOptions)
                        .select("id,parentFolderId,subject,body,receivedDateTime,toRecipients,sender,ccRecipients")
                        .get();
            }
        } else {
            messageDeltaCollectionPage = graphClient.users(backupUserId).mailFolders(folderId).messages()
                    .delta()
                    .buildRequest(requestOptions)
                    .select("id,parentFolderId,subject,body,receivedDateTime,toRecipients,sender,ccRecipients")
                    .deltaLink(deltaLink)
                    .get();
        }
        int size = messageDeltaCollectionPage.getCurrentPage().size();

        if(size > 0){
            for (int i = 0; i < size; i++){
                Message message = messageDeltaCollectionPage.getCurrentPage().get(i);

                JSONObject messageInfo = new JSONObject();

                messageInfo.put("message_id", message.id);
                messageInfo.put("parent_folder_id", message.parentFolderId);
                messageInfo.put("subject", message.subject);
                messageInfo.put("body", message.body.content.replace("\r\n", ""));
                messageInfo.put("recv_date", message.receivedDateTime);
                messageInfo.put("recipents", getEmailAddressFromList(message.toRecipients));
                messageInfo.put("sender", message.sender.emailAddress.address);
                messageInfo.put("cc", getEmailAddressFromList(message.ccRecipients));

                messageInfoList.add(messageInfo);
            }
        }

        JSONObject syncMessageInfoJsonObject = new JSONObject();

        String newSkipToken = "";
        if (messageDeltaCollectionPage.deltaLink() == null){
            String nextPageUrl = messageDeltaCollectionPage.getNextPage().getRequestUrl();
            String[] splitNextPageUrl = nextPageUrl.split("\\?");
            newSkipToken = splitNextPageUrl[1].replace("$skiptoken=", "");
            syncMessageInfoJsonObject.put("message_info_delta_token", "");
        } else {
            syncMessageInfoJsonObject.put("message_info_delta_token", messageDeltaCollectionPage.deltaLink());
        }

        syncMessageInfoJsonObject.put("sync_message_info_list", messageInfoList);
        syncMessageInfoJsonObject.put("message_info_skip_token", newSkipToken);
        syncMessageInfoJsonObject.put("is_finished", messageDeltaCollectionPage.deltaLink() == null ? "0" : "1");

        syncMessageInfoJson = syncMessageInfoJsonObject.toString();

        return syncMessageInfoJson;
    }


    private String getEmailAddressFromList(List<Recipient> emailAddressList){
        if (emailAddressList.size() == 0){
            return "";
        }
        List<String> emailAddressValidDataList = new ArrayList<>();

        for (int i=0; i < emailAddressList.size(); i++){
            emailAddressValidDataList.add(emailAddressList.get(i).emailAddress.address);
        }

        return emailAddressValidDataList.toString();
    }


    public String getMailIndexInfo(String folderId, int count)
    {

        return "";
    }
}
