<%@ page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@ page import="com.ptc.core.htmlcomp.util.TypeHelper"%>
<%@ page import="com.ptc.core.meta.type.mgmt.server.impl.TypeDomainHelper"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/picker" prefix="p"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/partRelDoc" prefix="partRelDoc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/configlinkui" prefix="clui"%>

<%@ include file="/netmarkets/jsp/util/begin.jspf"%>

<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<%@ include file="/netmarkets/jsp/components/defineItemReadOnlyPropertyPanel.jspf"%>

<c:choose>
  <c:when test='${param.invokedfrom == "config_link_table"}' >
     	<%-->	The action to create a document is from a configurable link table,
     	so the types need to be selected based on the association constraints
     	on the configurable link. <--%>
	  <clui:getRoleBTypesForNewAction var="roleBObjectTypes" roleBBaseType="wt.doc.WTDocument"/>

	   <jca:configureTypePicker>
	       <c:forEach var="item" items="${roleBObjectTypes}">
	         <p:pickerParam name="seedType" value="${item}"/>
	       </c:forEach>
    </jca:configureTypePicker>
   </c:when>

   <c:when test="${param.noRefDoc == null || partRelDoc:getWcPDMMethodPref()}">
   	<%-->	The action to create a document is not from a Part Details or Part Instance
   		details page, 3rd level nav References Document table or the wcPDMMethod
   		is true (any type of document can be created from the References Document
   		table) don't filter out Reference Documents and its sub types. <--%>
	   <jca:configureTypePicker>
		<p:pickerParam name="filterType"  value="wt.doc.WTDocument|com.ptc.InterferenceDetectionDefinition"/>
           </jca:configureTypePicker>
    </c:when>
   <c:otherwise>
   	<%-->  	Filter out Reference Documents and their sub types from the Part Details
   		page 3rd level nav References Documents table document create action<--%>
	<jca:configureTypePicker>
		<p:pickerParam name="filterType"
	               	       value="wt.doc.WTDocument|com.ptc.ReferenceDocument"/>
	        
      	       
	</jca:configureTypePicker>
    </c:otherwise>
</c:choose>

<%@ include file="/netmarkets/jsp/components/defineItem.jspf"%>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>
