<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mvc" uri="http://www.ptc.com/windchill/taglib/mvc" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>


<%@ page 
	import="com.ptc.windchill.enterprise.servlets.AttachmentsDownloadDirectionServlet"
	import="com.ptc.windchill.enterprise.servlets.DownloadStateManager"
	import="com.ptc.windchill.enterprise.attachments.attachmentsResource"
	import="wt.util.WTProperties"
	import="wt.util.WTMessage"
	import="java.util.Map"
	import="wt.util.LocalizableMessage"
	import="wt.fc.EnumeratedType"
	import="wt.session.SessionHelper"
	import="java.util.Set"
	import="java.util.Iterator"
	import="wt.access.configuration.SecurityLabel"
%>
<%@page contentType="text/html;charset=UTF-8"%>
<%
	String uniquePageId = (String)request.getParameter("uniquePageId");
 	String strCodeBase = WTProperties.getLocalProperties().getProperty("wt.server.codebase", null);
	
	String forwaredUrl = (String)session.getAttribute(AttachmentsDownloadDirectionServlet.PARA_CACHING_URL+"_"+uniquePageId);
	String oid = (String)request.getParameter(AttachmentsDownloadDirectionServlet.PARA_CONTENT_HOLDER_OID);
	Map<String,Object[]> securityLabelSet = (Map<String,Object[]>)session.getAttribute(AttachmentsDownloadDirectionServlet.SECURITY_LABEL_MAP+"_"+uniquePageId);
	Object iPref = session.getAttribute(AttachmentsDownloadDirectionServlet.DOWNLOAD_STATE_FORMAT+"_"+uniquePageId);
	
	session.removeAttribute(AttachmentsDownloadDirectionServlet.PARA_CACHING_URL+"_"+uniquePageId);
	session.removeAttribute(AttachmentsDownloadDirectionServlet.SECURITY_LABEL_MAP+"_"+uniquePageId);
	session.removeAttribute(AttachmentsDownloadDirectionServlet.DOWNLOAD_STATE_FORMAT+"_"+uniquePageId);
	Map securityLabelMap = AttachmentsDownloadDirectionServlet.getSecurityLabelMap(oid,securityLabelSet,SessionHelper.getLocale());
	
	request.setAttribute("securityLabelMap", securityLabelMap);
	
	String strParam = AttachmentsDownloadDirectionServlet.PARA_ACK_STATUS;

	String closeURL = strCodeBase + "/netmarkets/jsp/attachments/download/closePopupForIE7.jsp";
	
	boolean isShowSL = false;
	String showSL = WTProperties.getLocalProperties().getProperty("wt.attachments.acknowledge.securitylabel.show", null);
	if("true".equals(showSL)){
		isShowSL = true;
	}
	
	String detailMsg = "";
	Iterator it = securityLabelMap.entrySet().iterator();   
    while (it.hasNext()){   
    	Map.Entry entry = (Map.Entry)it.next();
    	if(isShowSL){
			detailMsg = detailMsg + entry.getKey() + "\n";
		}
		detailMsg = detailMsg + entry.getValue() + "\n \n";   
    } 

%>

<SCRIPT LANGUGE="JavaScript">
var downloadFormat = "<%=iPref%>";
var appletFormat = "<%=DownloadStateManager.I_PREFERENCE_APPLET%>";
var forwaredUrl = "<%=forwaredUrl%>";
var openersopener = window.opener.opener;
window.opener.open("<%= closeURL %>", "_self");

function cancelClicked() {
    window.close();
}

function okClicked() {
   document.mainform.action=forwaredUrl;
   document.mainform.<%= strParam %>.value="<%= AttachmentsDownloadDirectionServlet.PARA_ACK_STATUS_ACCEPT %>";
   if(downloadFormat == appletFormat){
		document.mainform.target="_blank"
   }else{
		document.mainform.target="acknowledgeIframe";
   }
   document.mainform.submit();
   
   window.close();
   return false;
}


function onLoad() {
  window.resizeTo(600, 600);
}

function rejectClicked() {
    document.mainform.accept.checked = false;
}

function acceptClicked() {
    document.mainform.reject.checked = false;
}



function resizeTextArea() {
	
	var txtArea = document.getElementById("ack_detail");
	var xlines=document.documentElement.clientWidth/7;
	var ylines=document.documentElement.clientHeight/28;
	txtArea.cols=xlines;
	txtArea.rows=ylines;

}
Event.observe(window, 'load',onLoad); 
Event.observe(window, 'resize', resizeTextArea);


</SCRIPT>

 <fmt:setLocale value="${localeBean.locale}"/>
 <fmt:setBundle basename="com.ptc.windchill.enterprise.attachments.attachmentsResource" />
 <fmt:message var="download_secured_label" key="DOWNLOAD_SECURED_CONFIRM" /> 
 <input type="hidden" name="<%= strParam %>" />

 <table>
 	<tr>
 		<td td align="left">
 			<label id="textLabel1" name="textLabel1"> ${download_secured_label}</label>
 		</td>
 	</tr>
 	<tr>
 		<td>
 			<br>
 		</td>
 	</tr>
 	<tr>
 		<td align="left">
			<textArea id="ack_detail" name="ack_detail" cols=85 rows=10><%=detailMsg%></textArea>
		</td>
	</tr>
 </table>
 <br>
 

