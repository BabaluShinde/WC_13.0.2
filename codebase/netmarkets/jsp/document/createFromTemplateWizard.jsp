<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ taglib prefix="docmgnt" uri="http://www.ptc.com/windchill/taglib/docmgnt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%-- insert revision javascript --%>
<script language="JavaScript" src='netmarkets/javascript/util/revisionLabelPicker.js'></script>

<jca:initializeItem operation="${createBean.create}" attributePopulatorClass="com.ptc.windchill.enterprise.doc.forms.DocAttributePopulator"/>

<%--  renders javascript to be used by the duplicate name validation on the attributes step --%>
<docmgnt:validateNameJSTag template="false"/>
<wctags:loadCreateClassificationScript isMultiObject="false"/>
<input type="hidden" value="true" name="createFromTemplateDTI"/>

<%--  Define attributes of the wizard and define the wizard steps --%>
<jca:wizard buttonList="DefaultWizardButtonsNoApply" helpSelectorKey="DTIDocCreateFromTemplate">

      <%-- defines the context when coming in from DTI or edacompare (or if no context is set) --%>
      <jca:wizardStep action="setContextWizStep" type="object"/>

      <%-- contains the type picker and the org picker --%>

      <%-- doc management specific attributes step. also contains primary attachment --%>
      <jca:wizardStep action="createDocumentSetTypeAndAttributesWizStep" type="document" />
      
      <jca:wizardStep action="setClassificationAttributesWizStep" type="classification"/>
      
      <%-- Set Security Label Step --%>
      <jca:wizardStep action="securityLabelStep" type="securityLabels"/>

      <%-- todo needs documentation --%>
      <jca:wizardStep action="typeRefWizStep"    type="object"/>

       <%-- adds attachments to the document --%>
      <jca:wizardStep action="attachments_step"  type="attachments" />      

    </jca:wizard>


<%@ include file="/netmarkets/jsp/util/end.jspf"%>