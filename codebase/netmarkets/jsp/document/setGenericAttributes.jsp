<%--
    Document   : setECADAttributes
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

<%@ page import="com.ptc.windchill.uwgm.cadx.createecaddesign.createECADDesignCommon" %>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="wrap"%>

<%! String softTypeEcad = null; %>

<% softTypeEcad = createECADDesignCommon.getFullSoftTypeNameForEcadDoc();%>

<INPUT type="hidden" name="revisionMode" id="revisionMode" value="create"/>

<div id='<%=wt.util.HTMLEncoder.encodeForHTMLAttribute(createBean.getCurrentObjectHandle())%>driverAttributesPane' style='visibility:hidden'>

	<%@ include file="/netmarkets/jsp/components/defineItemReadOnlyPropertyPanel.jspf"%>
		<jca:configureTypePicker>
			<p:pickerParam name="seedType" value="<%=softTypeEcad%>"/>	
		</jca:configureTypePicker>
	<%@ include file="/netmarkets/jsp/components/defineItem.jspf"%>	
</div>

<mvc:attributesTableWizComponent/>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
