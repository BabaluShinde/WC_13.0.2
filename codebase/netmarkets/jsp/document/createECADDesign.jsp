<%-- 
    Document   : CreateECADDesign
    Created on : Mar 04, 2010, 12:04:55 PM
    Author     : nsmoliakova
--%>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ page import="com.ptc.core.meta.type.mgmt.server.impl.TypeDomainHelper" %>
<%@ page import="com.ptc.windchill.uwgm.cadx.createecaddesign.createECADDesignCommon" %>

<script type="text/javascript" src="templates/cadx/common/refreshCC.js"></script>
<script type="text/javascript" src='netmarkets/javascript/util/revisionLabelPicker.js'></script>
<script type="text/javascript" src="netmarkets/javascript/ECADDocument.js" ></script>

<%
String board_step = createECADDesignCommon.BOARD_STEP ;
String schematic_step = createECADDesignCommon.SCHEMATIC_STEP;
String generic_step = createECADDesignCommon.GENERIC_STEP;
String currentECADPanel = createECADDesignCommon.CURRENT_ECAD_PANEL;
String softTypeEcadDoc = createECADDesignCommon.getFullSoftTypeNameForEcadDoc(); 
String softTypeEcadBrd = createECADDesignCommon.getFullSoftTypeNameForBrd();
String softTypeEcadSch = createECADDesignCommon.getFullSoftTypeNameForSch();
String softTypeEcadDer = createECADDesignCommon.getFullSoftTypeNameForEcadDerived();
%>

<DIV id='attributesID'></DIV> 	

<script language='Javascript'>
	function setAttributesBefore(step)	{
		var el = document.getElementById ('attributesID');
		var html = "<input type='hidden' id='currentECADPanel' name='" + "<%=currentECADPanel%>" + "' value='" + step + "'/>";
		el.innerHTML = html;
		return true;	
	}
	function setBoardAttributesBefore()	{
		return setAttributesBefore ("<%=board_step%>");
	}
	function setSchematicAttributesBefore()	{
		return setAttributesBefore ("<%=schematic_step%>");
	}
	function setGenericAttributesBefore()	{
		return setAttributesBefore ("<%=generic_step%>");
	}		
 </script>

<jca:initializeItem operation="${createBean.create}" baseTypeName="<%=softTypeEcadDoc%>"/>

<jca:initializeItem operation="${createBean.create}" objectHandle="schematic" 		  baseTypeName="<%=softTypeEcadSch%>"/>
<jca:initializeItem operation="${createBean.create}" objectHandle="board"     		  baseTypeName="<%=softTypeEcadBrd%>"/>
<jca:initializeItem operation="${createBean.create}" objectHandle="generic"   		  baseTypeName="<%=softTypeEcadDoc%>"/>
<jca:initializeItem operation="${createBean.create}" objectHandle="derived_schematic" baseTypeName="<%=softTypeEcadDer%>"/>
<jca:initializeItem operation="${createBean.create}" objectHandle="derived_board"     baseTypeName="<%=softTypeEcadDer%>"/>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.ptc.windchill.uwgm.cadx.createecaddesign.documentECADResource" />

<fmt:message var="titleLabel" key="CREATE_ECAD_DESIGN_LABEL"/>
<fmt:message var="defineDesignLabel" key="DESIGN_TYPE_LABEL"/>
<fmt:message var="setSchematicAttributesLabel" key="SCHEMATIC_ATTRIBUTES_LABEL"/>
<fmt:message var="setDerivedObjectsSchLabel" key="SCHEMATIC_DERIVED_OBJECTS_LABEL"/>
<fmt:message var="setBoardAttributesLabel" key="BOARD_ATTRIBUTES_LABEL"/>
<fmt:message var="setDerivedObjectsBrdLabel" key="BOARD_DERIVED_OBJECTS_LABEL"/>
<fmt:message var="setGenericAttributesLabel" key="GENERIC_ATTRIBUTES_LABEL"/>

<jca:wizard helpSelectorKey="CADxECADHelpNewEcadDesign" buttonList="DefaultWizardButtonsNoApply"  title="${titleLabel}" formProcessorController="com.ptc.windchill.uwgm.cadx.createecaddesign.forms.controllers.CreateDesignFormProcessorController">
    <jca:wizardStep action="defineDesign"     	     type="document"  label="${defineDesignLabel}" />
    <jca:wizardStep action="setGenericAttributes"    type="document"  label="${setGenericAttributesLabel}" objectHandle="generic"/>
    <jca:wizardStep action="setSchematicAttributes"  type="document"  label="${setSchematicAttributesLabel}" objectHandle="schematic"/>
    <jca:wizardStep action="setDerivedObjects"		 type="document"  label="${setDerivedObjectsSchLabel}" objectHandle="derived_schematic"/>
    <jca:wizardStep action="setBoardAttributes"  	 type="document"  label="${setBoardAttributesLabel}" objectHandle="board"/>
    <jca:wizardStep action="setDerivedObjects"		 type="document"  label="${setDerivedObjectsBrdLabel}" objectHandle="derived_board"/>
</jca:wizard>

<script language='Javascript'>
    Ext.namespace("PTC.ECAD");

    PTC.ECAD.setStepActionAttributes = function ()
    {
        wizardSteps["setDerivedObjects!~objectHandle~derived_board~!"].afterJS = PTC.ECAD.validateDerivedObjectsStep;
        wizardSteps["setDerivedObjects!~objectHandle~derived_schematic~!"].afterJS = PTC.ECAD.validateDerivedObjectsStep;
    }

    PTC.ECAD.validateDerivedObjectsStep = function (wizardAction)
    {
        if (wizardAction == 'BACK')
        {
            return true;
        }
        else
        {
            return PTC.validation.callAJAXValidation('validateDerivedObjectsStep');
        }
    }

     PTC.onReady(function() {
         PTC.ECAD.setStepActionAttributes();
         disableOkButton();
     
    var typeEle = $("<%=createECADDesignCommon.DESIGN_CONTENT_ID%>");
    if( typeEle && typeEle.length == 1 ) {
    <%-->close the wizard since there are no types available<--%>
         JCAAlert("com.ptc.windchill.uwgm.cadx.createecaddesign.documentECADResource.JS_NO_INSTANTIABLE_TYPES");
         wfWindowClose1();
         return;
    } 
    });
</script>

 
<%@ include file="/netmarkets/jsp/util/end.jspf"%>
