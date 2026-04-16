<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ page import="com.ptc.windchill.enterprise.doc.DocumentConstants" %>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<%-- attributes used by attachments code TODO remove this --%>
<%
String createType = "wt.doc.WTDocument";
request.setAttribute("createType", createType);
%>

<%-- Setting parameters used by insert revision TODO remove this --%>
<%
   String insertNumber = (String) request.getParameter(DocumentConstants.RequestParam.Names.INSERT_REVISION_NUMBER);
   boolean insertAction = false;
   if(insertNumber != null && insertNumber.length()>0)
      insertAction = true;
   request.setAttribute("insertingPart", insertAction);
%>

<%-- contains document management specific javascript methods, speicifically the name defaulting javascript --%>
<script type='text/javascript' src="netmarkets/javascript/documentManagement.js"></script>


<%-- When coming from EDA Compare this set the file for use in the attachment component
     But also it tells the attachments component to upload the file.
 --%>
<c:if test='${param.forcedFilePath != null }'>
 <c:set var="fixedFilePath" value="${param.forcedFilePath}" scope="request" />
 <c:set var="fixedFileUpload" value="true" scope="request" />
</c:if>

<%-- displays type and id attributes from the previous step --%>
<%@ include file="/netmarkets/jsp/components/setAttributesReadOnlyPropertyPanel.jspf"%>

<%-->Build a table descriptor and assign it to page variable td

This defines all of the attributes in the attributes step
<--%>
<jca:describeAttributesTable var="attributesTableDescriptor" id="createSetAttributes" mode="CREATE"
    componentType="WIZARD_ATTRIBUTES_TABLE" type="wt.doc.WTDocument" label="${attributesTableHeader}"
   scope="request">
  <jca:describeProperty id="number"/>

  <%-- Adds a htmlid which makes an id on the name textbox for use by the name defaulting javascript --%>
  <jca:describeProperty id="name" htmlId="NameInputId"/>
  <jca:describeProperty id="title"/>
  <jca:describeProperty id="description"/>
  <jca:describeProperty id="folder.id"/>
  <jca:describeProperty id="lifeCycle.id"/>
  <jca:describeProperty id="teamTemplate.id"/>
  <jca:describeProperty id="ALL_CUSTOM_HARD_ATTRIBUTES_FOR_INPUT_TYPE"/>
  <jca:describeProperty id="ALL_SOFT_NON_CLASSIFICATION_SCHEMA_ATTRIBUTES"/>
  <%--
   Display a data utility for revision label picker if insert revision action is executed
   --%>
   <c:if test="${insertingPart}">
      <jca:describeProperty id="revision" dataUtilityId="revisionPicker">
      <jca:setComponentProperty key="revisionMode" value="create"/>
      </jca:describeProperty>
  </c:if>
</jca:describeAttributesTable>

<%-- renders data defined above --%>
<%@ include file="/netmarkets/jsp/document/setAttributesWizStepWithContent.jspf"%>
<% 
	String userAgent = commandBean.getTextParameter("ua");
	boolean isDTI = false;
	if(userAgent != null && userAgent.equals("DTI"))
    	isDTI=true;
%>
<%-- if checked it will cause the form processor to check out the document after it was created --%>

<% if(!isDTI) { %>
	<%@ include file="/netmarkets/jsp/components/keepCheckedOutCheckbox.jspf"%>
<% } else { %>
	<wrap:checkBox name="keepCheckedOutDTI" id="keepCheckedOutDTI" label="${keepCheckedOutLabel}" renderLabel="true" renderLabelOnRight="true"/>
<% } %>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>