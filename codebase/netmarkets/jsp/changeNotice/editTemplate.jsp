<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ taglib prefix="cwiz" uri="http://www.ptc.com/windchill/taglib/changeWizards"%>
<%@ taglib prefix="rwiz" uri="http://www.ptc.com/windchill/taglib/reservation"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/attachments" prefix="attachments"%>

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf" %>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<jca:initializeItem operation="${createBean.edit}"/>

<%@include file="/netmarkets/jsp/change/changeWizardConfig.jspf" %>
<%@include file="/netmarkets/jsp/attachments/initAttachments.jspf"%>

<cwiz:initializeChangeWizard changeMode="EDIT" annotationUIContext="change" changeItemClass="wt.change2.ChangeOrderIfc" changeTemplateWizard="true"/>

<SCRIPT LANGUAGE="JavaScript">
	var storeIframes = true;
	var iframeTableId = "changeNotice.wizardImplementationPlan.table"; 
	var changeNotice = true;
	PTC.wizardIframes.initStoreIframes();
</SCRIPT>

<jca:wizard helpSelectorKey="templatesChangeNoticeEdit" buttonList="DefaultWizardButtonsWithSubmitPrompt" formProcessorController="com.ptc.windchill.enterprise.change2.forms.controllers.EffectivityAwareIframeFormProcessorController">
	<%-->Edit Change object template<--%>
	<jca:wizardStep action="editAttributesWizStep" type="object" />
	<jca:wizardStep action="editTemplate_wizardImplementationPlanStep" type="changeNotice" />
	<jca:wizardStep action="attachments_step" type="attachments" />	
</jca:wizard>

<rwiz:handleUpdateCount/>

<attachments:fileSelectionAndUploadApplet/>

<script language='Javascript'>
   change_postLoad();

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
   });
</script>


<%@ include file="/netmarkets/jsp/util/end.jspf"%>




