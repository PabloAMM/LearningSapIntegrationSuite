// This is Groovy Flowstep Version 2.x, running with Groovy runtime 4, Downgrade the script if older behaviour needed.

package script.v2

import com.sap.it.script.v2.api.Message;
import java.util.HashMap;

def Message processData(Message message) {

// Check Header
def header = message.getHeaders();
def context = header.get("context");

if (context === null){
    context = "Result";
}

message.setHeader("context",context);

    return message;
}
