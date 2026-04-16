<%@ page 
    session="false"
    import="com.ptc.windchill.enterprise.servlets.AttachmentsDownloadDirectionServlet"
    import="com.ptc.windchill.enterprise.attachments.server.AttachmentsDownloadDirectionResponse"
    import="com.ptc.windchill.enterprise.attachments.server.AttachmentsDownloadDirectionResponseItem"
        import="wt.util.WTProperties"
%>

<%
    AttachmentsDownloadDirectionResponse addResponse = (AttachmentsDownloadDirectionResponse)
        request.getAttribute(AttachmentsDownloadDirectionServlet.REQUEST_KEY_DOWNLOAD_DIRECTION);
        
    AttachmentsDownloadDirectionResponseItem theOne = addResponse.getSingletonItem(); // should be only one for browser download
    
    String strCodeBase = WTProperties.getLocalProperties().getProperty("wt.server.codebase", null);
    String closeURL = strCodeBase + "/netmarkets/jsp/attachments/download/closePopupForIE7.jsp";
%>

<HTML>

<HEAD></HEAD>

<BODY>

<SCRIPT type="text/javascript">
    var downloadURL = "<%= theOne.getDownloadURLString() %>";
    
    var crossDomainParent = false;
    try {
        //If the download is being embedded in an iframe from a page in another domain then
        //accessing window.parent properties will throw an exception
        crossDomainParent = window.parent && !window.parent.location.href;
    }
    catch (e) {
        crossDomainParent = true;
    }
    /*var downloadComplete = false;
    
    //Chrome-frame bug, cannot set the url of the main window to a download file
     // because it will cause the js engine to freeze
     try {
         if ((window.opener.Ext && window.opener.Ext.isGCF)|| (window.opener.top.Ext && window.opener.top.Ext.isGCF  )) {
             window.location.href=downloadURL;
             downloadComplete = true;
         }
     }catch(e) {
        //May not have access to interogate opener for isGCF flag
     }
         
    if (!downloadComplete) {*/
        if (crossDomainParent) {        
            window.location.href = downloadURL;
        }
        else if (window.parent && window.parent.windchillmain) {
            // for structure doc where popup is launched from a sub-frame instead of main window.
            // when main window refreshed, the sub-frame object for IE is still exist but not accessable?%@!
            // therefore we need the popup to remember a link (var windchillmain) to the main window,
            // and use a iframe to execute the download       

            window.parent.windchillmain.location.href = downloadURL;
            window.parent.open("<%= closeURL %>", "_self");        
        } else if (window.opener){
            <%-- > This if the action type is popup.< --%>
            window.opener.top.location.href = downloadURL;
            window.open("<%= closeURL %>", "_self");

        } else {
            window.top.location.href = downloadURL;
        } // end if
    //}
</SCRIPT>

</BODY>

</HTML>
