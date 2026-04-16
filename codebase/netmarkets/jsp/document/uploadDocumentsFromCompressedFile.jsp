<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ taglib prefix="docmgnt" uri="http://www.ptc.com/windchill/taglib/docmgnt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<fmt:setBundle basename="com.ptc.windchill.enterprise.doc.documentResource"/>
<fmt:message var="defineDocWizStepLabel" key="document.create.DEFINE_ITEM_WIZ_STEP_LABEL" />
<wctags:loadCreateClassificationScript isMultiObject="false"/>

 
 

<input type="hidden" id="uploadScreen" name="uploadScreen" value="true"></input>
 

<jca:initializeItem operation="${createBean.create}"
           attributePopulatorClass="com.ptc.windchill.enterprise.doc.forms.DocAttributePopulator"
           baseTypeName="${param.type}"/>

    <%--  Define attributes of the wizard and define the wizard steps --%>
    <jca:wizard title="${param.titleString}"
     buttonList="DefaultWizardButtonsNoApply" helpSelectorKey="DocMgmtDocUpload"
     >
    
      <%-- doc management specific attributes step. also contains primary attachment --%>
      <jca:wizardStep action="uploadDocumentsFromCompressedFileStep" type="document" />
            <jca:wizardStep action="setClassificationAttributesWizStep" type="classification"/>
      

    </jca:wizard>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>