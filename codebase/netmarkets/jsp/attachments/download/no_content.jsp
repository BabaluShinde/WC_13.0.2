<%@ page 
	session="false"
	isErrorPage="true"
	import="com.ptc.windchill.enterprise.servlets.AttachmentsDownloadDirectionServlet"
	import="com.ptc.windchill.enterprise.attachments.attachmentsResource"
 	import="wt.util.WTProperties"
 	import="wt.util.WTMessage"
%>
<%@page contentType="text/html;charset=UTF-8"%>
<%
 	String strCodeBase = WTProperties.getLocalProperties().getProperty("wt.server.codebase", null);
%>

<HTML>

<HEADER>
    <SCRIPT LANGUAGE="JavaScript">
    	function loaded() {
    	    window.resizeTo(600, 400);
    	}
    	
    	function okClicked() {
    	    window.close();
    	}
    </SCRIPT>
</HEADER>

<BODY BGCOLOR="<%= WTProperties.getLocalProperties().getProperty("wt.html.color.bg-msg", "#DFDFDF") %>" ONLOAD="JavaScript:loaded()">
  <TABLE>
    <TR HIGHT="200" VALIGN="top">
      <TD><IMG SRC="<%= strCodeBase %>/netmarkets/images/mssg_confirm.gif" /></TD>
      <TD>
        <B><%= WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.ITERATION_NO_CONTENT_MSG, null) %></B>
      </TD>
    </TR>
    <TR>
      <TD />
      <TD ALIGN="right">
        <INPUT TYPE="button" NAME="ok" ONCLICK="JavaScript:okClicked()"
        	value="<%= WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.DOWNLOAD_CONFIRM_LABEL_OK, null) %>" />
      </TD>
    </TR>
  </TABLE>
</BODY>

</HTML>


