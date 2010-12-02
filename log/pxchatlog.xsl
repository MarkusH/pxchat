<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">
		<html>
			<head>
				<title>
					chat log
					<xsl:text> from </xsl:text>
					<xsl:value-of select="pxchatlog/start/@date" />
				</title>
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

	<xsl:template match="invite">
		<i>
			<xsl:text>[</xsl:text>
			<xsl:value-of select="@date" />
			<xsl:text> - </xsl:text>
			<xsl:value-of select="@time" />
			<xsl:text>]: </xsl:text>
			<xsl:value-of select="@user1" />
			<xsl:text> has invited </xsl:text>
			<xsl:value-of select="@user2" />
		</i>
		<br />
	</xsl:template>

	<xsl:template match="join">
		<i>
			<xsl:text>[</xsl:text>
			<xsl:value-of select="@date" />
			<xsl:text> - </xsl:text>
			<xsl:value-of select="@time" />
			<xsl:text>]: </xsl:text>
			<xsl:value-of select="@user" />
			<xsl:text> has joined the chat</xsl:text>
		</i>
		<br />
	</xsl:template>

	<xsl:template match="leave">
		<i>
			<xsl:text>[</xsl:text>
			<xsl:value-of select="@date" />
			<xsl:text> - </xsl:text>
			<xsl:value-of select="@time" />
			<xsl:text>]: </xsl:text>
			<xsl:value-of select="@user" />
			<xsl:text> has left the chat</xsl:text>
		</i>
		<br />
	</xsl:template>

	<xsl:template match="message">
		<xsl:text>[</xsl:text>
		<xsl:value-of select="@date" />
		<xsl:text> - </xsl:text>
		<xsl:value-of select="@time" />
		<xsl:text>]: </xsl:text>
		<b>
			<xsl:value-of select="@author" />
		</b>
		<xsl:text>: </xsl:text>
		<xsl:apply-templates />
		<br />
	</xsl:template>


</xsl:stylesheet>