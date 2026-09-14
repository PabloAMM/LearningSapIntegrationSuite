/* ============================================================
   Groovy Script para SAP Integration Suite (CPI)
   Convierte la respuesta XML de un servicio OData V2 a JSON.

   Ventaja sobre el paso "XML to JSON Converter":
     - SIEMPRE devuelve un array, aunque venga un solo registro.
     - Funciona tanto con el feed Atom crudo (adapter HTTP)
       como con el XML simplificado del adapter OData V2.

   Configuración en el iFlow:
     Script (Groovy) ubicado DESPUES del Request Reply.
   ============================================================ */

import com.sap.gateway.ip.core.customdev.util.Message
import groovy.json.JsonBuilder



def Message processData(Message message) {
    
    // Nombre del nodo raiz del JSON de salida
def final ROOT_NODE = 'users'

    def body = message.getBody(java.lang.String) as String

    if (body == null || body.trim().isEmpty()) {
        message.setBody(new JsonBuilder([(ROOT_NODE): []]).toString())
        message.setHeader('Content-Type', 'application/json')
        return message
    }

    def xml = new XmlSlurper().parseText(body)

    // 1) Caso feed Atom: los datos estan dentro de <m:properties>
    def nodes = xml.'**'.findAll { it.name() == 'properties' }

    // 2) Caso adapter OData V2 (XML simplificado <userdetailsSet><userdetails>...)
    if (nodes.isEmpty()) {
        nodes = xml.'**'.findAll { it.name() == 'users' }
    }

    // 3) Fallback: hijos directos de la raiz
    if (nodes.isEmpty()) {
        nodes = xml.children().findAll { it.children().size() > 0 }
    }

    def rows = nodes.collect { node ->
        node.children().collectEntries { field ->
            [(field.name()): field.text()]
        }
    }

    message.setBody(new JsonBuilder([(ROOT_NODE): rows]).toString())
    message.setHeader('Content-Type', 'application/json')
    message.setProperty('recordCount', rows.size())

    return message
}
