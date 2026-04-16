<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/jcaMvc" prefix="mvc"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/picker" prefix="p"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/partRelDoc" prefix="partRelDoc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/configlinkui" prefix="clui"%>
<%@ taglib prefix="w" uri="http://www.ptc.com/windchill/taglib/wrappers"%>
<%@ taglib prefix="docmgnt" uri="http://www.ptc.com/windchill/taglib/docmgnt" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/dti" prefix="dti"%>
<%@ page import="com.ptc.windchill.enterprise.doc.DocumentConstants" %>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<br>

<%
String showPrimaryContent = "true";
String showTemplatePicker = "true";
String showPrimaryContentFromRequest = request.getParameter("show_primary_content");
String showTemplatePickerFromRequest = request.getParameter("show_template_picker");


if ("false".equals(showPrimaryContentFromRequest))
{
    showPrimaryContent = showPrimaryContentFromRequest;
}
else
{
    showPrimaryContentFromRequest = (String)request.getAttribute("show_primary_content");
    if ("false".equals(showPrimaryContentFromRequest))
        showPrimaryContent = showPrimaryContentFromRequest;
}


if ("false".equals(showTemplatePickerFromRequest))
{
    showTemplatePicker = showTemplatePickerFromRequest;
}
else
{
    showTemplatePickerFromRequest = (String)request.getAttribute("show_template_picker");
    if ("false".equals(showTemplatePickerFromRequest))
        showTemplatePicker = showTemplatePickerFromRequest;
}


%>


<c:set var="showPrimaryContent" value="<%=showPrimaryContent%>" />
<c:set var="showTemplatePicker" value="<%=showTemplatePicker%>" />

<c:choose>
    <c:when test='${showTemplatePicker}'>
        <div id='templatePickerDiv' style="visibility:hidden">
        <jca:renderPropertyPanel>
            <docmgnt:templatePicker id="createType"/>
            <jca:addPlaceHolder id="driverAttributes" />
        </jca:renderPropertyPanel>
        </div>
    </c:when>
    <c:otherwise>
        <input type="hidden" name="templateOid" value="default_selection"/>
    </c:otherwise>
</c:choose>
<div id='primaryContentHzLine' style="visibility:hidden">
<hr>
</div>
<%-- Renders the primary attachment component --%>
<c:choose>
    <c:when test='${showPrimaryContent}'>
        <wctags:primaryAttachmentWithMSOI fixedFilePath="${fixedFilePath}" fixedFileUpload="${fixedFileUpload}" defaultNameJSFunction="docSetName"/>
        <div id='templateContentDiv' style="visibility:hidden">
            <w:label id="templateContent" value="${templateSelectionMessage}"></w:label>
            <hr/>
        </div>
    </c:when>
</c:choose>

<mvc:attributesTableWizComponent/>
<%
    String userAgent = commandBean.getTextParameter("ua");
    boolean isDTI = false;
    boolean isOffice365 = false;
    if(userAgent != null && userAgent.equals("DTI"))
        isDTI=true;
    if(userAgent != null && userAgent.equals("OFFICE365"))
        isOffice365=true;

    String createFromTemplateDTIFromRequest = commandBean.getTextParameter("createFromTemplateDTI");
    boolean createFromTemplateDTI = false;
    if(createFromTemplateDTIFromRequest != null && createFromTemplateDTIFromRequest.equals("true"))
        createFromTemplateDTI=true;
%>
<%-- if checked it will cause the form processor to check out the document after it was created --%>

<div id='checkboxkeepCheckedOutDiv' style="visibility:hidden">

<% if(!isDTI && !isOffice365) { %>
    <%@ include file="/netmarkets/jsp/components/keepCheckedOutCheckbox.jspf"%>
<% }else if(isOffice365) { %>
    <wrap:checkBox name="keepCheckedOutDTI"
                                   id="keepCheckedOutDTI"
                                   label="${keepCheckedOutLabel}"
                                   renderLabel="true"
                                   renderLabelOnRight="true"
                                   renderExtra="onclick=checkedOutClicked();"/>
<% } else if(!createFromTemplateDTI) { %>
<%      if("true".equalsIgnoreCase(request.getParameter("isOutlook"))) { %>
            <wrap:checkBox name="keepCheckedOutDTI" id="keepCheckedOutDTI" label="${keepCheckedOutLabel}" renderLabel="true" renderLabelOnRight="true" renderExtra="disabled"/>
<%      } else { %>
            <c:choose>
                <c:when test='${empty dti:getFilePath(param.externalFormData)}'>
                    <%@ include file="/netmarkets/jsp/components/keepCheckedOutCheckbox.jspf"%>
                </c:when>
                <c:otherwise>
                    <wrap:checkBox name="keepCheckedOutDTI"
                                   id="keepCheckedOutDTI"
                                   label="${keepCheckedOutLabel}"
                                   renderLabel="true"
                                   renderLabelOnRight="true"
                                   renderExtra="onclick=checkedOutClicked();" />
                    <br/>
                    <%  String wizardResponseHandler = request.getParameter("wizardResponseHandler");
                        if(wizardResponseHandler != null && !(wizardResponseHandler.indexOf("dtiCommand=dragDropCreate")>0)){ %>
                            <docmgnt:prefCheckBox   name="keepDocOpen"
                                                    id="keepDocOpen"
                                                    label="${keepDocOpenLabel}"
                                                    checkBoxPref="/com/ptc/windchill/enterprise/attachments/keepDocOpen"
                                                    renderLabel="true"
                                                    renderLabelOnRight="true" />
                    <%	}	%>
                </c:otherwise>
            </c:choose>
<%		}
	} %>

</div>

<br>

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

<div id='checkboxcheckoutDownloadDiv' style="display: none">
<% if(!invokedFromDocSB){%>
<docmgnt:prefCheckBox name="checkoutDownload" id="checkoutDownload" label="${checkoutDownload}" checkBoxPref="/com/ptc/windchill/doc/defaultCheckoutOnCreateFromTemplate" renderLabel="true" renderLabelOnRight="true" />
<%}%>
</div>

<input type = "hidden" name = "lastSelectedType" id  ="lastSelectedType" value = "">
<input type = "hidden" name = "scmHiddenElement" id  ="scmHiddenElement" value = "">

<%@ include file="/netmarkets/jsp/util/end.jspf"%>