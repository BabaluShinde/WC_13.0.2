<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ taglib prefix="docmgnt" uri="http://www.ptc.com/windchill/taglib/docmgnt" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/attachments" prefix="attachments" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/util" prefix="util" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/picker" prefix="p"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/partRelDoc" prefix="partRelDoc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/configlinkui" prefix="clui"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>

<%@ page import="com.ptc.windchill.enterprise.util.AttachmentsWebHelper" %>

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<fmt:setBundle basename="com.ptc.windchill.enterprise.doc.documentResource"/>
<fmt:message var="defineDocWizStepLabel" key="document.createMulti.DEFINE_ITEM_WIZ_STEP_LABEL" />

    <c:choose>
        <%--  used for DTI to prepopulate attributes in the attributes wizard step --%>
      <c:when test='${param.externalFormData != null}'>
         <jca:initializeItem operation="${createBean.create}" attributePopulatorClass="com.ptc.windchill.enterprise.nativeapp.msoi.forms.ExternalFormDataPopulator" />
      </c:when>
      <c:otherwise>
         <%-- populate number attrubute for insert revision action only --%>
         <jca:initializeItem operation="${createBean.create}"
           attributePopulatorClass="com.ptc.windchill.enterprise.doc.forms.DocAttributePopulator"
           baseTypeName="${param.type}"/>
      </c:otherwise>
   </c:choose>

   <docmgnt:validateNameJSTag/>
   <script language="JavaScript" type="text/javascript" src="netmarkets/javascript/multiobject/multiObjectUtils.js"></script>
   <script type="text/javascript">
      function preUniqueAttsStep() {
         if (dragAndDropFileSelectionAppletSource) {
            CreateControl('dragAndDropFileSelectionAppletDiv', dragAndDropFileSelectionAppletSource);
         }
      }

       function multiDocWizardAttributes(wizardAction) {
         var fileElements = getFileElements(getMainForm(), true, "filePath");
         var locationElements = getFileElements(getMainForm(), true, "Location");
         var multiDocFilesAdded = ( fileElements != null && fileElements.length > 0 && fileElements[0] != null);

               if( wizardAction == "BACK" ) {
                  wizardSteps[currentStepStrName].isComplete = false;
                        return true;
               }

            var isdti = document.getElementById('isDTI');
            if (!multiDocFilesAdded && locationElements.length<=0 && !isdti) {
                window.alert("${util:escapeJavascriptStringLiteral(jca:getLocalizedMessage("com.ptc.windchill.enterprise.doc.documentResource", "ERROR_MULTI_DOC_NO_FILES", null)) }");
                return false;
            }
            else{
                var isDuplicateChecked = validateDuplicateName();
                return isDuplicateChecked ;
                
            }
      }

      setUserSubmitFunction(multiDocWizardAttributes);
      var addPickerWrapperFunction = 1;
   </script>

<c:set var="pickerParamFilterTypes" value="<%=request.getAttribute(\"pickerParamFilterTypes\")%>" />

<c:choose>

  <c:when test='${param.invokedfrom == "docsb"}' >
    <!-- If New Document wizard is invoked from Edit Structure, association constraints
         need to be enforced. (Please see the Javadoc for DefaultAssociationConstraintIT
         for more details). The list of creatable types needs to be filtered out to
         inlcude only the types allowed by association constrains. This is achieved by
         finding the list of valid (allowable) types using the tag class 
         getValidRoleBTypesForSelectedDocument below and then setting the type picker's 
         'type' parameter to 'ROOT_TYPES'-->
      <docmgnt:getValidRoleBTypesForSelectedDocument var="roleBDocTypes" />
      <jca:configureTypePicker>
          <c:forEach var="item" items="${roleBDocTypes}">
              <p:pickerParam name="seedType" value="${item}"/>
          </c:forEach>
          <c:choose>
              <c:when test='${not empty pickerParamFilterTypes}'>
                  <c:forEach var="item" items="${pickerParamFilterTypes}">
                      <p:pickerParam name="filterType" value="${item}"/>
                  </c:forEach>          
              </c:when>
              <c:otherwise>
                  <p:pickerParam name="filterType" value="wt.federation.ProxyDocument"/>
                  <p:pickerParam name="filterType" value="wt.doc.WTDocument|com.ptc.InterferenceDetectionDefinition"/>
              </c:otherwise>
          </c:choose>
          <p:pickerParam name="type" value="ROOT_TYPES"/>
      </jca:configureTypePicker>
   </c:when>

  <c:when test='${param.invokedfrom == "config_link_table"}' >
        <%-->   The action to create a document is from a configurable link table,
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
    <%-->   The action to create a document is not from a Part Details or Part Instance
        details page, 3rd level nav References Document table or the wcPDMMethod
        is true (any type of document can be created from the References Document
        table) don't filter out Reference Documents and its sub types. <--%>
    
    <c:choose>  
        <c:when test='${not empty pickerParamFilterTypes}'>
            <jca:configureTypePicker>
                    <c:forEach var="item" items="${pickerParamFilterTypes}">
                    <p:pickerParam name="filterType" value="${item}"/>
                </c:forEach>        
            </jca:configureTypePicker>
        </c:when>
        <c:otherwise>
            <jca:configureTypePicker>       
                <p:pickerParam name="filterType"
                    value="wt.doc.WTDocument|com.ptc.InterferenceDetectionDefinition"/>
            </jca:configureTypePicker>
        </c:otherwise>
    </c:choose>
    
   </c:when>
   <c:otherwise>
    <%-->   Filter out Reference Documents and their sub types from the Part Details
        page 3rd level nav References Documents table document create action<--%>
    
    <c:choose>  
        <c:when test='${not empty pickerParamFilterTypes}'>
            <jca:configureTypePicker>
                    <c:forEach var="item" items="${pickerParamFilterTypes}">
                    <p:pickerParam name="filterType" value="${item}"/>
                </c:forEach>        
            </jca:configureTypePicker>
        </c:when>
        <c:otherwise>
            <jca:configureTypePicker>
            <p:pickerParam name="filterType"
                value="wt.doc.WTDocument|com.ptc.ReferenceDocument"/>
            <p:pickerParam name="filterType"
            value="wt.doc.WTDocument|com.ptc.InterferenceDetectionDefinition"/>
            </jca:configureTypePicker>
        </c:otherwise>
    </c:choose>
            
    </c:otherwise>
</c:choose>

<%  /*When Security Label validation is enabled i.e. disableSecureUpload is "false",
     securityLabelStep appears before the setTypeAndAttributesWizStepForCreateMulti*/
    boolean disableSecureUpload = AttachmentsWebHelper.isSecureUploadDisabled();
    if (!disableSecureUpload) {
%>
    <jca:wizard buttonList="CreateMultiDocsWizardButtonsNoApply" helpSelectorKey="DocMgmtDocCreateMultiple">
    
	<%-- defines the context when coming in from DTI (or if no context is set) --%>
    <jca:wizardStep action="setContextWizStep" type="object"/>
      
	    <%-- Set Security Label Step --%>
      <jca:wizardStep action="securityLabelStep" type="securityLabels"/>
	  
	  <%-- Define Item  Step merged with Set Attributes For Create Multi Step Story Id: B-51319 --%>
      <jca:wizardStep action="setTypeAndAttributesWizStepForCreateMulti" type="document" />

    </jca:wizard>
<%
    /*When Security Label validation is disabled i.e. disableSecureUpload is "true",
    setTypeAndAttributesWizStepForCreateMulti comes before the securityLabelStep*/
    }
    else {
%>
<jca:wizard buttonList="CreateMultiDocsWizardButtonsNoApply" helpSelectorKey="DocMgmtDocCreateMultiple">

    <%-- defines the context when coming in from DTI (or if no context is set) --%>
    <jca:wizardStep action="setContextWizStep" type="object"/>

      <%-- Define Item  Step merged with Set Attributes For Create Multi Step Story Id: B-51319 --%>
      <jca:wizardStep action="setTypeAndAttributesWizStepForCreateMulti" type="document" />

        <%-- Set Security Label Step --%>
      <jca:wizardStep action="securityLabelStep" type="securityLabels"/>

    </jca:wizard>
<%
    }
%>
    <%--- If we are not DTI then add the applet for doing file browsing and file uploads --%>
   <wctags:fileSelectionAndUploadAppletUnlessMSOI forceApplet='${param.forcedFilePath != null }'/>
    

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
