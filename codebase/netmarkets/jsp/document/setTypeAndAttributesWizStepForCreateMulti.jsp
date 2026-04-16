<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib prefix="attachments" tagdir="/WEB-INF/tags/attachments"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/jcaMvc" prefix="mvc"%>
<%@ taglib prefix="w" uri="http://www.ptc.com/windchill/taglib/wrappers"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/core" prefix="wc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/dti" prefix="dti"%>

<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ include file="/netmarkets/jsp/components/defineItemReadOnlyPropertyPanel.jspf"%>
<%@ include file="/netmarkets/jsp/attachments/initAttachments.jspf"%>
<%@ include file="/netmarkets/jsp/attachments/initPTCAttachments.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/partRelDoc" prefix="partRelDoc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/picker" prefix="p"%>

<%@ page import="wt.access.configuration.SecurityLabelsHelper" %>
<%@ page import="wt.util.WTProperties" %>
<%@ page import="wt.util.HTMLEncoder" %>


<!-- Get the localized strings from the resource bundle -->
<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.ptc.windchill.enterprise.doc.documentResource" />

<fmt:message var="att_folderLocation_label" key="LOCATION" scope="request"/>
<fmt:message var="att_location"       key="FILE_PATH_COLUMN" scope="request"/>
<fmt:message var="att_file_name"      key="FILE_NAME_COLUMN" scope="request"/>
<fmt:message var="att_file_location"      key="FILE_LOCATION_COLUMN" scope="request"/>
<fmt:message var="att_name"           key="NAME_COLUMN" scope="request"/>
<fmt:message var="att_number"         key="NUMBER_COLUMN" scope="request"/>
<fmt:message var="lifecycle_template" key="LIFECYCLE_TEMPLATE" scope="request"/>

<input type="hidden" id="newFiles" name="newFiles">
<input type="hidden" id="fileSep" name="fileSep" value="\">
<input type="hidden" id="previousPickerValue" name="previousPickerValue" value="">
 
<%  
    String userAgent = commandBean.getTextParameter("ua");
    boolean isDTI = false;
    String folderOid = commandBean.getTextParameter("oid");
    if(userAgent != null && userAgent.equals("DTI"))
    isDTI=true;
    
    String isDnDrequest = commandBean.getTextParameter("isDnD");
    boolean isDnD = false;
    if(isDnDrequest != null && isDnDrequest.equalsIgnoreCase("true"))
        isDnD = true;
	
    String dtiCommand = commandBean.getTextParameter("dtiCommand");
    boolean isDtiCreateMulti = false;
    if(dtiCommand != null && dtiCommand.equalsIgnoreCase("createMulti"))
        isDtiCreateMulti = true;
        
    //This is to get OID of Cabinet and used during validation. Required when files are dropped at PRODUCT level
    //TODO:: Ideally Cabinet oid should have been passed from DTI as a post data 
    try{
        if(isDTI && folderOid == null && commandBean.getContainer() != null){           
            if(commandBean.getContainer().getDefaultCabinet() != null){
                com.ptc.netmarkets.model.NmOid nmoid = new com.ptc.netmarkets.model.NmOid(commandBean.getContainer().getDefaultCabinet());
                folderOid = nmoid.toString();
                }
        }
    }catch(Exception e){
    }
	
    boolean isSecurityLabelsEnabled = SecurityLabelsHelper.isSecurityLabelFeatureEnabled();
    
    WTProperties props = WTProperties.getLocalProperties();
    boolean enableRequiredViewDnD =  props.getProperty("wt.multidoc.DnD.requiredView.enable", false);

%>

<c:set var="isDTI" value="<%=isDTI%>" scope="request"/>
<c:set var="isDnD" value="<%=isDnD%>" scope="request"/>
<c:set var="enableRequiredViewDnD" value="<%=enableRequiredViewDnD%>" scope="request"/>

<%  
    if (isSecurityLabelsEnabled) {
%>
<jsp:setProperty name="createBean" property="includeTypePicker" value="true"/>
<%  
    }
%>

<jsp:include page="/netmarkets/jsp/document/setTypeAndAttributesWizStepForCreateMultiInclude.jsp" />

<script Language="JavaScript">
var createMultiTableID = "multiDocWizAttributesTableDescriptor";
var cancelConfirmation = 0;
PTC.onReady(function() {
    console.log("ready");
    if (<%= isDnD %>) {
        setTimeout(function(){
            var files = window.opener.files;
            Ext.ux.grid.BufferView.prototype.cacheSize = files.length;
            createRowsAndStartUpload("newFiles", files, null);
            if (<%= enableRequiredViewDnD %>) {
               var viewCombo = Ext.getCmp(createMultiTableID + "filterSelect");
               console.log("viewCombo: " + viewCombo);
               viewCombo.disable();
            }
        }, 500);
    }
});
</script>

   <!-- This hidden value will be required to query wizard source in java script -->
   <input type="hidden" id="isDTI" name="isDTI" value="<%=isDTI%>">

   <c:if test='${isDTI}'>
     <c:choose>
        <c:when test="${dti:getMultiDnDFiles(param.externalFormData) != null}">
            <c:set var="multiDnDFiles" value="${dti:getMultiDnDFiles(param.externalFormData)}" scope="request" />
            <input type="hidden" id="dndFiles" name="dndFiles" value="${requestScope.multiDnDFiles}"/>
            <input type="hidden" id="selectedFolderFromFolderContext" name="selectedFolderFromFolderContext" value="<%=HTMLEncoder.encodeForHTMLAttribute(folderOid)%>"/>
        </c:when>
     </c:choose>
   </c:if>
   
   <c:set var="tableActionModelName" value="multi doc create table toolbar actions" scope="request"/>
   <c:if test='${isDTI}'>
        <c:set var="tableActionModelName" value="DnD multi doc create table toolbar actions" scope="request"/>
   </c:if>
   <c:if test='${isDnD}'>
        <c:set var="tableActionModelName" value="DnD folderbrowser multi doc create table toolbar actions" scope="request"/>
   </c:if>

 <!-- added to send itemTypeInstanceId in consecutive requests -->
<%
    if(isDTI){
        Object typeInstanceId = request.getParameter("itemTypeInstanceId");
        if(null!=typeInstanceId){
            commandBean.getParameterMap().put("itemTypeInstanceId",typeInstanceId);
        }
    }
%> 

<jsp:include page="/netmarkets/jsp/document/setTypeAndAttributesWizStepForCreateMultiInclude2.jsp" />

<script Language="JavaScript">
var required_view_label = '';
Ext.ComponentMgr.onAvailable( 'multiDocWizAttributesTableDescriptor',function (){
    var grid =Ext.getCmp('multiDocWizAttributesTableDescriptor');
    if(grid){
        grid.getStore().on('add', function(store) {
            grid.clearStickyConfig(); // reset values
            PTC.jca.ColumnUtils.resizeAllColumns(grid);
            return true;
        }, null, {single: true, delay:100}); // only run this function one time
        
        //For DTI DnD: The pre-populated table columns dosen't get resize, as above 'add' event will not get triggered. Hence added this this, but this single event seems not sufficient. 
        grid.getStore().on('datachanged', function(store) {
             grid.clearStickyConfig(); // reset values
             PTC.jca.ColumnUtils.resizeAllColumns(grid);
             return true;
         }, null, {single: true, delay:100}); // only run this function one time
    }	
    if(!<%=isDnD %>) {
        if(required_view_label == '') {
             var viewCombo = Ext.getCmp(createMultiTableID + "filterSelect");
             required_view_label = viewCombo.getRawValue();			 
        }
    }
});

if(addPickerWrapperFunction){
    // only add the picker wrapper once.
    addPickerWrapperFunction = 0;
    // From the common component we are calling our local pickerGo which calls the original pickerGo and then calls populateTemplates
    pickerGo = pickerGo.wrap(function(original,value, currentObjectHandle, template) {
       original(value, currentObjectHandle, template);
       var multiDocWizAttributesTableDescriptorDivTag = document.getElementById('multiDocWizAttributesTableDescriptorDiv');
       if(cancelConfirmation == 0){
           var table = tableUtils.getTable( createMultiTableID );
           var viewCombo = Ext.getCmp(createMultiTableID + "filterSelect");
           var currentView;
           if(!<%=isDnD %>) {
              currentView = viewCombo.getRawValue();
           }
		   
           if(<%=isDtiCreateMulti %> || <%=isSecurityLabelsEnabled %>) {
               if(required_view_label == '') {
                   var viewCombo = Ext.getCmp(createMultiTableID + "filterSelect");
                   required_view_label = viewCombo.getRawValue();			    
               }
           }
           //var required_view_label = document.getElementById("required_view_label").value;
           var dti = false;
           var dtiVal = document.getElementById('isDTI');
           if(dtiVal != null || dtiVal != 'undefined') dti = dtiVal;
           //show confirm dialog when there is data in table or view is not default view.
           var message ='com.ptc.windchill.enterprise.doc.documentResource.TYPE_CHANGE_CONFIRM';
           if(dti)
               message = 'com.ptc.windchill.enterprise.doc.documentResource.TYPE_CHANGE_CONFIRM_DND_DTI';
           if(value!=''){
               // change the style of DIV to visible once one type is selected.
               if(multiDocWizAttributesTableDescriptorDivTag != null){
                   multiDocWizAttributesTableDescriptorDivTag.style.visibility = 'visible';
               }
               table.show();
               
               //In case of DTI Multi Drag-n-Drop, table data persists and only view changes to default view and message accordingly.     
               if((!dti && table.store.getCount() > 0 || required_view_label != currentView) || (dti && required_view_label != currentView)) {               
                   if(JCAConfirm(message)){
                       removeDataAndChangeTableView(table, viewCombo, required_view_label, currentView);
                   } else{
                       // go back to the old value of the type picker
                       var typeComboBox = document.getElementById("createType");
                       typeComboBox.value = document.getElementById("previousPickerValue").value;
                       cancelConfirmation = 1;
                       if(document.getElementById("previousPickerValue").value==""){
                           hideTableFunction();
                       }
                       // force to trigger the onChange Event
                       typeComboBox.onchange();
                       return;
                   }				 
               } else {
                   //Refresh the view afterRefresh, in oder to have proper TI set and available. SPR#2082072
                   //refreshView(viewCombo, required_view_label);
               }
           }else{
               if((!dti && table.store.getCount() > 0 || required_view_label != currentView) || (dti && required_view_label != currentView)) {
                   //show confirm dialog when there is data in table or view is not default view.
                   if(JCAConfirm(message)){
                       removeDataAndChangeTableView(table, viewCombo, required_view_label, currentView);
                   } else{
                       //the table should not be refreshed and the type picker should show the last selected value. 
                       //Basically we should not allow to change to "Select A Type" in case cancel is selected
                       var typeComboBox = document.getElementById("createType");
                       typeComboBox.value = document.getElementById("previousPickerValue").value;
                       cancelConfirmation = 1;
                       //no need to change the previousPickerValue and run original function. 
                       // and the table will not hide
                       // force to trigger the onChange Event
                       typeComboBox.onchange();
                       return;
                   }
               } 
               setTimeout(hideTableFunction,500); 
           }
       }
       //save the current value of the type drop down list
       document.getElementById("previousPickerValue").value = value;
       cancelConfirmation = 0;
    })
}


var createType = document.getElementById('createType');
// When the type is selected by default , we need to show the table
if (createType!= null && createType!= 'undefined' && createType.value!= 'undefined' && createType.value!= "") {
    var multiDocWizAttributesTableDescriptorDivTag = document.getElementById('multiDocWizAttributesTableDescriptorDiv');
    if(multiDocWizAttributesTableDescriptorDivTag != null){
       multiDocWizAttributesTableDescriptorDivTag.style.visibility = 'visible';
    }
    document.getElementById("previousPickerValue").value = createType.value;
}
if(<%= isDnD %>) {
    var multiDocWizAttributesTableDescriptorDivTag = document.getElementById('multiDocWizAttributesTableDescriptorDiv');
    if(multiDocWizAttributesTableDescriptorDivTag != null){
       multiDocWizAttributesTableDescriptorDivTag.style.visibility = 'visible';
    }
}

PTC.driverAttributes.on("afterRefresh", function() {
    //If only one instanciable type is available, refresh the table as document type many not be set prorely. SPR#2096886
    //For one instanciable type, createType.length will be 2. 0='Select A Type' and 1=<Instanciable Document Type>

    //Refresh the table only afterRefresh, as it might cause timing issue i.e. proper TypeInstance not availabe
    //Refer PSR#2082072
     try{
        var table = tableUtils.getTable( createMultiTableID );
        table.store.on("exception", PTC.multi.doc.reloadEx, null, {single:true});
        errorPopup = false;
        PTC.jca.table.Utils.reload('multiDocWizAttributesTableDescriptor',{},true);       
     }
     catch( e) {
         //Ignoring this exception.
     }

});

// Overriding the refreshCurrentStep function which gets called from htmlviews.js when we click OK on the Customize Views popup.
//SPR 2252362(related old SPR 2049721):no need to override otherwise it will set view to required always. Just reload table and it will keep type picker also.
refreshCurrentStep = refreshCurrentStep.wrap(function(original) {    
    var table = tableUtils.getTable( createMultiTableID );
    table.store.on("exception", PTC.multi.doc.reloadEx, null, {single:true});
    errorPopup = false;
    tableUtils.reload(table);    
    //var viewCombo = Ext.getCmp(createMultiTableID + "filterSelect");
    //var currentView = viewCombo.getRawValue();
    //var required_view_label = document.getElementById("required_view_label").value;
    //removeDataAndChangeTableView(table, viewCombo, required_view_label, currentView);
})

var _responseText;
var errorPopup = false;
PTC.multi={};
PTC.multi.doc={};

PTC.jca.WJsonReader.prototype.read = PTC.jca.WJsonReader.prototype.read.wrap(function(original, response) {    
    _responseText = response.responseText;
    return original(response);
})

PTC.multi.doc.reloadEx = function() {
    if(_responseText){
        var index= _responseText.indexOf("<div id='multiDocWizAttributesTableDescriptorDiv'");
        _responseText=_responseText.substr(index);
        index= _responseText.indexOf("CAPTION class=\"errortitle\"");
        if(index>-1){
            // extract the error message
            responseText=_responseText.substr(index);
            index= _responseText.indexOf("FONT class=\"errorfont\">");
            _responseText=_responseText.substr(index+ "FONT class=\"errorfont\">".length + 1);
            index= _responseText.indexOf("</FONT>");
            _responseText=_responseText.substr(0,index);
            
            // DecodeHTML            
            _responseText = PTC.multi.doc.decodeHtml(_responseText);     

            // show the error
            alert(_responseText);
            errorPopup = true;        
            PTC.jca.table.Utils.removeAll(createMultiTableID);  
           
            // wrap the handleSubmitResult for DTI to block the form submission
            if (PTC.multi.doc.isDtiCreateMultiWizard()) {
                handleSubmitResult = handleSubmitResult.wrap(PTC.multi.doc.handleSubmitResult);
            }            
            
            // hide the loading mask
            var el = document.getElementById(createMultiTableID);
            var mask = Ext.fly(el).child('.x-grid3-scroller');            
            if(mask){
                Ext.fly(mask).removeClass("contentloading");  
            }
            
        } else {
            _responseText = undefined;           
        }
    }
};

PTC.jca.table.Utils.reloadLegacyTable = PTC.jca.table.Utils.reloadLegacyTable.wrap(function(original) {
    if(_responseText){
        _responseText = undefined;        
    } else {
        return original();
    }
})

PTC.multi.doc.decodeHtml = function (str) {        
    var e = document.createElement('div');     
    e.innerHTML = str;
    return e.childNodes[0].nodeValue;
}

PTC.multi.doc.handleSubmitResult = function (original, status, nextAction, js, URL, dynamicRefreshInfo) {    
    if (PTC.multi.doc.isDtiCreateMultiWizard()) {
        if (errorPopup) {
            console.log("DtiCreateMultiWizard - handleSubmitResult with error popup");
            return original(status, 1, js, '', dynamicRefreshInfo);
        } else {
            console.log("DtiCreateMultiWizard - handleSubmitResult");
            return original(status, nextAction, js, URL, dynamicRefreshInfo);
        }
    }
    return original(status, nextAction, js, URL, dynamicRefreshInfo);
}

PTC.multi.doc.isDtiCreateMultiWizard = function () {
    var isdti = document.getElementById('isDTI');
    if (isdti !== undefined && isdti !== null) {
        if (isdti.value === 'true') {
            return true;
        }
    }
    return false;
}

</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
