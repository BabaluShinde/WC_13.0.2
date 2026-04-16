<%@ page 
   session="false"
   import="wt.util.WTProperties"
   import="com.ptc.core.appsec.CSRFProtector"
%>
<%@page import="wt.util.HTMLEncoder"%>

<%
   String strCodeBase = WTProperties.getLocalProperties().getProperty("wt.server.codebase", null);
   String dtiurl = strCodeBase + "/servlet/DTIActionServlet?actionName=attachmentDownload";
  
   String closeURL = strCodeBase + "/netmarkets/jsp/attachments/download/closePopupForIE7.jsp";
   
   String subAction = request.getParameter("subAction");
   if(subAction !=null){
   	dtiurl += "&subAction="+ HTMLEncoder.encodeForHTMLAttribute(subAction);
   }
  
   String attachmentsDataKey = request.getParameter("AttachmentsDataKey");
      if(attachmentsDataKey !=null){
      	dtiurl += "&AttachmentsDataKey="+ HTMLEncoder.encodeForHTMLAttribute(attachmentsDataKey);
   }
   
%>

<HTML>

<HEADER>
<SCRIPT language="JavaScript">

     var downloadComplete = false;
     try {
         if ((window.opener.Ext && window.opener.Ext.isGCF)|| (window.opener.top.Ext && window.opener.top.Ext.isGCF  )) {
             window.location.href="<%= HTMLEncoder.encodeForJavascript(dtiurl) %>";
             downloadComplete = true;
         }
     }catch(e) {
        //May not have access to interogate opener for isGCF flag
     }
     
     if (!downloadComplete) {  
        if (window.parent && window.parent.windchillmain) { 
           // for structure doc where popup is launched from a sub-frame instead of main window.
           // when main window refreshed, the sub-frame object for IE is still exist but not accessable?%@!
           // therefore we need the popup to remember a link (var windchillmain) to the main window,
           // and use a iframe to execute the download

            window.parent.windchillmain.location.href = "<%= HTMLEncoder.encodeForJavascript(dtiurl) %>";
            window.parent.open("<%= closeURL %>", "_self");
        } else if (window.opener != null) {
            <%-- > This if the action type is popup.< --%>
            window.opener.location.href = "<%= HTMLEncoder.encodeForJavascript(dtiurl) %>";
            window.open("<%= closeURL %>", "_self"); 
        } else {
            window.location.href= "<%= HTMLEncoder.encodeForJavascript(dtiurl) %>";
        }
    }
</SCRIPT>
</HEADER>

<BODY >

</BODY>

</HTML>
