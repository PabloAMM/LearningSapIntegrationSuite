import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.xml.*;
import java.io.*;
 
def Message processData(Message message) 
{
    String bodyJson = message.getBody(String)
    message.setProperty("TempPayload", bodyJson)
   
    return message;
}

def Message processData2(Message message) 
{
    def headers = message.getHeaders();
    def messageLog = messageLogFactory.getMessageLog(message);
    
    def cookie = headers.get("Set-Cookie");
    StringBuffer bufferedCookie = new StringBuffer();
    for (Object item : cookie) 
    {
        bufferedCookie.append(item + ";");      
    }
    message.setHeader("Cookie", bufferedCookie.toString());
    
    
    if(messageLog != null)
    {
        messageLog.setStringProperty("Logging_Cookie", bufferedCookie.toString());
    }
    
    message.setBody(message.getProperty("TempPayload"))
	
    return message;
}