<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>

<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<%@ include file="/netmarkets/jsp/components/setAttributesReadOnlyPropertyPanel.jspf"%>

<%-->Build a table descriptor and assign it to page variable td<--%>
<jca:describeAttributesTable var="attributesTableDescriptor" id="createSetAttributes" mode="CREATE"
    componentType="WIZARD_ATTRIBUTES_TABLE" type="wt.doc.WTDocument" label="${attributesTableHeader}"
	scope="request">
  <jca:describeProperty id="title"/>
  <jca:describeProperty id="folder.id"/>
  <jca:describeProperty id="lifeCycle.id"/>
  <jca:describeProperty id="teamTemplate.id"/>
  <jca:describeProperty id="ALL_CUSTOM_HARD_ATTRIBUTES_FOR_INPUT_TYPE"/>
  <jca:describeProperty id="ALL_SOFT_NON_CLASSIFICATION_SCHEMA_ATTRIBUTES"/>
</jca:describeAttributesTable>

<%@ include file="/netmarkets/jsp/components/setAttributesWizStep.jspf"%>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
