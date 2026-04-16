<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>

<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<wctags:loadCreateClassificationScript isMultiObject="false"/>
<wctags:initClassification enforceAutonamingRule="false" notifySimilarObject="false"/>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.ptc.windchill.enterprise.doc.documentResource" />
<fmt:message var="enabledLabel" key="<%= documentResource.ENABLED%>" />

<%@ page import="com.ptc.windchill.enterprise.doc.documentResource" %>

<%@ include file="/netmarkets/jsp/components/setAttributesReadOnlyPropertyPanel.jspf"%>

<script type='text/javascript' src="netmarkets/javascript/documentManagement.js"></script>

<%
String createType = "wt.doc.WTDocument";
request.setAttribute("createType", createType);
%>

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
   <jca:describeProperty id="lifeCycle.id"/>
   <jca:describeProperty id="teamTemplate.id"/>
   <jca:describeProperty id="ALL_CUSTOM_HARD_ATTRIBUTES_FOR_INPUT_TYPE"/>
    <jca:describeProperty id="ALL_SOFT_SCHEMA_ATTRIBUTES_FOR_INPUT_TYPE"/>
</jca:describeAttributesTable>

<%@ include file="/netmarkets/jsp/document/setAttributesWizStepWithContent.jspf"%>

<table border="0">
<tr>
   <td align="right" valign="middle" nowrap>
     &nbsp;
   </td>
   <td align="left" valign="top" nowrap>
     <w:checkBox name="enable" checked="true"/>
     <font class="wizardlabel">
     ${enabledLabel}
     </font>
   </td>
  </tr>
</table>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
