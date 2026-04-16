<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%
//
// Also add the following action to DocumentManagement-actions.xml:
//
//     <action name="setDerivedObjects" ajax="row" id="setDerivedObjects" preloadWizardPage="false"  required="true">
//  <command windowType="wizard_step" />
//    </action>
//
// Because no serviceName attribute is put on the getModel tag,  the EmptyModelCommand will be used to retrieve the table
// row data on first display.  As a result, the table will initially be displayed with no rows.  When a row is added via the toolbar add
// action, the ClientRowUpdateCommandDelegate will call the InflatorHelper to instantiate a InflatorHelper.WizardAttributesTableInflator
// instance which will create the TypeInstance for the new row data object and associate it to the NmSimpleOid created by the client-side
// MultiObjectWizardCommands.addDocument command.
%>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ page import="com.ptc.windchill.uwgm.cadx.createecaddesign.documentECADResource" %>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.ptc.windchill.uwgm.cadx.createecaddesign.documentECADResource" />
<fmt:message var="name"             key="<%= documentECADResource.COLUMN_NAME_WITH_ASTERISK %>" />
<fmt:message var="number"           key="<%= documentECADResource.COLUMN_NUMBER_WITH_ASTERISK%>" />
<fmt:message var="revision"         key="<%= documentECADResource.COLUMN_REVISION_WITH_ASTERISK%>" />
<fmt:message var="derived_obj_type" key="<%= documentECADResource.DERIVED_OBJECT_TYPE %>" />
<fmt:message var="table_name" 	    key="<%= documentECADResource.DERIVED_OBJECTS %>" />


<INPUT type="hidden" name="revisionMode" id="revisionMode" value="create"/>

<jca:describePropertyPanel var="attrsStepReadOnlyPanel" id="attrsStepReadOnlyPanel" scope="request" mode="CREATE" type="${createBean.itemType}">
    <jca:describeProperty id="containerName" label="${createBean.containerLabel}" mode="VIEW"/>
    <jca:describeProperty id="itemType" need="type" mode="VIEW"/> 
</jca:describePropertyPanel>


<c:set var="objectHandle" value="<%=createBean.getCurrentObjectHandle()%>"/>
<c:set var="tableId" value="createECADDesignWizAttributesTableDescriptor_board"/>
<c:set var="toolbar" value="board derived objects create table toolbar actions"/>

<c:if test="${objectHandle == '!~objectHandle~derived_schematic~!'}"> 
	<c:set var="tableId" value="createECADDesignWizAttributesTableDescriptor_schematic"/>
	<c:set var="toolbar" value="schematic derived objects create table toolbar actions"/>
</c:if>

<jca:describeTable var="createECADDesignWizAttributesTableDescriptor"
	targetObject="com.ptc.core.meta.type.common.TypeInstance"
	componentType="WIZARD_ATTRIBUTES_TABLE" 
	scope="request" 
	id="${tableId}"
	mode="CREATE" 
	type="${createBean.itemType}" 
	label="${table_name}" >

      <jca:setComponentProperty key="actionModel" value="${toolbar}" />
      <jca:setComponentProperty key="selectable" value="true"/>
     
      <jca:describeColumn id="type_icon" sortable="false"/>
      <jca:describeColumn id="name" dataUtilityId="multiDoc.name" htmlId="NameInputId" required="true" inputRequired="true" label="${name}" sortable="false">
		<jca:setComponentProperty key="useExact" value="true"/>
	  </jca:describeColumn>
      <jca:describeColumn id="number" inputRequired="true" label="${number}" sortable="false">
			<jca:setComponentProperty key="useExact" value="true"/>
	  </jca:describeColumn>
      <jca:describeColumn id="derived_objects_combobox" label="${derived_obj_type}" mode="CREATE" dataUtilityId="derivedObjectTypes" sortable="false" inputRequired="true">
	  	  	<jca:setComponentProperty key="useExact" value="true"/>
	  </jca:describeColumn>
      <jca:describeColumn  id="revision" dataUtilityId="revisionPicker" sortable="false" inputRequired="true" label="${revision}" mode="CREATE">
	  	<jca:setComponentProperty key="useExact" value="true"/>
	  </jca:describeColumn>
      <jca:describeColumn id="folder.id" sortable="false" hidden="true">
	  	<jca:setComponentProperty key="useExact" value="true"/>
	</jca:describeColumn>  
      <jca:describeColumn  id="description" sortable="false" mode="CREATE"/>

</jca:describeTable>

<jca:getModel var="tableModel" descriptor="${createECADDesignWizAttributesTableDescriptor}" >
 
</jca:getModel>

<jca:getModel var="attrsStepReadOnlyPanelModel" descriptor="${attrsStepReadOnlyPanel}" 
   serviceName="com.ptc.core.components.forms.CreateAndEditModelGetter"
   methodName="getItemAttributes">
   <jca:addServiceArgument value="${attrsStepReadOnlyPanel}" type="com.ptc.core.components.descriptor.ComponentDescriptor"/>
   <jca:addServiceArgument value="${commandBean}" type="com.ptc.netmarkets.util.beans.NmCommandBean"/>
   <jca:addServiceArgument value="${nmcontext.context}" type="com.ptc.netmarkets.util.misc.NmContext"/>
</jca:getModel>

<jca:renderPropertyPanel model="${attrsStepReadOnlyPanelModel}"/>

<jca:renderTable model="${tableModel}" rowBasedObjectHandle="true" showPagingLinks="false" scroll="true"/>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
