<%@ page import="com.ptc.windchill.enterprise.attachments.attachmentsResource" %>
<%@ page import="wt.workflow.work.WorkItem" %>
<%@ page import="com.ptc.jca.json.table.TableConfigHolder" %>
<%@ page import="com.ptc.mvc.components.FindInTableMode" %>
<%@ page import="wt.org.WTUser" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/core" prefix="core"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<fmt:setBundle basename="com.ptc.windchill.enterprise.attachments.attachmentsResource" />

<c:choose>
    <c:when test='${role == "WP_EXP_SECONDARY"}'>
         <c:set var="tableId" value="${param.tableId}" scope="request"/>
         <c:set var="actionName" value="${param.actionName}"/>
         <c:set var="tableLabel" value="${param.tableLabel}"/>
                 <c:set var="roleType" value="<%= wt.content.ContentRoleType.WP_EXP_SECONDARY %>"/>
                 <fmt:message var="att_name"           key="<%= attachmentsResource.WP_EXP_ATTACHMENT_NAME %>" />
         <c:set var="helpFileName" value="ObjectOviewInfoTablesGeneral"/>                 
    </c:when>
    <c:otherwise>
         <c:set var="tableId" value="attachments.list.editable" scope="request"/>
         <c:set var="actionName" value="attachments wizard table toolbar actions"/>
         <c:set var="tableLabel" value="${att_label}"/>
                 <c:set var="roleType" value="<%= wt.content.ContentRoleType.SECONDARY %>"/>
         <c:set var="helpFileName" value="DocMgmtAttachmentAbout"/>
                 
    </c:otherwise>

</c:choose>

<!-- * means required -->
<jca:describeTable var="tableDescriptor" id="${tableId}" type="wt.content.ContentItem" label="${tableLabel}" mode="${createBean.operation}" scope="request">
   <jca:setComponentProperty key="actionModel" value="${actionName}"/>
   <jca:setComponentProperty key="variableRowHeight" value="true"/>
   <jca:setComponentProperty key="<%=TableConfigHolder.FIND_IN_TABLE_MODE%>" value="<%=FindInTableMode.DISABLED%>"/>   
   
   <%-- the gridfileinputhandler plugin will disable many grid features, so that using a browser input field in the ext grid will work--%>
   <jca:setTablePlugin ptype="gridfileinputhandler"/>

   <jca:describeColumn id="type_icon"                 sortable="false" />
   <c:if test="${cSortNumber}">
    <jca:describeColumn id="contentLineNumber"    sortable="false" />
   </c:if>
  
   <jca:describeColumn id="contentName"               sortable="false"  label="*${att_name}">
      <jca:setComponentProperty key="useExact" value="true"/>
   </jca:describeColumn>
     
   <jca:describeColumn id="contentLocation"           sortable="false"  label="*${att_location}">
      <jca:setComponentProperty key="useExact" value="true"/>
   </jca:describeColumn>

   <jca:describeColumn id="contentDescription"        sortable="false"  label="${att_description}" />

    <c:if test="${cComments}">
        <jca:describeColumn id="contentComments"      sortable="false"  label="${att_comments}" />
    </c:if>
    <c:if test="${cDistributable}">
        <jca:describeColumn id="contentDistributable" sortable="false"  label="${att_distributable}" />
    </c:if>
    <c:if test="${cAuthoredBy}">
        <jca:describeColumn id="contentAuthoredBy"    sortable="false"  label="${att_authoredBy}" />
    </c:if>
    <c:if test="${cLastAuthored}">
    <jca:describeColumn id="contentLastAuthored"  sortable="false"  label="${att_lastAuthored}" />
    </c:if>
    <c:if test="${cFileVersion}">
        <jca:describeColumn id="contentFileVersion"   sortable="false"  label="${att_fileVersion}" />
    </c:if>
   
         <jca:describeColumn id="contentToolName"     sortable="false"  label="${att_toolName}" />
		 
    <c:if test="${cToolVersion}">
         <jca:describeColumn id="contentToolVersion"  sortable="false"  label="${att_toolVersion}" />
    </c:if>
	
	<jca:describeColumn id="strikethrough"  sortable="false"  label="Owner" />
	
</jca:describeTable>

<c:set target="${tableDescriptor.properties}" property="selectable" value="true"/>

<jca:getModel var="tableModel" descriptor="${tableDescriptor}"
               serviceName="com.ptc.windchill.enterprise.attachments.commands.AttachmentQueryCommands"
               methodName="getAttachments">
    <jca:addServiceArgument value="${commandBean}" type="com.ptc.netmarkets.util.beans.NmCommandBean" />
    <jca:addServiceArgument value="${roleType}"/>
</jca:getModel>

<jca:renderTableTree var="attachments_tree_model"
                     model="${tableModel}">
    <jca:getNonSelectables 
        className="ext.splm.attachments.handler.StrikeThroughRowHandler"
        methodName="getNonSelectableRows"/>

</jca:renderTableTree>

 
<!-- Finally render -->
<jca:renderTableTree tree="${attachments_tree_model}"
                     model="${tableModel}"/>
					 




<%@ include file="/netmarkets/jsp/util/end.jspf"%>

