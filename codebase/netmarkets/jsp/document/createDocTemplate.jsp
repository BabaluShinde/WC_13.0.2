<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/docmgnt" prefix="docmgnt" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/attachments" prefix="attachments" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/dti" prefix="dti"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>

<%@ page import="com.ptc.windchill.enterprise.util.AttachmentsWebHelper" %>

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ include file="/netmarkets/jsp/attachments/initAttachments.jspf"%>
<%@ include file="/netmarkets/jsp/attachments/initPTCAttachments.jspf"%>

<fmt:setBundle basename="com.ptc.windchill.enterprise.doc.documentResource"/>
<fmt:message var="defineDocWizStepLabel" key="document.createDocTemplate.DEFINE_ITEM_WIZ_STEP_LABEL" />

<docmgnt:validateNameJSTag template="true"/>
<wctags:loadCreateClassificationScript isMultiObject="false"/>

<%
 String userAgent = commandBean.getTextParameter("ua"); 
 boolean isOffice365 = false;
 if(userAgent != null && userAgent.equals("OFFICE365"))
     isOffice365=true;
 %>

<c:choose>
   <c:when test='${param.externalFormData != null}'>
      <jca:initializeItem operation="${createBean.create}" attributePopulatorClass="com.ptc.windchill.enterprise.nativeapp.msoi.forms.ExternalFormDataPopulator" />
   </c:when>
   <c:when test='${isoffice365 != false}'>
      <jca:initializeItem operation="${createBean.create}" attributePopulatorClass="com.ptc.windchill.cloudapp.officeapp.forms.Office365FormDataPopulator" />
   </c:when>
   <c:otherwise>
      <jca:initializeItem operation="${createBean.create}"  attributePopulatorClass="com.ptc.windchill.enterprise.doc.forms.DocTemplateAttributePopulator" />
   </c:otherwise>
</c:choose>

<%  /*When Security Label validation is enabled i.e. disableSecureUpload is "false",
     securityLabelStep appears before the createDocTemplateSetAttributesStep*/
    boolean disableSecureUpload  = AttachmentsWebHelper.isSecureUploadDisabled();
    if (!disableSecureUpload) {
%>
<jca:wizard buttonList="DefaultWizardButtonsNoApply" helpSelectorKey="TemplatesDocCreate">
    <jca:wizardStep action="setContextWizStep" type="object"/>
    <jca:wizardStep action="defineItemWizStep" label="${defineDocWizStepLabel}" type="object"/>
	
	    <%-- Set Security Label Step --%>
    <jca:wizardStep action="securityLabelStep" type="securityLabels"/>
    
    <jca:wizardStep action="createDocTemplateSetAttributesStep" type="document"/>
     <jca:wizardStep action="setClassificationAttributesWizStep" type="classification"/>
    
    <jca:wizardStep action="attachments_step" type="attachments" />
</jca:wizard>
<%
    /*When Security Label validation is disabled i.e. disableSecureUpload is "true",
    createDocTemplateSetAttributesStep comes before the securityLabelStep*/
    }
    else {
%>
<jca:wizard buttonList="DefaultWizardButtonsNoApply" helpSelectorKey="TemplatesDocCreate">
    <jca:wizardStep action="setContextWizStep" type="object"/>
    <jca:wizardStep action="defineItemWizStep" label="${defineDocWizStepLabel}" type="object"/>

    <jca:wizardStep action="createDocTemplateSetAttributesStep" type="document"/>
        <%-- Set Security Label Step --%>
    <jca:wizardStep action="securityLabelStep" type="securityLabels"/>

     <jca:wizardStep action="setClassificationAttributesWizStep" type="classification"/>

    <jca:wizardStep action="attachments_step" type="attachments" />
</jca:wizard>
<%
    }
%>
<%--- If we are not DTI then add the applet for doing file browsing and file uploads --%>
<wctags:fileSelectionAndUploadAppletUnlessMSOI forceApplet='${param.addAttachments != null }'/>


<%@include file="/netmarkets/jsp/util/end.jspf"%>
