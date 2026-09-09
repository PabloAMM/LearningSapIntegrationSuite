<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
  <userdetailsSet>
    <xsl:for-each select="//m:properties">
      <userdetails>
        <xsl:for-each select="d:*">
          <xsl:element name="{local-name()}"><xsl:value-of select="."/></xsl:element>
        </xsl:for-each>
      </userdetails>
    </xsl:for-each>
  </userdetailsSet>
</xsl:template>
</xsl:stylesheet>


