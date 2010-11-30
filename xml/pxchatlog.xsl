<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">
		<html>
			<head>
				<title>chat log</title>
			</head>
			<body>
				<xsl:apply-templates />
				<hr />
				<p>
					<b>end of chat</b>
				</p>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="participants">
		<b>Participants of this chat.</b>
		<br />
		<br />
		<xsl:apply-templates />
		<hr />
		<br />
	</xsl:template>

	<xsl:template match="name">
		<xsl:value-of select="." />
		<br />
	</xsl:template>

	<xsl:template match="chat">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="message">
		<xsl:value-of select="@author" />
		<xsl:text> (</xsl:text>
		<xsl:value-of select="@date" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="@time" />
		<xsl:text>): </xsl:text>
		<xsl:apply-templates />
		<br />
	</xsl:template>

	<xsl:template match="i">
		<xsl:copy>
			<xsl:value-of select="." />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="b">
		<xsl:copy>
			<xsl:value-of select="." />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="img">
		<img src="{@src}" />
	</xsl:template>

</xsl:stylesheet>