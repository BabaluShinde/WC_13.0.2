<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib prefix="attachments" tagdir="/WEB-INF/tags/attachments"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/jcaMvc" prefix="mvc"%>
<%@ taglib prefix="w" uri="http://www.ptc.com/windchill/taglib/wrappers"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/core" prefix="wc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/dti" prefix="dti"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/partRelDoc" prefix="partRelDoc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/picker" prefix="p"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ include file="/netmarkets/jsp/components/defineItemReadOnlyPropertyPanel.jspf"%>
<%@page import="com.ptc.windchill.enterprise.object.util.AdvancedCreateEditMultiObjectHelper" %>
<%@page import="com.ptc.core.components.descriptor.ComponentDescriptor" %>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>

<div id='multiDocWizAttributesTableDescriptorDiv' style="visibility:hidden">
        <jca:describeTable var="multiDocWizAttributesTableDescriptor"
                targetObject="typeInstance"
                scope="request" id="multiDocWizAttributesTableDescriptor"
                componentType="WIZARD_ATTRIBUTES_TABLE" mode="CREATE"
                type="${createBean.itemType}" label="${attributesTableHeader}" configurable="${(!isDnD || enableRequiredViewDnD)}">
          <jca:setComponentProperty key="actionModel" value="${tableActionModelName}"/>
          <jca:setComponentProperty key="selectable" value="true"/>
          <!-- 
            Uncomment the following to enable the plugin that gives the table ability 
            to handle browser input fields in the table. See SPR 2035410.  This can
            cause performance issues and thus is not enabled by default.
          -->
          <%-- the gridfileinputhandler plugin will disable many grid features, so that using a browser input field in the ext grid will work--%>
          <jca:setTablePlugin ptype="gridfileinputhandler"/>
          <jca:describeColumn  id="type_icon" sortable="false"/>
          <jca:describeColumn  id="name" dataUtilityId="multiDoc.name" htmlId="NameInputId" label="*${att_name}" sortable="false">
            <jca:setComponentProperty key="useExact" value="true"/>
          </jca:describeColumn>
          <jca:describeColumn  id="number" label="*${att_number}" sortable="false"/>
          <jca:describeColumn  id="contentTypeIcon" dataUtilityId="attachments.list.editable" sortable="false"/>
          <jca:describeColumn  id="multiObjectPrimaryAttachment" dataUtilityId="multiObjectPrimaryAttachment" label="*${att_file_location}" sortable="false">
            <jca:setComponentProperty key="useExact" value="true"/>
          </jca:describeColumn>
          <c:choose>
              <c:when test='${isDTI}'>
                  <jca:describeColumn id="folder.id" dataUtilityId="multiObjectPrimaryAttachment" sortable="false" mode="CREATE" label="${att_folderLocation_label}">
                     <jca:setComponentProperty key="useExact" value="true"/>
                  </jca:describeColumn>
              </c:when>
              <c:otherwise>
                  <jca:describeColumn id="folder.id" sortable="false" mode="CREATE" label="${att_folderLocation_label}">
                     <jca:setComponentProperty key="useExact" value="true"/>
                  </jca:describeColumn>
              </c:otherwise>
           </c:choose>
          <jca:describeColumn id="lifeCycle.id" sortable="false" label="${lifecycle_template}"/>
          <jca:describeColumn  id="description" sortable="false" mode="CREATE"/>
    </jca:describeTable>   
<attachments:addFileProcessor/>

  <%

    ComponentDescriptor td = (ComponentDescriptor) request.getAttribute("multiDocWizAttributesTableDescriptor");
        if(td != null) {
    NmCommandBean cb = (NmCommandBean) request.getAttribute("commandBean");
    AdvancedCreateEditMultiObjectHelper.configureRequiredColumnsForCreate(td, cb);
        }
%>
<c:choose>
    <c:when test='${isDTI}'>
        <c:if test="${requestScope.multiDocWizAttributesTableDescriptor != null}">
        <jca:getModel var="commonTableModel" descriptor="${multiDocWizAttributesTableDescriptor}"
             serviceName="com.ptc.windchill.enterprise.doc.commands.CreateMultiDocCommand" 
             methodName="addRows">
             <jca:addServiceArgument value="${multiDocWizAttributesTableDescriptor}" type="com.ptc.core.components.descriptor.ComponentDescriptor"/>
             <jca:addServiceArgument value="${commandBean}" type="com.ptc.netmarkets.util.beans.NmCommandBean"/>
             <jca:addServiceArgument value="${nmcontext.context}" type="com.ptc.netmarkets.util.misc.NmContext"/>
             <jca:addServiceArgument value="${multiDnDFiles}" type="java.lang.String"/>
        </jca:getModel>
        </c:if>
    </c:when>
    <c:otherwise>
    
        <c:if test="${requestScope.multiDocWizAttributesTableDescriptor != null}">
              
                <jca:getModel var="commonTableModel" descriptor="${multiDocWizAttributesTableDescriptor}" serviceName="com.ptc.windchill.enterprise.doc.commands.CreateMultiDocCommand"
             methodName="configureDataUtilities" >
                <jca:addServiceArgument value="${multiDocWizAttributesTableDescriptor}" type="com.ptc.core.components.descriptor.ComponentDescriptor" />
                <jca:addServiceArgument value="${commandBean}" type="com.ptc.netmarkets.util.beans.NmCommandBean" /> 
            </jca:getModel>
        </c:if> 
    </c:otherwise> 
</c:choose>

<c:if test="${requestScope.multiDocWizAttributesTableDescriptor != null}">
   <jca:renderTable model="${commonTableModel}" rowBasedObjectHandle="true" helpContext="DocMgmtAttachmentAbout" scroll="true"/>
</c:if>
</div>

<fmt:setBundle basename="com.ptc.core.htmlcomp.htmlcompResource" />

<fmt:message var="required_view"       key="REQUIRED" />
<wc:htmlEncoder encodingMethod="encodeForHTMLAttribute" text="${required_view}" var="encodedRequiredView" scope="page"/>
<input type="hidden" id="required_view_label" name="required_view_label" value="${encodedRequiredView}">


<%@ include file="/netmarkets/jsp/util/end.jspf"%>