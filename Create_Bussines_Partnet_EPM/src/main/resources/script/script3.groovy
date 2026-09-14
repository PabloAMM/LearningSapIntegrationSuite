import com.sap.gateway.ip.core.customdev.util.Message

def Message processData(Message message) {
    def messageLog = messageLogFactory.getMessageLog(message)
    def headers = message.getHeaders()

    // ---- 1. Token del ERP ----
    def token = null
    headers.each { k, v ->
        if (k.toString().equalsIgnoreCase("X-Backend-Token")) { token = v }
    }
    def tokenStr = (token == null) ? null : token.toString().trim()

    // ---- 2. Payload original (esta como HEADER, no como property) ----
    def payload = message.getProperty("TempPayload")
    if (payload == null) {
        headers.each { k, v ->
            if (k.toString().equalsIgnoreCase("TempPayload")) { payload = v }
        }
    }

    // ---- 3. Cookies del ERP, limpiando atributos ----
    def raw = null
    headers.each { k, v ->
        if (k.toString().equalsIgnoreCase("Cookie-Backend")) { raw = v }
    }

    def partes = []
    if (raw instanceof List) {
        raw.each { partes.add(it.toString()) }
    } else if (raw != null) {
        def txt = raw.toString().trim()
        if (txt.startsWith("[")) { txt = txt.substring(1) }
        if (txt.endsWith("]"))   { txt = txt.substring(0, txt.length() - 1) }
        txt.split(",").each { partes.add(it) }
    }

    def limpias = []
    partes.each { p ->
        def par = p.toString().split(";")[0].trim()
        if (par.contains("=")) {
            def nombre = par.substring(0, par.indexOf("=")).trim().toLowerCase()
            if (nombre != "path" && nombre != "domain" && nombre != "expires"
             && nombre != "max-age" && nombre != "httponly" && nombre != "secure"
             && nombre != "samesite" && nombre != "version") {
                limpias.add(par)
            }
        }
    }
    def cookieStr = limpias.join("; ")

    if (tokenStr == null || tokenStr.isEmpty() || cookieStr.isEmpty()) {
        throw new Exception("Credenciales incompletas. Token=" + tokenStr + " Cookie=" + cookieStr)
    }
    if (payload == null) {
        throw new Exception("No se recupero TempPayload")
    }

    // ---- 4. Borrar los valores del runtime de CPI ----
    def aBorrar = []
    headers.keySet().each { k ->
        def n = k.toString().toLowerCase()
        if (n == "cookie" || n == "set-cookie" || n == "x-csrf-token"
         || n == "x-backend-token" || n == "cookie-backend"
         || n == "temppayload" || n == "content-length") {
            aBorrar.add(k)
        }
    }
    aBorrar.each { k -> headers.remove(k) }

    // ---- 5. Poner los buenos ----
    message.setHeader("X-CSRF-Token", tokenStr)
    message.setHeader("Cookie",       cookieStr)
    message.setHeader("Content-Type", "application/json")
    message.setHeader("Accept",       "application/json")

    if (messageLog != null) {
        messageLog.setStringProperty("POST_Token",  tokenStr)
        messageLog.setStringProperty("POST_Cookie", cookieStr)
    }

    // ---- 6. Restaurar el payload ----
    message.setBody(payload.toString())
    return message
}