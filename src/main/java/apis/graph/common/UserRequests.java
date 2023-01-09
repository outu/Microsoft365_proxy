package apis.graph.common;

import apis.graph.GraphBaseRequest;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.UserDeltaCollectionPage;
import okhttp3.Request;

public class UserRequests extends GraphBaseRequest {
    public UserRequests(GraphServiceClient<Request> graphClientCache){
        graphClient = graphClientCache;
    }


    public String getUserInfoBySkipToken(String skipToken){
        return "";
    }


    public String getUserInfoByDeltaLink(String deltaLink){
        UserDeltaCollectionPage userDeltaCollectionPage;

        if(deltaLink == null){
            userDeltaCollectionPage = graphClient.users()
                    .delta()
                    .buildRequest()
                    .get();
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
                System.out.println(user.displayName);
            }
        }
        return "";
    }
}
