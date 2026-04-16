<%--
    Document   : setBoardAttributes
    Created on : Mar 2, 2010, 1:46:00 PM
    Author     : nsmoliakova
--%>
     
<%@taglib uri="http://www.ptc.com/windchill/taglib/changeWizards" prefix="cwiz"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/jcaMvc" prefix="mvc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/picker" prefix="p"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags" %>

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.ptc.windchill.uwgm.cadx.createecaddesign.documentECADResource" />
<fmt:message var="createDerivedObjectsLabel" key="CREATE_DERIVED_OBJECTS"/>

<%@ page import="com.ptc.windchill.uwgm.cadx.createecaddesign.createECADDesignCommon" %>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="wrap"%>

<%! String softTypeEcadBrd = null; %>

<% softTypeEcadBrd = createECADDesignCommon.getFullSoftTypeNameForBrd();%>

<INPUT type="hidden" name="revisionMode" id="revisionMode" value="create"/>


<div id='<%=wt.util.HTMLEncoder.encodeForHTMLAttribute(createBean.getCurrentObjectHandle())%>driverAttributesPane'>
	<%@ include file="/netmarkets/jsp/components/defineItemReadOnlyPropertyPanel.jspf"%>
		<jca:configureTypePicker>
			<p:pickerParam name="seedType" value="<%=softTypeEcadBrd%>"/>	
		</jca:configureTypePicker>
	<%@ include file="/netmarkets/jsp/components/defineItem.jspf"%>	
</div>

<mvc:attributesTableWizComponent/>

<wrap:checkBox name="showDerivedObjectsStepBrd" id="showDerivedObjectsStepBrd" label="${createDerivedObjectsLabel}" renderLabel="true" 
          renderLabelOnRight="true" checked="false" onclick="toggleCreateDerivedObjectsStep();" />

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
