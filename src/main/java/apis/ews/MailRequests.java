package apis.ews;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.BasePropertySet;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.property.complex.ItemId;

public class MailRequests extends EwsBaseRequest{


//    public MailRequests(ExchangeService ewsClientCache){
//        ewsClient.set(ewsClientCache);
//    }

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


    public String getMailFolder(String rootFolder)
    {
        return "";
    }


    public String getMailIndexInfo(String folderId, int count)
    {

        return "";
    }
}
