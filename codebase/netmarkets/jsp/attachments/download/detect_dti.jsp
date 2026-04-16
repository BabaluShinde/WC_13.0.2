<%@ include file="/netmarkets/jsp/util/beginPopup.jspf"%>

<%@ page
    import="com.ptc.windchill.enterprise.servlets.AttachmentsDownloadDirectionServlet,wt.httpgw.URLFactory, wt.util.WTProperties, wt.util.HTMLEncoder"
%>


<%
    String strUpdateURL = (String) request.getAttribute(AttachmentsDownloadDirectionServlet.REQUEST_KEY_URI) + "?";
    String paraHolderOID = request.getParameter(AttachmentsDownloadDirectionServlet.PARA_CONTENT_HOLDER_OID);
    boolean paramAdded = false;

    if (paraHolderOID != null) {
        strUpdateURL += AttachmentsDownloadDirectionServlet.PARA_CONTENT_HOLDER_OID + "=" + paraHolderOID;
        paramAdded = true;

    }
    String paraItemOIDs = request.getParameter(AttachmentsDownloadDirectionServlet.PARA_CONTENT_ITEM_OIDS);
    if (paraItemOIDs != null) {
        if(paramAdded){
            strUpdateURL += "&" + AttachmentsDownloadDirectionServlet.PARA_CONTENT_ITEM_OIDS + "=" + paraItemOIDs;
        }
        else{
            strUpdateURL +=  AttachmentsDownloadDirectionServlet.PARA_CONTENT_ITEM_OIDS + "=" + paraItemOIDs;
        }


    }
    String role = request.getParameter("role");
    if (role != null) {
       strUpdateURL += "&" + "role"+ "=" + role;
    }
    strUpdateURL += "&" + AttachmentsDownloadDirectionServlet.PARA_DTI_STATUS + "=";

    boolean ignoreDTIInstallationCheck = false;
    boolean ignoreDTIInstallationCheckForAllBrowsers = false;
    WTProperties properties = WTProperties.getLocalProperties ();
    ignoreDTIInstallationCheck = properties.getProperty ("wt.doc.ignoreDTIInstallationCheck", false);
    ignoreDTIInstallationCheckForAllBrowsers = properties.getProperty ("wt.doc.ignoreDTIInstallationCheckForAllBrowsers", true);
%>

<HTML>

<HEAD>
<SCRIPT language="JavaScript" src="<%=new URLFactory().getHREF("netmarkets/javascript/util/main.js",true)%>"></SCRIPT>
<SCRIPT language="JavaScript" SRC="<%=new URLFactory().getHREF("templates/cadx/common/trlUtils.js")%>"></SCRIPT>

<!-- Mozilla detection -->
<SCRIPT language="JavaScript">
    var hasDTI = false;
    if(navigator.mimeTypes){
        var mimeType = navigator.mimeTypes["application/wcdti"];
        if(mimeType){
            hasDTI=true;
        }
    }
</SCRIPT>
<!-- IE detection -->
<SCRIPT language="VBScript">
    on error resume next
    set obj = CreateObject("wtTaskRun.clsInfo")
    hasDTI = obj.MimeSupported
    set obj = Nothing
</SCRIPT>
<SCRIPT language="JavaScript">
    var updateURL = "<%= HTMLEncoder.encodeForJavascript(strUpdateURL) %>";
    var ignoreHasDTICheck = <%= ignoreDTIInstallationCheck %>;
    var ignoreHasDTICheckForAllBrowsers = <%= ignoreDTIInstallationCheckForAllBrowsers %>;
    var isChrome = navigator.userAgent.toLowerCase().indexOf('chrome') > -1;

    if (hasDTI==true || ignoreHasDTICheckForAllBrowsers==true || (isChrome==true && ignoreHasDTICheck==true)) {
        updateURL += "<%= AttachmentsDownloadDirectionServlet.PARA_STATUS_INSTALLED %>";
    } else {
        updateURL += "<%= AttachmentsDownloadDirectionServlet.PARA_STATUS_NOT_INSTALLED %>";
    } // end if hasDTI
    window.location.replace(updateURL);

    WaitAndClose();

</SCRIPT>

</HEAD>

<BODY>

</BODY>

</HTML>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
