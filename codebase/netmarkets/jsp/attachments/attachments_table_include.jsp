<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ taglib prefix="wc" uri="http://www.ptc.com/windchill/taglib/core" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>

<%@ include file="/netmarkets/jsp/util/begin.jspf"%>

<c:set var="roleType" value="${param.role}" scope="request"/>

<c:choose>
    <c:when test='${roleType == "PRIMARY"}'>
        <c:set var="tableLabel" value="${primaryTableTitle}" scope="request"/>
        <c:set var="role" value="<%= wt.content.ContentRoleType.PRIMARY %>" scope="request"/>
        <c:set var="tableID" value="attachments.table.primary" scope="request"/>
        <c:set var="helpLink" value="PrimaryAttachmentTableHelp" scope="request"/>
    </c:when>
    <c:otherwise>
        <c:set var="tableLabel" value="${secondaryTableTitle}" scope="request"/>
        <c:set var="role" value="<%= wt.content.ContentRoleType.SECONDARY %>" scope="request"/>
        <c:set var="tableID" value="attachments.table.secondary" scope="request"/>
        <c:set var="helpLink" value="SecondaryAttachmentsTableHelp" scope="request"/>
    </c:otherwise>
</c:choose>

<!-- Definition of the Attachments Table -->
<jca:describeTable var="tableDescriptor" id="${tableID}" type="wt.content.ContentItem" label="${tableLabel}" configurable="false" scope="request">

<c:choose>
    <c:when test='${roleType != "PRIMARY"}'>
        <jca:setComponentProperty key="actionModel" value="attachments readonly table toolbar actions"/>
        <jca:setComponentProperty key="selectable" value="true"/>
    </c:when>
</c:choose>

    <c:if test="${cSortNumber}">
        <c:if test='${roleType != "PRIMARY"}'>
            <jca:describeColumn id="lineNumber" />
        </c:if>
    </c:if>
    <jca:describeColumn id="attachmentsName" label="${att_name}" />
    <jca:describeColumn id="infoPageAction"  sortable="false" />
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

<%@ include file="/netmarkets/jsp/util/end.jspf"%>