<%@ page 
	import="com.ptc.windchill.enterprise.servlets.AttachmentsDownloadDirectionServlet,com.ptc.netmarkets.util.beans.NmCommandBean"
%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%
	String strUpdateURL = (String) request.getAttribute(AttachmentsDownloadDirectionServlet.REQUEST_KEY_URI) + "?";
	String paraHolderOID = request.getParameter(AttachmentsDownloadDirectionServlet.PARA_CONTENT_HOLDER_OID);
	
	if (paraHolderOID != null) {
		strUpdateURL += "&" + AttachmentsDownloadDirectionServlet.PARA_CONTENT_HOLDER_OID + "=" + paraHolderOID;
	}
	String[] sOID = request.getParameterValues(NmCommandBean.ElementName.SELECTED_OID);
	
	if (sOID != null){
		for (int i=0;i<sOID.length;i++){	
			strUpdateURL += "&" + NmCommandBean.ElementName.SELECTED_OID + "=" + sOID[i];			
		}	
	}
	String paraItemOIDs = request.getParameter(AttachmentsDownloadDirectionServlet.PARA_CONTENT_ITEM_OIDS);
	if (paraItemOIDs != null) {
		strUpdateURL += "&" + AttachmentsDownloadDirectionServlet.PARA_CONTENT_ITEM_OIDS + "=" + paraItemOIDs;
	}
	strUpdateURL += "&" + AttachmentsDownloadDirectionServlet.PARA_JRE_STATUS + "=";
%>

<script type='text/javascript' src="netmarkets/javascript/util/deployJava.js"></script>
<script type='text/javascript' src="netmarkets/javascript/attachments/detect_jre.js"></script>

<!-- Mozilla detection -->
<SCRIPT language="JavaScript">
	var hasJRE = (navigator.mimeTypes && navigator.mimeTypes["application/x-java-applet;version=1.4.2"]) || (getJREsValue() > 0);
</SCRIPT>
<!-- IE detection -->
<!--<SCRIPT language="VBScript">
	on error resume next
	hasJRE_JavaPlugin = not IsNull(CreateObject("JavaPlugin"))
	hasJRE_JavaBeansBridge = not IsNull(CreateObject("JavaSoft.JavaBeansBridge"))
	hasJRE_JavaWebStart = not IsNull(CreateObject("JavaWebStart.isInstalled"))
	hasJRE = (hasJRE_JavaPlugin Or hasJRE_JavaBeansBridge Or hasJRE_JavaWebStart)
</SCRIPT>-->
<SCRIPT language="JavaScript">
	var updateURL = "<%= strUpdateURL %>";
	if (hasJRE) {
		updateURL += "<%= AttachmentsDownloadDirectionServlet.PARA_STATUS_INSTALLED %>";
	} else {
		updateURL += "<%= AttachmentsDownloadDirectionServlet.PARA_STATUS_NOT_INSTALLED %>";
	} // end if hasJRE
	window.location.replace(updateURL);
</SCRIPT>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
