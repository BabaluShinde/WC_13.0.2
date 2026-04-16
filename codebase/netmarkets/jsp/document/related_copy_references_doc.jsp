<%@ include file="/netmarkets/jsp/util/context.jsp"%>

<%@ page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@ page import="com.ptc.windchill.enterprise.doc.commands.RelatedObjectsCommand"%>

<%	
	NmCommandBean commandBean = new NmCommandBean();
	commandBean.setCompContext(nmcontext.getContext().toString());
	commandBean.setRequest(request);
	
	RelatedObjectsCommand.getDocumentsFromSelectedInOpenerDependencyLinks(commandBean);    
	request.setAttribute("existingcommandBean",commandBean);	
%>

<jsp:forward page="/netmarkets/jsp/object/copy_main.jsp">
	<jsp:param name="list" value="true"/>
</jsp:forward>