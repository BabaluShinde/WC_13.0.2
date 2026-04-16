<% request.setAttribute(NmAction.SHOW_CONTEXT_INFO, "false"); %>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>

<%@ page import="com.ptc.windchill.enterprise.doc.documentResource" %>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.ptc.windchill.enterprise.doc.documentResource" />

<fmt:message var="referencedByDocTableHeader" key="REFERENCED_BY_DOC_TABLE_HEADER" />


<%-->Get the References By relation NmHTMLTable from the command<--%>
<jca:describeTable var="referencedBy" id="document.relatedDocsReferencedByDocuments.list" type="wt.doc.WTDocument"
                   label="${referencedByDocTableHeader}"  configurable="true">
  <jca:describeColumn id="type_icon"/>
  <jca:describeColumn id="number"/>
  <jca:describeColumn id="version"/>
  <jca:describeColumn id="infoPageAction"/>
  <jca:describeColumn id="name"/>
  <jca:describeColumn id="containerName"/>
  <jca:describeColumn id="state"/>
  <jca:describeColumn id="lastModified"/>
  <jca:describeColumn id="nmActions">
        <jca:setComponentProperty key="actionModel" value="relatedParts DescRef actions"/>
  </jca:describeColumn>
</jca:describeTable>

<%-->Get a component model for our table<--%>
<jca:getModel var="referencedByTableModel" descriptor="${referencedBy}"
              serviceName="com.ptc.windchill.enterprise.doc.commands.DocDocServiceCommand"
              methodName="getHasDependentDocuments" >
<jca:addServiceArgument type="wt.doc.WTDocument" value="${param.oid}"/>
</jca:getModel>

<%-->Get the NmHTMLTable from the command<--%>
<jca:renderTable model="${referencedByTableModel}" pageLimit="0" showCount="true" helpContext="doc_referenced_by"/>


<%@ include file="/netmarkets/jsp/util/end.jspf"%>