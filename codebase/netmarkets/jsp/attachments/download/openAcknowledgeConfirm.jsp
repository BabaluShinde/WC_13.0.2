<%//@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
 
  <%@ page 
 	import="com.ptc.windchill.enterprise.servlets.AttachmentsDownloadDirectionServlet"
  	import="java.util.Map"
  	import="wt.access.configuration.SecurityLabel"
  	import="com.ptc.netmarkets.util.misc.NmAction"
    import="com.ptc.netmarkets.util.misc.NmActionServiceHelper"
    import="com.ptc.netmarkets.model.NmOid"
    import="java.util.UUID"
 %>
 
 <%
 String uniquePageId = UUID.randomUUID().toString();
 String forwaredUrl = (String)request.getAttribute(AttachmentsDownloadDirectionServlet.PARA_CACHING_URL);
 String oid = (String)request.getParameter(AttachmentsDownloadDirectionServlet.PARA_CONTENT_HOLDER_OID);
 Map<String,Object[]> securityLabelSet = (Map<String,Object[]>)request.getAttribute(AttachmentsDownloadDirectionServlet.SECURITY_LABEL_MAP);
 Object iPref = request.getAttribute(AttachmentsDownloadDirectionServlet.DOWNLOAD_STATE_FORMAT);
 
 session.setAttribute(AttachmentsDownloadDirectionServlet.PARA_CACHING_URL+"_"+uniquePageId,forwaredUrl);
 session.setAttribute(AttachmentsDownloadDirectionServlet.SECURITY_LABEL_MAP+"_"+uniquePageId,securityLabelSet);
 session.setAttribute(AttachmentsDownloadDirectionServlet.DOWNLOAD_STATE_FORMAT+"_"+uniquePageId,iPref);

 NmAction action = NmActionServiceHelper.service.getAction("attachments","acknowledgeConfirmWizard"); 

 action.setContextObject(new NmOid(oid));
 action.addParam("uniquePageId", uniquePageId);
 
 %>
 
 <script>
 var win = window.top;
 if(window.opener){
	win = window.opener;
 }
 var iframe = win.document.getElementById("acknowledgeIframe");
 if(iframe == null){
	try {  
		iframe = win.document.createElement('<iframe name="acknowledgeIframe">');  
	} catch (ex) {  
		iframe = win.document.createElement('iframe');  
	}  
	iframe.setAttribute("id", "acknowledgeIframe"); 
	iframe.setAttribute("src", ""); 
	iframe.setAttribute("name", "acknowledgeIframe"); 
	iframe.setAttribute("style", "display:none");
	iframe.setAttribute("width", "0");
	iframe.setAttribute("height", "0");
	win.document.body.appendChild(iframe); 
	
 }
 
 PTC.onReady( function () {
 <%=action.getActionUrlExternal().substring(11).replaceAll("'", "\"")%>;
 });
 </script>
 
<%@ include file="/netmarkets/jsp/util/end.jspf" %>
