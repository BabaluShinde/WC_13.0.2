<%@ page import="com.ptc.windchill.enterprise.attachments.attachmentsResource" %>
<%@ page import="wt.preference.PreferenceHelper" %>
<%@ page import="wt.preference.PreferenceClient" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ taglib prefix="wc" uri="http://www.ptc.com/windchill/taglib/core" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>

<% request.setAttribute(NmAction.SHOW_CONTEXT_INFO, "true"); %>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>

<!-- Get the localized strings from the resource bundle -->
<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.ptc.windchill.enterprise.attachments.attachmentsResource" />

<fmt:message var="att_name"            key="<%= attachmentsResource.ATTACHMENT_NAME %>" scope="request"/>
<fmt:message var="att_number"          key="<%= attachmentsResource.ATTACHMENT_NUMBER %>" scope="request"/>
<fmt:message var="att_format"          key="<%= attachmentsResource.FORMAT %>" scope="request"/>
<fmt:message var="att_description"     key="<%= attachmentsResource.ATTACHMENT_DESCRIPTION %>" scope="request"/>
<fmt:message var="primaryTableTitle"   key="<%= attachmentsResource.PRIMARY_CONTENT %>" scope="request"/>
<fmt:message var="secondaryTableTitle" key="<%= attachmentsResource.ATTACHMENT_LABEL %>" scope="request"/>
<fmt:message var="att_comments"        key="<%= attachmentsResource.ATTACHMENT_COMMENTS %>" scope="request"/>
<fmt:message var="att_distributable"   key="<%= attachmentsResource.ATTACHMENT_EXTERNALDISTRIBUTION %>" scope="request"/>
<fmt:message var="att_authoredBy"      key="<%= attachmentsResource.ATTACHMENT_AUTHOREDBY %>" scope="request"/>
<fmt:message var="att_lastAuthored"    key="<%= attachmentsResource.ATTACHMENT_LASTAUTHORED %>" scope="request"/>
<fmt:message var="att_fileVersion"     key="<%= attachmentsResource.ATTACHMENT_FILEVERSION %>" scope="request"/>
<fmt:message var="att_toolName"        key="<%= attachmentsResource.ATTACHMENT_TOOLNAME %>" scope="request"/>
<fmt:message var="att_toolVersion"     key="<%= attachmentsResource.ATTACHMENT_TOOLVERSION %>" scope="request"/>

<!-- get optional column preferences -->
<%
    Boolean prefSortNumber    = (Boolean)PreferenceHelper.service.getValue("/com/ptc/windchill/enterprise/attachments/optionalAttributes/sortNumber",    PreferenceClient.WINDCHILL_CLIENT_NAME);
    Boolean prefComments      = (Boolean)PreferenceHelper.service.getValue("/com/ptc/windchill/enterprise/attachments/optionalAttributes/comments",      PreferenceClient.WINDCHILL_CLIENT_NAME);
    Boolean prefDistributable = (Boolean)PreferenceHelper.service.getValue("/com/ptc/windchill/enterprise/attachments/optionalAttributes/distributable", PreferenceClient.WINDCHILL_CLIENT_NAME);
    Boolean prefAuthoredBy    = (Boolean)PreferenceHelper.service.getValue("/com/ptc/windchill/enterprise/attachments/optionalAttributes/authoredBy",    PreferenceClient.WINDCHILL_CLIENT_NAME);
    Boolean prefLastAuthored  = (Boolean)PreferenceHelper.service.getValue("/com/ptc/windchill/enterprise/attachments/optionalAttributes/lastAuthored",  PreferenceClient.WINDCHILL_CLIENT_NAME);
    Boolean prefFileVersion   = (Boolean)PreferenceHelper.service.getValue("/com/ptc/windchill/enterprise/attachments/optionalAttributes/fileVersion",   PreferenceClient.WINDCHILL_CLIENT_NAME);
    Boolean prefToolName      = (Boolean)PreferenceHelper.service.getValue("/com/ptc/windchill/enterprise/attachments/optionalAttributes/toolName",      PreferenceClient.WINDCHILL_CLIENT_NAME);
    Boolean prefToolVersion   = (Boolean)PreferenceHelper.service.getValue("/com/ptc/windchill/enterprise/attachments/optionalAttributes/toolVersion",   PreferenceClient.WINDCHILL_CLIENT_NAME);
%>
<c:set var="cSortNumber"    value="<%= prefSortNumber    %>" scope="request"/>
<c:set var="cComments"      value="<%= prefComments      %>" scope="request"/>
<c:set var="cDistributable" value="<%= prefDistributable %>" scope="request"/>
<c:set var="cAuthoredBy"    value="<%= prefAuthoredBy    %>" scope="request"/>
<c:set var="cLastAuthored"  value="<%= prefLastAuthored  %>" scope="request"/>
<c:set var="cFileVersion"   value="<%= prefFileVersion   %>" scope="request"/>
<c:set var="cToolName"      value="<%= prefToolName      %>" scope="request"/>
<c:set var="cToolVersion"   value="<%= prefToolVersion   %>" scope="request"/>

<jsp:include page="/netmarkets/jsp/attachments/attachments_table_include.jsp" />

<!-- Define the query for the Attachments table -->
<jca:getModel var="tableModel" descriptor="${tableDescriptor}"
               serviceName="com.ptc.windchill.enterprise.attachments.server.AttachmentsService"
               methodName="getAttachments">
    <jca:addServiceArgument value="${param.oid}" type="wt.content.ContentHolder"/>
    <jca:addServiceArgument value="${role}"/>
</jca:getModel>

<!-- Render the table -->
<jca:renderTable model="${tableModel}" helpContext="${helpLink}" />

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
