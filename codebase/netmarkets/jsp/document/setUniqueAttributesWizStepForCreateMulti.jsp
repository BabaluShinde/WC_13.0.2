<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib prefix="attachments" tagdir="/WEB-INF/tags/attachments"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<!-- Get the localized strings from the resource bundle -->
<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.ptc.windchill.enterprise.doc.documentResource" />

<fmt:message var="att_location"       key="FILE_PATH_COLUMN" />
<fmt:message var="att_file_name"      key="FILE_NAME_COLUMN" />
<fmt:message var="att_name"           key="NAME_COLUMN" />
<fmt:message var="att_number"         key="NUMBER_COLUMN" />

<input type="hidden" id="newFiles" name="newFiles">
<input type="hidden" id="fileSep" name="fileSep" value="\">


<jca:describeTable var="multiDocWizAttributesTableDescriptor"
  targetObject="com.ptc.core.meta.type.common.TypeInstance"
  scope="request" id="multiDocWizAttributesTableDescriptor"
  componentType="WIZARD_ATTRIBUTES_TABLE" mode="CREATE"
  type="wt.doc.WTDocument" label="${attributesTableHeader}">
      <jca:setComponentProperty key="actionModel" value="multi doc create table toolbar actions"/>
      <jca:setComponentProperty key="selectable" value="true"/>
	  <jca:describeColumn  id="name" dataUtilityId="multiDoc.name" htmlId="NameInputId" label="*${att_name}" sortable="false"/>
	  <jca:describeColumn  id="number" label="*${att_number}" sortable="false"/>
	  <jca:describeColumn  id="description" sortable="false"/>
      <jca:describeColumn  id="multiObjectPrimaryAttachmentName" dataUtilityId="multiObjectPrimaryAttachmentName" label="*${att_file_name}" htmlId="FileNameInputId" sortable="false"/>
	  <jca:describeColumn  id="multiObjectPrimaryAttachment" dataUtilityId="multiObjectPrimaryAttachment" label="*${att_location}" sortable="false"/>
</jca:describeTable>

<attachments:addFileProcessor/>

<c:if test="${requestScope.multiDocWizAttributesTableDescriptor != null}">
	<jca:getModel var="commonTableModel" descriptor="${multiDocWizAttributesTableDescriptor}">
	</jca:getModel>
</c:if>

<c:if test="${requestScope.multiDocWizAttributesTableDescriptor != null}">
   <jca:renderTable model="${commonTableModel}" rowBasedObjectHandle="true" helpContext="DocMgmtAttachmentAbout"/>
</c:if>
