<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib prefix="attachments" tagdir="/WEB-INF/tags/attachments"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ page import="com.ptc.core.components.descriptor.DescriptorConstants" %>
<%@page import="com.ptc.windchill.enterprise.object.util.AdvancedCreateEditMultiObjectHelper" %>
<%@page import="com.ptc.core.components.descriptor.ComponentDescriptor" %>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>

<wctags:primaryAttachmentWithMSOI />

<!-- Get the localized strings from the resource bundle -->
<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.ptc.windchill.enterprise.doc.documentResource" />

<fmt:message var="att_location"       key="FILE_LOCATION_COLUMN" />
<fmt:message var="att_file_name"      key="FILE_NAME_COLUMN" />
<fmt:message var="att_name"           key="NAME_COLUMN" />
<fmt:message var="att_number"         key="NUMBER_COLUMN" />
<!--Changed for SPR 1748393-->
<fmt:message var="att_comment"      key="COMMENT_TEXTAREA_HEADER" />
<fmt:message var="att_docHeader"   key="DOC_TEXTAREA_HEADER"/>
<fmt:message var="att_contHeader" key="CONTENT_TYPE_ICON"/>

<input type="hidden" id="newFiles" name="newFiles">
<input type="hidden" id="fileSep" name="fileSep" value="\">
<input type="hidden" name="fileAttachmentCount" id="fileAttachmentCount" value="0" />
<input type="hidden" name="scmResults" value="" id="scmResults">

<jca:describePropertyPanel var="attributesStepReadOnlyPanel" id="attributesStepReadOnlyPanel"
        scope="request" mode="EDIT" type="wt.doc.WTDocument">
       <jca:describeProperty id="containerName" label="${createBean.containerLabel}" mode="VIEW"/>        
</jca:describePropertyPanel>

<%
//preference 'Expose Organization' 
Object prefValue = wt.preference.PreferenceHelper.service.getValue( "Display_OrgID", createBean.getCommandBean().getContainer(), (wt.org.WTUser) wt.session.SessionHelper.getPrincipal() );
%>
<c:set var="displayOrg" value="<%= prefValue%>"/> 

<jca:describeTable var="editmultiDocWizAttributesTableDescriptor" scope="request" id="editmultiDocWizAttributesTableDescriptor" componentType="WIZARD_ATTRIBUTES_TABLE" mode="EDIT"  type="wt.doc.WTDocument" label="${att_docHeader}" configurable="true">
      <jca:setComponentProperty key="<%= DescriptorConstants.TableProperties.RUN_DU_IN_TWO_STAGE %>" value="false"/>
      <jca:setComponentProperty key="actionModel" value="edit multi doc actions"/>
      <jca:setComponentProperty key="selectable" value="true"/>
      <jca:setTablePlugin ptype="gridfileinputhandler"/>
      <jca:describeColumn id="statusFamily_General" sortable="false"/>
      <jca:describeColumn id="type_icon" sortable="false"/>
      <jca:describeColumn id="itemType" need="type" mode="VIEW" sortable="false"/>
      <c:if test="${displayOrg}"><%-- If preference is ON show Organization ID --%> 
            <jca:describeColumn id="orgid" need="organization.id" />
      </c:if>
      <jca:describeColumn id="name" mode="VIEW" sortable="false"/>
      <jca:describeColumn id="number" label="*${att_number}" sortable="false"  mode="VIEW"/>
      <jca:describeColumn id="locker" dataUtilityId="editMultiObjectsPrincipalDataUtility" sortable="false"  mode="VIEW"/>
      <jca:describeColumn id="creator" dataUtilityId="editMultiObjectsPrincipalDataUtility" sortable="false" mode="VIEW"/>
      <jca:describeColumn id="thePersistInfo.createStamp" sortable="false"  mode="VIEW"/>
      <jca:describeColumn id="thePersistInfo.modifyStamp" sortable="false"  mode="VIEW"/>
      <jca:describeColumn id="modifier" dataUtilityId="editMultiObjectsPrincipalDataUtility" sortable="false"  mode="VIEW"/>
      <jca:describeColumn id="owner" sortable="false"  mode="VIEW"/>
      <jca:describeColumn id="infoPageAction" sortable="false"  mode="VIEW"/>
      <jca:describeColumn id="contentFormatIcon" dataUtilityId="attachments.list.editable" sortable="false"  label="${att_contHeader}"/>
      <jca:describeColumn id="multiObjectPrimaryAttachmentName" need="multiObjectPrimaryAttachmentName" label="*${att_location}" sortable="false">
            <jca:setComponentProperty key="useExact" value="true"/>
      </jca:describeColumn>   
      <jca:describeColumn id="description" mode="EDIT" sortable="false"/>
      <jca:describeColumn id="iterationNote" label="${att_comment}" mode="EDIT" sortable="false"/>
      <jca:describeColumn id="nmActions">
            <jca:setComponentProperty key="actionModel" value="EMPTY_ACTION_MODEL"/>
      </jca:describeColumn> 
      <jca:describeColumn id="lifeCycleTemplate" dataUtilityId="lifeCycle.id" mode="VIEW" />              
</jca:describeTable>
   
<attachments:addFileProcessor/>

<%
ComponentDescriptor td = (ComponentDescriptor) request.getAttribute("editmultiDocWizAttributesTableDescriptor");
 NmCommandBean cb = (NmCommandBean) request.getAttribute("commandBean");
 AdvancedCreateEditMultiObjectHelper.configureRequiredColumnsForEdit(td, cb);
%>
<jca:getModel var="tableModel" descriptor="${editmultiDocWizAttributesTableDescriptor}"
      serviceName="com.ptc.windchill.enterprise.object.commands.AdvancedEditMultiObjectCommand" methodName="getMultiObjects">
      <jca:addServiceArgument value="${commandBean}" type="com.ptc.netmarkets.util.beans.NmCommandBean" />
     <jca:addServiceArgument value="${editmultiDocWizAttributesTableDescriptor}" type="com.ptc.core.components.descriptor.ComponentDescriptor"/>
    <jca:addServiceArgument value="${nmcontext.context}" type="com.ptc.netmarkets.util.misc.NmContext"/>
</jca:getModel>

<jca:getModel var="attributesStepReadOnlyPanelModel" descriptor="${attributesStepReadOnlyPanel}" 
   serviceName="com.ptc.core.components.forms.CreateAndEditModelGetter"
   methodName="getItemAttributes">
   <jca:addServiceArgument value="${attributesStepReadOnlyPanel}" type="com.ptc.core.components.descriptor.ComponentDescriptor"/>
   <jca:addServiceArgument value="${commandBean}" type="com.ptc.netmarkets.util.beans.NmCommandBean"/>
   <jca:addServiceArgument value="${nmcontext.context}" type="com.ptc.netmarkets.util.misc.NmContext"/>
</jca:getModel>

<jca:renderPropertyPanel model="${attributesStepReadOnlyPanelModel}"/>

<jca:renderTable model="${tableModel}" rowBasedObjectHandle="true" helpContext="DocMgmtAttachmentAbout"/>

<!-- Below script is added for auto resizing of the table column -->
<script Language="JavaScript">
Ext.ComponentMgr.onAvailable( 'editmultiDocWizAttributesTableDescriptor',function (){
    var grid =Ext.getCmp('editmultiDocWizAttributesTableDescriptor');
    if(grid){
        grid.getStore().on('datachanged', function(store) {
            grid.clearStickyConfig(); // reset values
            PTC.jca.ColumnUtils.resizeAllColumns(grid);
            return true;
        }, null, {single: true, delay:100}); // only run this function one time
    }
});
     
</script>
