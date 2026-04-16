<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ taglib prefix="cwiz" uri="http://www.ptc.com/windchill/taglib/changeWizards" %>
<%@ taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf" %>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf" %> 


<jca:initializeItem operation="${createBean.create}" attributePopulatorClass="com.ptc.windchill.enterprise.change2.forms.populators.FlexibleChangeNoticeAttributePopulator" />
<%@ include file="/netmarkets/jsp/change/changeWizardConfig.jspf" %>
<%@include file="/netmarkets/jsp/attachments/initAttachments.jspf"%>
<cwiz:initializeChangeWizard changeMode="CREATE" annotationUIContext="change" changeItemClass="wt.change2.ChangeOrderIfc" changeTemplateWizard="true"/>
<cwiz:initializeSelectedItems />

<SCRIPT LANGUAGE="JavaScript">
	var storeIframes = true;
	var iframeTableId = "changeNotice.wizardImplementationPlan.table"; 
	var changeNotice = true;
	PTC.wizardIframes.initStoreIframes();

	PTC.attributePanel.on("afterRender", function() {
	    // The location of the following variable "options" matters a lot, because if it is kept outside the scope of
	    // "afterRender" listener then we don't get enough formData at the server side..
	    var options = {
	            asynchronous: true,
	            method: 'post',
	            parameters: getFormData(),
	            onSuccess: NonModeledRequiredConstraintHandler.handleResponse,
	            onFailure: NonModeledRequiredConstraintHandler.handleFailure,
	            onException: NonModeledRequiredConstraintHandler.handleFailure
	    };

	    requestHandler.doRequest('ptc1/getSoftAndNonModeledReqConstraint', options);

	    PTC.change.getChangeNoticeOrganization();
	    PTC.change.registerListenerForChangeNoticeOrganization();
	});
</SCRIPT>

<input type="hidden" id="organizationIDOfChangeNotice" name="organizationIDOfChangeNotice" value="">

<jca:wizard helpSelectorKey="templatesChangeNoticeCreate" buttonList="DefaultWizardButtonsWithSubmitPrompt" formProcessorController="com.ptc.windchill.enterprise.change2.forms.controllers.EffectivityAwareIframeFormProcessorController" wizardSelectedOnly="true">
   <jca:wizardStep action="defineItemAttributesWizStep" type="object"/>
   <jca:wizardStep action="createTemplate_wizardImplementationPlanStep" type="changeNotice" />
   <jca:wizardStep action="attachments_step" type="attachments" />   
</jca:wizard>

<attachments:fileSelectionAndUploadApplet/>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>