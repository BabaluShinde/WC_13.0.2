<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ page 
    isErrorPage="true"
    import="com.ptc.windchill.enterprise.servlets.AttachmentsDownloadDirectionServlet"
    import="com.ptc.windchill.enterprise.attachments.attachmentsResource"
    import="wt.util.WTProperties"
    import="wt.util.WTMessage"
    import="wt.util.HTMLEncoder"
%>


<%
    String strCodeBase = WTProperties.getLocalProperties().getProperty("wt.server.codebase", null);
%>

<HTML SCROLL="no" RESIZABLE="no">

<HEADER>
    <SCRIPT LANGUAGE="JavaScript">
        
        window.resizeTo(400, 430);
        window.resizable="false";
        
        
        function okClicked() {
            window.close();
        }
    </SCRIPT>
</HEADER>

<BODY SCROLL="no" >
  <TABLE>
    <TR HIGHT="200" VALIGN="top">
      <TD><IMG SRC="<%= strCodeBase %>/netmarkets/images/mssg_confirm.gif" /></TD>
      <TD>
        <B><%= WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.DOWNLOAD_NONE_DOWNLOADABLE_MSG, null) %></B>
      </TD>
    </TR>
    <TR>
      <TD />
      <TD ALIGN="center">
        <INPUT TYPE="button" NAME="ok" ONCLICK="JavaScript:okClicked()" width="100" size="100"
            value="<%= HTMLEncoder.encodeForHTMLContent(WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.DOWNLOAD_CONFIRM_LABEL_OK, null)) %>" />
      </TD>
    </TR>
  </TABLE>
</BODY>

</HTML>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>