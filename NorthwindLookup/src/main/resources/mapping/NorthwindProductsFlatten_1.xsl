<?xml version="1.0" encoding="UTF-8"?>
<!--
  NorthwindProductsFlatten.xsl
  Aplana la respuesta Atom de Northwind OData V2 a XML plano sin namespaces,
  apto para el campo "Path to Node" del Content Enricher de SAP Cloud Integration.

  Entrada : feed / entry / content / m:properties / d:*
  Salida  : Products / Product / <campo>

  Path to Node (Lookup) = Products/Product
  Key Element  (Lookup) = ProductID
-->
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:a="http://www.w3.org/2005/Atom"
    xmlns:d="http://schemas.microsoft.com/ado/2007/08/dataservices"
    xmlns:m="http://schemas.microsoft.com/ado/2007/08/dataservices/metadata"
    exclude-result-prefixes="a d m">

  <xsl:output method="xml" indent="yes" encoding="UTF-8" omit-xml-declaration="no"/>

  <xsl:template match="/">
    <Products>
      <!-- Un <Product> por cada <entry> del feed. Si el feed viene vacio,
           el resultado es <Products/> y no un body de cero bytes: eso evita
           el WstxEOFException en la agregacion. -->
      <xsl:for-each select="a:feed/a:entry/a:content/m:properties">
        <Product>
          <!-- Copia cada propiedad d:* quitando el prefijo -->
          <xsl:for-each select="d:*">
            <xsl:element name="{local-name()}">
              <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
          </xsl:for-each>
        </Product>
      </xsl:for-each>
    </Products>
  </xsl:template>

</xsl:stylesheet>
