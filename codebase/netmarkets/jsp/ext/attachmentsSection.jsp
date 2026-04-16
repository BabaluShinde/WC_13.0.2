<%@ page import="com.ptc.windchill.enterprise.attachments.attachmentsResource" %>
<%@ page import="wt.preference.PreferenceHelper" %>
<%@ page import="wt.preference.PreferenceClient" %>

<%@ page import="wt.fc.ReferenceFactory" %>
<%@ page import="wt.fc.WTReference" %>
<%@ page import="wt.workflow.work.WorkItem" %>
<%@ page import="wt.fc.ObjectReference" %>
<%@ page import="wt.fc.WTReference" %>
<%@ page import="wt.content.ContentHolder" %>
<%@ page import="wt.fc.WTObject" %>
<%@ page import="wt.doc.WTDocumentMaster" %>
<%@ page import="wt.doc.WTDocument" %>
<%@ page import="wt.fc.Persistable" %>
<%@ page import="wt.fc.PersistentReference" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ taglib prefix="wc" uri="http://www.ptc.com/windchill/taglib/core" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>

<% request.setAttribute(NmAction.SHOW_CONTEXT_INFO, "true"); %>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>

<!-- Get the localized strings from the resource bundle -->
<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.ptc.windchill.enterprise.attachments.attachmentsResource" />

<fmt:message var="att_name"            key="<%= attachmentsResource.ATTACHMENT_NAME %>" />
<fmt:message var="att_number"          key="<%= attachmentsResource.ATTACHMENT_NUMBER %>" />
<fmt:message var="att_format"          key="<%= attachmentsResource.FORMAT %>" />
<fmt:message var="att_description"     key="<%= attachmentsResource.ATTACHMENT_DESCRIPTION %>" />
<fmt:message var="primaryTableTitle"   key="<%= attachmentsResource.PRIMARY_CONTENT %>" />
<fmt:message var="secondaryTableTitle" key="<%= attachmentsResource.ATTACHMENT_LABEL %>" />
<fmt:message var="att_comments"        key="<%= attachmentsResource.ATTACHMENT_COMMENTS %>" />
<fmt:message var="att_distributable"   key="<%= attachmentsResource.ATTACHMENT_EXTERNALDISTRIBUTION %>" />
<fmt:message var="att_authoredBy"      key="<%= attachmentsResource.ATTACHMENT_AUTHOREDBY %>" />
<fmt:message var="att_lastAuthored"    key="<%= attachmentsResource.ATTACHMENT_LASTAUTHORED %>" />
<fmt:message var="att_fileVersion"     key="<%= attachmentsResource.ATTACHMENT_FILEVERSION %>" />
<fmt:message var="att_toolName"        key="<%= attachmentsResource.ATTACHMENT_TOOLNAME %>" />
<fmt:message var="att_toolVersion"     key="<%= attachmentsResource.ATTACHMENT_TOOLVERSION %>" />

<!-- get optional column preferences -->
<%
    String oid = request.getParameter("oid");

    ReferenceFactory rf = new ReferenceFactory();
    Persistable obj = rf.getReference(oid).getObject();

    WorkItem wi = (WorkItem) obj;
    Persistable pbo = wi.getPrimaryBusinessObject().getObject();

    if (pbo instanceof wt.content.ContentHolder) {
        ContentHolder contentHolder = (ContentHolder) pbo;

        // Set the actual ContentHolder object as service argument
        request.setAttribute("oid1", contentHolder);

        // Set the WTReference version of the context object
        WTReference pboRef = rf.getReference(pbo);
        request.setAttribute("contextObject", pboRef);
    } else {
        System.out.println("PBO is not a ContentHolder");
    }
%>

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

<c:set var="cSortNumber"    value="<%= prefSortNumber    %>" />
<c:set var="cComments"      value="<%= prefComments      %>" />
<c:set var="cDistributable" value="<%= prefDistributable %>" />
<c:set var="cAuthoredBy"    value="<%= prefAuthoredBy    %>" />
<c:set var="cLastAuthored"  value="<%= prefLastAuthored  %>" />
<c:set var="cFileVersion"   value="<%= prefFileVersion   %>" />
<c:set var="cToolName"      value="<%= prefToolName      %>" />
<c:set var="cToolVersion"   value="<%= prefToolVersion   %>" />


<c:set var="roleType" value="${param.role}"/>

<c:choose>
    <c:when test='${roleType == "PRIMARY"}'>
        <c:set var="tableLabel" value="${primaryTableTitle}"/>
        <c:set var="role" value="<%= wt.content.ContentRoleType.PRIMARY %>"/>
        <c:set var="tableID" value="attachments.table.primary" />
        <c:set var="helpLink" value="PrimaryAttachmentTableHelp" />
    </c:when>
    <c:otherwise>
        <c:set var="tableLabel" value="${secondaryTableTitle}"/>
        <c:set var="role" value="<%= wt.content.ContentRoleType.SECONDARY %>"/>
        <c:set var="tableID" value="attachments.table.secondary" />
        <c:set var="helpLink" value="SecondaryAttachmentsTableHelp" />
    </c:otherwise>
</c:choose>

<!-- Definition of the Attachments Table -->
<jca:describeTable var="tableDescriptor" id="${tableID}" type="wt.content.ContentItem" label="${tableLabel}" configurable="false" >

<c:choose>
    <c:when test='${roleType != "PRIMARY"}'>
        <jca:setComponentProperty key="actionModel" value="attachments readonly table toolbar actions"/>
    </c:when>
</c:choose>

    <c:if test="${cSortNumber}">
        <c:if test='${roleType != "PRIMARY"}'>
            <jca:describeColumn id="lineNumber" />
        </c:if>
    </c:if>
    <jca:describeColumn id="attachmentsName" label="${att_name}" />

    <jca:describeColumn id="formatIcon"      sortable="false"  />
    <jca:describeColumn id="formatName"      label="${att_format}" />
    <jca:describeColumn id="description"     label="${att_description}" />
    <jca:describeColumn id="thePersistInfo.modifyStamp" />
    <jca:describeColumn id="modifier"        need="modifiedBy" targetObject="modifiedBy"/>
    <c:if test="${cComments}">				
        <jca:describeColumn id="comments"        label="${att_comments}" />
    </c:if>
    <c:if test="${cDistributable}">
	<jca:describeColumn id="distributable"   label="${att_distributable}" />
    </c:if>
    <c:if test="${cAuthoredBy}">
	<jca:describeColumn id="authoredBy"      label="${att_authoredBy}" />
    </c:if>
    <c:if test="${cLastAuthored}">
	<jca:describeColumn id="lastAuthored"    label="${att_lastAuthored}" />
    </c:if>
    <c:if test="${cFileVersion}">
	<jca:describeColumn id="fileVersion"     label="${att_fileVersion}" />
    </c:if>
    <c:if test="${cToolName}">
	<jca:describeColumn id="toolName"        label="${att_toolName}" />
    </c:if>
    <c:if test="${cToolVersion}">
	<jca:describeColumn id="toolVersion"     label="${att_toolVersion}" />
    </c:if>

</jca:describeTable>



<!-- Define the query for the Attachments table -->
<jca:getModel var="tableModel" descriptor="${tableDescriptor}"
               serviceName="com.ptc.windchill.enterprise.attachments.server.AttachmentsService"
               methodName="getAttachments">
			   
    <jca:addServiceArgument value="${oid1}" type="wt.content.ContentHolder"/>
    <jca:addServiceArgument value="${role}"/>
</jca:getModel>

<!-- Render the table -->
<jca:renderTable model="${tableModel}" helpContext="${helpLink}" />

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
