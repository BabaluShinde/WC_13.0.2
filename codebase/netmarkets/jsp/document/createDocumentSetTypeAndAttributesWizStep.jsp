<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/jcaMvc" prefix="mvc"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/core" prefix="core"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/picker" prefix="p"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/partRelDoc" prefix="partRelDoc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/configlinkui" prefix="clui"%>
<%@ taglib prefix="w" uri="http://www.ptc.com/windchill/taglib/wrappers"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/informationElement" prefix="inf"%>
<%@ page import="java.util.List ,com.ptc.windchill.enterprise.doc.validators.DocValidatorHelper"%>
<%@ page import="com.ptc.windchill.enterprise.doc.DocumentConstants" %>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ taglib prefix="docmgnt" uri="http://www.ptc.com/windchill/taglib/docmgnt" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/dti" prefix="dti"%>
<%@ page import="com.ptc.windchill.enterprise.doc.documentResource" %>
<%@ page import="com.ptc.windchill.enterprise.workSet.util.WorkSetManagementHelper" %>
<%@ page import="com.ptc.windchill.enterprise.util.PartManagementHelper"%>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.ptc.windchill.enterprise.doc.documentResource" />
<fmt:message var="checkoutDownload" key="<%= documentResource.CHECKOUT_DOWNLOAD%>" scope="request" />
<fmt:message var="templateSelectionMessage" key="<%= documentResource.TEMPLATE_SELECTION_MESSAGE%>" scope="request" />
<fmt:message var="keepDocOpenLabel" key="KEEP_DOC_OPEN_LABEL" scope="request" />

<%-- attributes used by attachments code TODO remove this --%>
<%
String createType = "wt.doc.WTDocument";
request.setAttribute("createType", createType);

List<String> validRefTypeRoleBObjectTypeList=null;
List<String> validDesTypeRoleBObjectTypeList=null;
boolean isPDMMethod = PartManagementHelper.getWcPDMMethodPref();

if(commandBean.getElementContext()!=null && commandBean.getElementContext().toString().contains("relatedParts")){

	validRefTypeRoleBObjectTypeList  = DocValidatorHelper.getValidRoleBObjectType(commandBean,"wt.part.WTPartReferenceLink");
	validDesTypeRoleBObjectTypeList  = DocValidatorHelper.getValidRoleBObjectType(commandBean,"wt.part.WTPartDescribeLink");
}
request.setAttribute("validRefTypeRoleBObjectTypeList",validRefTypeRoleBObjectTypeList);
request.setAttribute("validDesTypeRoleBObjectTypeList",validDesTypeRoleBObjectTypeList); 
%>

<script language='Javascript'>
	var cbTemplates = document.getElementById('templatesCombo');
	var attributePopulatorClass = document.getElementsByName('attributePopulatorClass')[0];
	var externalFormData = document.getElementById('externalFormData');
	var userAgnt = document.getElementById('isDTI');

	if (cbTemplates != null || cbTemplates != "null" || cbTemplates.value == "") {
		if (externalFormData != null || (userAgnt != null && userAgnt.value == 'true')) {
			if (attributePopulatorClass != null) {
				attributePopulatorClass.value = "com.ptc.windchill.enterprise.nativeapp.msoi.forms.ExternalFormDataPopulator";
			}
		} else {
			if (attributePopulatorClass != null) {
				attributePopulatorClass.value = "com.ptc.windchill.enterprise.doc.forms.DocAttributePopulator";
			}
		}
	}
</script>
<%-- Setting parameters used by insert revision TODO remove this --%>
<%
   String insertNumber = (String) request.getParameter(DocumentConstants.RequestParam.Names.INSERT_REVISION_NUMBER);
   boolean insertAction = false;
   if(insertNumber != null && insertNumber.length()>0)
   {
      insertAction = true;
   }

   request.setAttribute("insertingPart", insertAction);

   String invokedfrom = (String) request.getParameter("invokedfrom");
   String actionName = (String) request.getParameter("actionName");

   boolean invokedFromDocSB = false;

   if("docsb".equals(invokedfrom) || "insertNewDocStructureGWT".equals(actionName))
   {
       invokedFromDocSB = true;
   }

%>

<input type="hidden" id="insertingPart" name="insertingPart" value="<%=insertAction%>"></input>
<input type="hidden" name="revisionMode" id="revisionMode" value="create"/>

<c:if test='<%=request.getParameter("invokedFromContext") != null%>'>
	<core:htmlEncoder encodingMethod="encodeForJavascript" text='<%=request.getParameter("invokedFromContext")%>' var="htmlInvokedFromContext" scope="request"/>
</c:if>

<%-- contains document management specific javascript methods, specifically the name defaulting javascript --%>
<script language="JavaScript" type="text/javascript">
   PTC.navigation.loadScript('netmarkets/javascript/scmContentManagement.js');   
   PTC.navigation.loadScript('netmarkets/javascript/documentManagement.js');
   PTC.navigation.loadScript('netmarkets/javascript/attachments/attachments.js');
</script>

<%-- When coming from EDA Compare this set the file for use in the attachment component
     But also it tells the attachments component to upload the file.
 --%>

<c:if test='${param.forcedFilePath != null }'>
 <c:set var="fixedFilePath" value="${param.forcedFilePath}" scope="request" />
 <c:set var="fixedFileUpload" value="true" scope="request" />
</c:if>


<div id='driverAttributesPane'>
<%@ include file="/netmarkets/jsp/components/defineItemReadOnlyPropertyPanel.jspf"%>

<jsp:include page="/netmarkets/jsp/document/createDocumentSetTypeAndAttributesWizStepInclude.jsp" />

<%@ include file="/netmarkets/jsp/components/defineItem.jspf"%>
</div>

<jsp:include page="/netmarkets/jsp/document/createDocumentSetTypeAndAttributesWizStepInclude2.jsp" />

<c:if test='${htmlInvokedFromContext == "pubSt"}'>
	<div id='insertLocationPickerDiv' style="visibility:hidden">	
	    <jca:renderPropertyPanel>
	        <inf:getNodeName id="nodeNameTextBox"/>
	        <inf:insertLocationPicker id="insertLocationPicker"/> 
	    </jca:renderPropertyPanel>	
	</div>
</c:if>

<script language='Javascript'>
// From the common component we are calling our local pickerGo which calls the original pickerGo and then calls populateTemplates
pickerGo = pickerGo.wrap(function(original,value, currentObjectHandle, template) {
   original(value, currentObjectHandle, template);
   if (!template)
   populateTemplates(value);
})

var createType = document.getElementById('createType');

// When the type is selected by default , we need to call populateTemplates
if (createType!= null && createType!= 'undefined' && createType.value!= 'undefined' && createType.value!= "")
populateTemplates(createType.value);

//This is to hide the primary content, KeepCheckout checkbox initially
var localType = '';
if (createType!= null && createType!= 'undefined' && createType.value!= 'undefined' && createType.value!= "")
{
    localType = createType.value;
}
updatePrimaryContent(localType);

function checkedOutClicked(){
    if(window.document.getElementsByName("keepCheckedOutDTI")[0].checked){
        if(window.document.getElementsByName("keepDocOpen")[0] != null) {
            window.document.getElementsByName("keepDocOpen")[0].checked = true;
            window.document.getElementsByName("keepDocOpen")[0].disabled = true;
        }
    } else {
        if(window.document.getElementsByName("keepDocOpen")[0] != null) {
            window.document.getElementsByName("keepDocOpen")[0].checked = false;
            window.document.getElementsByName("keepDocOpen")[0].disabled = false;
        }
    }
}

//Trigger a download when creating a document from template in case checkout and download checkbox is checked
PTC.action.on('objectsaffected', function(formResult) {
   var url = formResult.extraData.downloadUrl;
   if (url) {
       var opener = getOpener().open(url);
   }
});

 function getOpener(){
            if (opener) {
                return opener;
            }
            else if (top.opener) {
                return top.opener;
            }
            return window;
        }
</script>


<%@ include file="/netmarkets/jsp/util/end.jspf"%>
