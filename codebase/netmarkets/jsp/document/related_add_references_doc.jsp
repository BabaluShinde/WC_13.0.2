<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ page import="com.ptc.windchill.enterprise.doc.documentResource" %>
<%@ page import="com.ptc.windchill.enterprise.doc.commands.RelatedObjectsCommand" %>
<%@ page import="wt.doc.WTDocument" %>
<%@ page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>


<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>
<fmt:setBundle basename="com.ptc.windchill.enterprise.doc.documentResource" />
<fmt:message var="referencesDocTableHeader" key="<%= documentResource.REFERENCES_DOC_TABLE_HEADER%>" />

<%
    NmCommandBean cb = new NmCommandBean();
    cb.setCompContext(nmcontext.getContext().toString());
    cb.setRequest(request);
    
    String containerReferenceString =  RelatedObjectsCommand.getContainersToSearch(cb.getContainerRef());
    WTDocument document = (WTDocument) cb.getPrimaryOid().getWtRef().getObject();
    String number = document.getNumber().toString();
    String tableLabel = (String) pageContext.findAttribute("referencesDocTableHeader");
    
    request.setAttribute("tableLabel",tableLabel);
    request.setAttribute("number",number);
%>
                 
<wctags:genericPicker id="related_add_references_doc" multiSelect="true" inline="true"
        pickerCallback="doNothing" pickerTitle="${tableLabel}"
        objectType="wt.doc.WTDocument" componentId="RelatedObjectAddAssociation"
        containerRef="<%=containerReferenceString%>"
        baseWhereClause="(number!='${number}')"/>
        
        <%-->  baseWhereClause="((number!='${number}') & (template.templated='false'))"/> <--%>

        <%@ include file="/netmarkets/jsp/util/end.jspf"%>