package apis.graph.common;

import apis.graph.GraphBaseRequest;
import com.alibaba.fastjson.JSONObject;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.UserDeltaCollectionPage;
import okhttp3.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserRequests extends GraphBaseRequest {
    public UserRequests(GraphServiceClient<Request> graphClientCache){
        graphClient = graphClientCache;
    }


    public String syncUserInfo(String deltaLink, String skipToken){
        String syncUserInfoJson = "";
        List<JSONObject> userInfoList = new ArrayList<>();

        UserDeltaCollectionPage userDeltaCollectionPage;

        if(Objects.equals(deltaLink, "")){
            if (!Objects.equals(skipToken, "")){
                userDeltaCollectionPage = graphClient.users()
                        .delta()
                        .buildRequest()
                        .skipToken(skipToken)
                        .get();
            } else {
                userDeltaCollectionPage = graphClient.users()
                        .delta()
                        .buildRequest()
                        .get();
            }
        } else {
            userDeltaCollectionPage = graphClient.users()
                    .delta()
                    .buildRequest()
                    .deltaLink(deltaLink)
                    .get();
        }
        int size = userDeltaCollectionPage.getCurrentPage().size();

        if(size > 0){
            for (int i = 0; i < size; i++){
                User user = userDeltaCollectionPage.getCurrentPage().get(i);
                if (user.mail == null || user.displayName == null){
                    continue;
                }
                JSONObject oneUserInfo = new JSONObject();

                oneUserInfo.put("user_uuid", user.id);
                oneUserInfo.put("display_name", user.displayName);
                oneUserInfo.put("mail", user.mail);
                userInfoList.add(oneUserInfo);
            }
        }

        JSONObject syncUserInfoJsonObject = new JSONObject();

        String newSkipToken = "";
        if (userDeltaCollectionPage.deltaLink() == null){
            String nextPageUrl = userDeltaCollectionPage.getNextPage().getRequestUrl();
            String[] splitNextPageUrl = nextPageUrl.split("\\?");
            newSkipToken = splitNextPageUrl[1].replace("$skiptoken=", "");
            syncUserInfoJsonObject.put("user_delta_token", "");
        } else {
            syncUserInfoJsonObject.put("user_delta_token", userDeltaCollectionPage.deltaLink());
        }

        syncUserInfoJsonObject.put("sync_user_list", userInfoList);
        syncUserInfoJsonObject.put("user_skip_token", newSkipToken);

        syncUserInfoJson = syncUserInfoJsonObject.toString();

        return syncUserInfoJson;
    }
}
