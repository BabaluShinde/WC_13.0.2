<%@ include file="/netmarkets/jsp/util/begin.jspf"%>

<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ taglib prefix="docmgnt" uri="http://www.ptc.com/windchill/taglib/docmgnt" %>

<jca:describePropertyPanel var="defineItemStepContextPanelDescriptor" 
	id="defineItemStepContextPanelDescriptor"  
	scope="request" mode="VIEW" type="wt.doc.WTDocument">
    <jca:describeProperty id="containerName" label="${createBean.containerLabel}"/>
</jca:describePropertyPanel>

<jca:renderPropertyPanel>
    <%@ include file="/netmarkets/jsp/components/defineItemStepContextPanel.jspf"%>
    <docmgnt:templatePicker id="createType"/>
    <jca:addPlaceHolder id="driverAttributes" />
</jca:renderPropertyPanel>

<jca:configureTypePicker>
			<p:pickerParam name="filterType"
	               	       value="wt.doc.WTDocument|com.ptc.InterferenceDetectionDefinition"/>
</jca:configureTypePicker>


<script type="text/javascript">	
if(document.getElementById('createType') != null)
{
    resetTypeInstanceId(document.getElementById('createType').value,"","true");
}
refreshDriverAttributes("/servlet/TypeBasedIncludeServlet?contextAction=defineItemAttributesPanel");
</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>