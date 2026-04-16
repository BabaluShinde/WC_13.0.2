<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/attachments" prefix="attachments" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/dti" prefix="dti"%>

<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

 <%
 String createType = "wt.doc.WTDocument";
 request.setAttribute("createType", createType);
 %>

 <attachments:primaryAttachment/>

<%-->Build a table descriptor and assign it to page variable td<--%>
<jca:describeAttributesTable var="attributesTableDescriptor" id="editSetAttributes" mode="CREATE"
    componentType="WIZARD_ATTRIBUTES_TABLE" type="wt.doc.WTDocument" label="${attributesTableHeader}"
    scope="request">
   <jca:describeProperty id="name" mode="VIEW"/>
   <jca:describeProperty id="number" mode="VIEW"/>
   <jca:describeProperty id="title"/>
   <jca:describeProperty id="description"/>
   <jca:describeProperty id="folder.id"/>
   <jca:describeProperty id="ALL_CUSTOM_HARD_ATTRIBUTES_FOR_INPUT_TYPE"/>
   <jca:describeProperty id="ALL_SOFT_NON_CLASSIFICATION_SCHEMA_ATTRIBUTES"/>
   <jca:describeProperty id="revision" inputRequired="true" dataUtilityId="revisionPicker">  
   	<jca:setComponentProperty key="revisionMode" value="insert"/>  
   </jca:describeProperty>
</jca:describeAttributesTable>

<%@ include file="/netmarkets/jsp/components/setAttributesWizStep.jspf"%>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>