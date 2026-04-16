<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/attachments" prefix="attachments" %>
<%@ taglib prefix="docmgnt" uri="http://www.ptc.com/windchill/taglib/docmgnt" %>

<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.ptc.windchill.enterprise.doc.documentResource" />
<fmt:message var="checkoutDownload" key="<%= documentResource.CHECKOUT_DOWNLOAD%>" />

<%@ page import="com.ptc.windchill.enterprise.doc.documentResource" %>

<%
String createType = "wt.doc.WTDocument";
request.setAttribute("createType", createType);
%>

<%@ include file="/netmarkets/jsp/components/setAttributesReadOnlyPropertyPanel.jspf"%>

<attachments:primaryAttachment />

<jca:describeAttributesTable var="attributesTableDescriptor" scope="request" 
                                               id="create.setAttributes" 
                                               componentType="WIZARD_ATTRIBUTES_TABLE" 
                                               mode="CREATE" 
                                               type="wt.doc.WTDocument" 
                                               label="${attributesTableHeader}">
  	<jca:describeProperty id="name" htmlId="NameInputId"/>
  	<jca:describeProperty id="number"/>
  	<jca:describeProperty id="title"/>
  	<jca:describeProperty id="description"/> 
  	<jca:describeProperty id="folder.id"/>
  	<jca:describeProperty id="lifeCycle.id"/>
	<jca:describeProperty id="teamTemplate.id"/>
  	<jca:describeProperty id="ALL_CUSTOM_HARD_ATTRIBUTES_FOR_INPUT_TYPE"/>
    <jca:describeProperty id="ALL_SOFT_SCHEMA_ATTRIBUTES_FOR_INPUT_TYPE"/>
</jca:describeAttributesTable>

<%@ include file="/netmarkets/jsp/components/setAttributesWizStep.jspf"%>


<docmgnt:prefCheckBox name="checkoutDownload" id="checkoutDownload" label="${checkoutDownload}" checkBoxPref="/com/ptc/windchill/doc/defaultCheckoutOnCreateFromTemplate" renderLabel="true" renderLabelOnRight="true"/>


<script type="text/javascript">
	// This method will load the attachments step if marked as
    // preload="false", and not required, if it has not been loaded yet.
    // The "isDirty" flag will be set to false.
    // The incomplete steps will be recalculated after this function is called.
	function loadAttachmentsStep()
	{
	   for( i = 0; i < steps.length; i++ )
	   {
	      var s = wizardSteps[steps[i]];
	      // If the page is not preloaded AND it is dirty and not required
	      // load it now.
	
	      if (steps[i] == 'attachments.attachments_step'){
	      if ( s.preloadPage == false && s.isDirty == true && !s.required )
	        {
	           reloadPane(steps[i], steps[i]);
	           s.isDirty = ( s.isDirty ) ? false: s.isDirty;
	        }
	      }
	   }
	   recalcIncomplete();
	   return true;
	}

    loadAttachmentsStep(); 
</script>

 
<%@ include file="/netmarkets/jsp/util/end.jspf"%>

