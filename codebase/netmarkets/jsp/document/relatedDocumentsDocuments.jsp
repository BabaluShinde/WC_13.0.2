<% request.setAttribute(NmAction.SHOW_CONTEXT_INFO, "false"); %>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>

<%@ page import="com.ptc.windchill.enterprise.doc.documentResource" %>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.ptc.windchill.enterprise.doc.documentResource" />

<fmt:message var="referencesDocTableHeader"   key="REFERENCES_DOC_TABLE_HEADER" />

<%-->Get the Described By relation NmHTMLTable from the command<--%>
<jca:describeTable var="references" id="document.relatedDocsReferencesDocuments.list" type="wt.doc.WTDocument"
				   label="${referencesDocTableHeader}"  configurable="true" >
  <jca:setComponentProperty key="actionModel" value="relatedDocumentRefToolBar"/>
  <jca:describeColumn id="type_icon"/>
  <jca:describeColumn id="number"/>
  <jca:describeColumn id="version"/>
  <jca:describeColumn id="editComment" mode="EDIT" dataUtilityId="nmActions" >
      <jca:setComponentProperty key="actionName"  value="related_addEditCommentWizard"/>
      <jca:setComponentProperty key="objectType"  value="document"/>
   </jca:describeColumn>
  <jca:describeColumn id="infoPageAction"/>
  <jca:describeColumn id="name"/>
  <jca:describeColumn id="containerName"/>
      <jca:describeColumn id="nmActions">
               <jca:setComponentProperty key="actionModel" value="reference docs row actions"/>
          </jca:describeColumn>
  <jca:describeColumn id="state"/>
  <jca:describeColumn id="lastModified"/>
  <jca:describeColumn id="linkDescription"/>
</jca:describeTable>

<c:set target="${references.properties}" property="selectable" value="true"/>

<%-->Get a component model for our table<--%>
<jca:getModel var="referencesTableModel" descriptor="${references}"
              serviceName="com.ptc.windchill.enterprise.doc.commands.DocDocServiceCommand"
              methodName="getDependsOnDocuments" >
<jca:addServiceArgument type="wt.doc.WTDocument" value="${param.oid}"/>
</jca:getModel>

<%-->Get the NmHTMLTable from the command<--%>
<jca:renderTable model="${referencesTableModel}" pageLimit="0" showCount="true" helpContext="doc_references_doc"/>



<%@ include file="/netmarkets/jsp/util/end.jspf"%>