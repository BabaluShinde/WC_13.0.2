<%@ page import="com.ptc.netmarkets.util.beans.NmCommandBean"
%><%@ page import="wt.fc.ObjectIdentifier"
%><%@ page import="wt.util.WTMessage"
%><%@ page import="com.ptc.netmarkets.model.NmOid"
%><%@ page import="com.ptc.windchill.enterprise.mail.MailToBuilder"
%><%@ page import="com.ptc.windchill.enterprise.team.teamResource"
%><%@ page import="wt.util.HTMLEncoder"
%><%@ page import="wt.inf.team.ContainerTeamManaged"
%><%@ taglib prefix="fmt" uri="http://www.ptc.com/windchill/taglib/fmt"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ include file="/netmarkets/jsp/util/beginPopup.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/core" prefix="wc"%>
<%@ taglib prefix="w" uri="http://www.ptc.com/windchill/taglib/wrappers"%>
<%@ page import="com.ptc.jca.json.table.TableConfigHolder" %>
<%@ page import="com.ptc.mvc.components.FindInTableMode" %>
<%@ page import="wt.preference.PreferenceHelper" %>
<%@ page import="wt.preference.PreferenceClient" %>
<%@ page import="com.ptc.jca.json.table.TableConfigHolder" %>
<%@ page import="com.ptc.windchill.enterprise.attachments.attachmentsResource" %>
<%@ page import="com.ptc.windchill.enterprise.doc.documentResource"%>

<%! private static final String TEAM_RESOURCE = "com.ptc.windchill.enterprise.team.teamResource";%>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.ptc.windchill.enterprise.team.teamResource" />

<fmt:message var="selectedRecipients" key="SELECT_RECIPIENTS" />
<fmt:message var="subject" key="SUBJECT"  />
<fmt:message var="messageLabel" key="MESSAGE" />
<fmt:message var="additionalMessageLabel" key="ADDITIONAL_MESSAGE" />

<fmt:setBundle basename="com.ptc.windchill.enterprise.attachments.attachmentsResource" />
<fmt:message var="att_name"         key="<%= attachmentsResource.ATTACHMENT_NAME%>" />
<fmt:message var="att_size"         key="<%= attachmentsResource.ATTACHMENT_FILESIZE %>" />
<fmt:message var="att_format"           key="<%= attachmentsResource.FORMAT %>" />
<fmt:message var="att_description"      key="<%= attachmentsResource.ATTACHMENT_DESCRIPTION %>" />
<fmt:message var="att_lastModified"     key="<%= attachmentsResource.LAST_MODIFIED %>" />
<fmt:message var="att_modifiedBy"       key="<%= attachmentsResource.LAST_MODIFIED_BY %>" />
<fmt:message var="attachments"          key="<%= attachmentsResource.ATTACHMENT_LABEL  %>" />
<fmt:message var="primaryContent"       key="<%= attachmentsResource.PRIMARY_CONTENT  %>" />

<fmt:setBundle basename="com.ptc.windchill.enterprise.doc.documentResource" />
<fmt:message var="ownEmailClient"       key="<%= documentResource.OWN_EMAIL_CLIENT_LABEL %>" />


<%
String action = HTMLEncoder.encodeForHTMLContent(request.getParameter("actionName"));
String onMainTabStr = HTMLEncoder.encodeForHTMLContent(request.getParameter("onMainTab"));
boolean onMainTab = (onMainTabStr != null && Boolean.valueOf(onMainTabStr));
WTContainerRef ref = commandBean.getContainerRef();
commandBean.setCompContext(nmcontext.getContext().toString()); 
commandBean.setRequest(request);
wt.util.EncodingConverter ec = new wt.util.EncodingConverter();
java.util.Locale locale1 = localeBean.getLocale();
ResourceBundle teamRb = ResourceBundle.getBundle(TEAM_RESOURCE, locale1);
String subject = null;

String emailBody = null;

if (request.getParameter("body") != null) {
   emailBody = NmCommandBean.convert(request.getParameter("body"));
}
else {
   emailBody = teamRb.getString("EMAIL_PAGE");
}
String emailBodyEncoded = HTMLEncoder.encodeForHTMLContent(emailBody);

String body2 = null;

if (request.getParameter("body2") != null) {
   body2 = NmCommandBean.convert(request.getParameter("body2"));
}

// The var referenceLink comes from below when it gets the action from the opener.
// This is so that if someone goes into a subfolder then the correct link will be placed in the
// email.  SPR 1566443 - RA
String st0 = "<a href=\"javascript:var a=wfWindowOpen(referenceLink,'','')\">" + emailBody + "</a>";
String lang = locale1.getLanguage();
%>

 <%
//} // end mailto link using existing mail application code

String recipientError = (WTMessage.getLocalizedMessage(TEAM_RESOURCE, "EMAIL_PAGE_RECIPIENT_ERROR", null, locale1)).replace("\"","\\\"").replace("'", "\\'");
String subjectError = (WTMessage.getLocalizedMessage(TEAM_RESOURCE, "EMAIL_PAGE_SUBJECT_ERROR", null, locale1)).replace("\"","\\\"").replace("'", "\\'");
String subjectLengthError = (WTMessage.getLocalizedMessage(TEAM_RESOURCE, "EMAIL_SUBJECT_TOO_LONG", null, locale1)).replace("\"","\\\"").replace("'", "\\'");
%>

  <!-- * means required -->

<table border="0">
   <!-- Select recipients -->
   <tr id="recipients">
     <td valign="top" align="right" nowrap>
 <br>
	 <w:label id="recipient_label" name="recipient_label" required="true" value="* ${selectedRecipients}" styleClass="wizardlabel"/>
       &nbsp;&nbsp;
     </td>
     <td valign="top" align="left">
         <% 
            // The Team table is displayed only if there in a CTM container context and the onMainTab flag is false
            // In other scenarios, a regular table with users will be displayed
            if ( ("emailPage").equals(action) &&  !onMainTab  && ref != null && ref.getContainer() instanceof ContainerTeamManaged)
            {
               NmOid containerOid = NmOid.newNmOid((ObjectIdentifier)ref.getKey());  //OR:wt.pdmlink.PDMLinkProduct:54403
         %>
           <wctags:teamComponent id="containerTeamEmailTable" 
	 	    title="Team" 
	 	    selectionScope="Container" 
	 	    teamContext="<%=containerOid.toString()%>" 
	 	    emailMembership="true"
                    filterEmail="true" />
         
         <%
            } else { 
                %>
                <jsp:include page="${mvc:getComponentURL('netmarkets.team.emailPage')}"/>
          <%
             }
          %>
     </td>
   </tr>
   <tr>
     <td>
       &nbsp;
     </td>
   </tr>
   
   
    
  
<!-- primary Attachment -->  
  <%   String paraCheckinOID = request.getParameter("oid");   %>
<c:if test="${param.checkinOid != null}">
   <c:set var="checkinOid" value="<%= paraCheckinOID %>" />
</c:if>
<c:if test="${checkinOid != null}">
  <jsp:setProperty name="commandBean" property="elemAddress" value="${checkinOid}" />
</c:if>

<jca:describeTable var="primaryTableDescriptor" id="primary.attachments.list.emailPage" type="wt.content.ApplicationData" label="${primaryContent}" scope="request">
   <jca:setComponentProperty key="actionModel" value="${action}"/>
   <jca:setComponentProperty key="variableRowHeight" value="true"/>
   <jca:setComponentProperty key="<%=TableConfigHolder.FIND_IN_TABLE_MODE%>" value="<%=FindInTableMode.DISABLED%>"/>   
   <%-- the gridfileinputhandler plugin will disable many grid features, so that using a browser input field in the ext grid will work--%>
   <jca:setTablePlugin ptype="gridfileinputhandler"/>

  <jca:describeColumn id="type_icon"   sortable="false" />
  <jca:describeColumn  id="fileName"  sortable="false"   label="${att_name}" >
  	<jca:setComponentProperty   key="useExact"   value="true"/>
  </jca:describeColumn>
   <jca:describeColumn  id="fileSize"  sortable="false"   label="${att_size}" >
  	<jca:setComponentProperty   key="useExact"   value="true"/>
  </jca:describeColumn>
   <jca:describeColumn  id="format"  sortable="false"   label="${att_format}" >
  	<jca:setComponentProperty   key="useExact"   value="true"/>
  </jca:describeColumn>
   <jca:describeColumn  id="description"  sortable="false"   label="${att_description}" >
  	<jca:setComponentProperty   key="useExact"   value="true"/>
  </jca:describeColumn>
   <jca:describeColumn  id="thePersistInfo.modifyStamp"  sortable="false" label="${att_lastModified}" >
  	<jca:setComponentProperty   key="useExact"   value="true"/>
   </jca:describeColumn>
   <jca:describeColumn  id="modifiedBy"   sortable="false"   label="${att_modifiedBy}" >
  	<jca:setComponentProperty   key="useExact"   value="true"/>
  </jca:describeColumn>
  
</jca:describeTable>

<c:set target="${primaryTableDescriptor.properties}" property="selectable" value="true"/>

<jca:getModel var="primaryTableModel" descriptor="${primaryTableDescriptor}"
               serviceName="com.ptc.windchill.enterprise.attachments.commands.AttachmentQueryCommands"
               methodName="getAttachmentsForAppData">
    <jca:addServiceArgument value="${commandBean}" type="com.ptc.netmarkets.util.beans.NmCommandBean" />
    <jca:addServiceArgument value="<%= wt.content.ContentRoleType.PRIMARY %>"/>
</jca:getModel>



<tr>
    <td valign="top" align="right" nowrap>
    <br>
     <w:label id="AttachmentLabel" name="AttachmentLabel" value="${attachments}:" styleClass="wizardlabel"/>
       &nbsp;&nbsp;
     </td>
    <td valign="top" align="left">
    <jca:renderTable model="${primaryTableModel}" helpContext="${helpFileName}"/>
    </td>
</tr>

<!-- primary Attachment End -->
<!-- secondary Attachment -->

 <jca:describeTable var="secTableDescriptor" id="attachments.list.emailPage" type="wt.content.ApplicationData" label="${attachments}" mode="VIEW" scope="request">
  <jca:setComponentProperty key="actionModel" value="${action}"/>
  <jca:setComponentProperty key="variableRowHeight" value="true"/>
  <jca:setComponentProperty key="<%=TableConfigHolder.FIND_IN_TABLE_MODE%>" value="<%=FindInTableMode.DISABLED%>"/>
  
  <!-- the gridfileinputhandler plugin will disable many grid features, so that using a browser input field in the ext grid will work -->
  <jca:setTablePlugin ptype="gridfileinputhandler"/>

  <jca:describeColumn id="type_icon"   sortable="false" />
  <jca:describeColumn  id="fileName"  sortable="false"   label="${att_name}" >
  	<jca:setComponentProperty   key="useExact"   value="true"/>
  </jca:describeColumn>
   <jca:describeColumn  id="fileSize"  sortable="false"   label="${att_size}" >
  	<jca:setComponentProperty   key="useExact"   value="true"/>
  </jca:describeColumn>
   <jca:describeColumn  id="format"  sortable="false"   label="${att_format}" >
  	<jca:setComponentProperty   key="useExact"   value="true"/>
  </jca:describeColumn>
   <jca:describeColumn  id="description"  sortable="false"   label="${att_description}" >
  	<jca:setComponentProperty   key="useExact"   value="true"/>
  </jca:describeColumn>
   <jca:describeColumn  id="thePersistInfo.modifyStamp"  sortable="false" label="${att_lastModified}" >
  	<jca:setComponentProperty   key="useExact"   value="true"/>
   </jca:describeColumn>
   <jca:describeColumn  id="modifiedBy"   sortable="false"   label="${att_modifiedBy}" >
  	<jca:setComponentProperty   key="useExact"   value="true"/>
  </jca:describeColumn>
 
</jca:describeTable>
 
<c:set target="${secTableDescriptor.properties}" property="selectable" value="true"/>
 
<jca:getModel var="secondaryTableModel" descriptor="${secTableDescriptor}"
              serviceName="com.ptc.windchill.enterprise.attachments.commands.AttachmentQueryCommands"
              methodName="getAttachmentsForAppData">
    <jca:addServiceArgument value="${commandBean}" type="com.ptc.netmarkets.util.beans.NmCommandBean" />
    <jca:addServiceArgument value="<%= wt.content.ContentRoleType.SECONDARY %>"/>
</jca:getModel>

  <tr>
    <td valign="middle" align="right" nowrap></td>
    <td valign="top" align="left">
    <jca:renderTable model="${secondaryTableModel}" />
    </td>
</tr>


<!-- secondary Attachment End -->
  <tr>
  
    <td valign="middle" align="left" nowrap>
    
    </td>
     <td valign="middle" align="left">
     <input type="checkbox" id="isDTIMailClientEnabled" name="isDTIMailClientEnabled" value="true"/>
     <w:label id="DTIClientLabel" name="DTIClientLabel" value="${ownEmailClient}" styleClass="wizardlabel"/>
       &nbsp;&nbsp;
     </td>
</tr>
<tr>
     <td>
       &nbsp;
     </td>
   </tr>
 <!-- Subject -->
 
<% String subjectEncoded = (String)commandBean.getMap().get("DocumentName");%> 
 <tr>
     <td valign="middle" align="right" nowrap>
     <w:label id="subject_label" name="subject_label" required="true" value="* ${subject}" styleClass="wizardlabel"/>
       &nbsp;&nbsp;
     </td>
     <td valign="middle" align="left">
       <w:textBox id="subject" name="subject" required="true" maxlength="1000" size="60" value="<%=subjectEncoded%>" enabled="true" hidden="false" />
     </td>
   </tr>
   <tr>
     <td>
       &nbsp;
     </td>
   </tr>
   <!-- Show Message Link only if Email Page -->
   <c:if test="${actionName == 'emailPage'}">

   <tr>
     <td>
       &nbsp;
     </td>
   </tr>
   </c:if>
   <tr>
     <td valign="top" align="right" nowrap>
       <!-- Additional Message -->
     <w:label id="additionalMessage_label" name="additionalMessage_label" required="false" value="${additionalMessageLabel}" styleClass="wizardlabel"/>
       &nbsp;&nbsp;
     </td>
     <td valign="top" align="left">
		<textarea id="emailText" name="emailText" required="false" maxLength="1000" rows="8" cols="80"> </textarea>
     </td>
   </tr>
</table>

<wc:htmlEncoder encodingMethod="encodeForHTMLAttribute" text="${actionName}" var="actionNameEncodedForHTMLAttribute" scope="page"/>

<input type="hidden" name ="actionName" value="${actionNameEncodedForHTMLAttribute}" />
<%
ArrayList list  = (ArrayList) commandBean.getMap().get("selectedUsers");
   if(list!= null && !list.isEmpty()) {
      for (Iterator iterator = list.iterator(); iterator.hasNext();) {
         String oid = (String) iterator.next();
%>
       <input type="hidden" name ="selectedOid" value="<%=oid%>"/>
<%
      }
   } else if(list == null || (list!= null && list.isEmpty())) {
%>
    <input type="hidden" name ="selectedOid" value="None"/>
<%
   }
%>
<script type="text/javascript" src="netmarkets/javascript/util/ckeditor/ckeditor.js" />
<script type="text/javascript">
PTC.navigation.loadScript('netmarkets/javascript/util/ckeditor/ckeditor.js');
</script>
<script type="text/javascript">

    var editor = CKEDITOR.replace('emailText', {
        height: '200',
        width: '650',
        resize_minHeight: 310,
        resize_minWidth: 650,
        entities_processNumerical: true,
        enterMode: 'div',
        toolbarStartupExpanded: true,
        format_tags: 'div;p;h1;h2;h3;h4;h5;h6',
        pasteFromWordRemoveFontStyles: false,
        pasteFromWordRemoveStyles: false,
        skin: 'kama',
        font_names: 'Arial;Comic Sans MS;Courier New;Tahoma;Times New Roman;Verdana',
        font_defaultLabel: 'Arial',
        fontSize_defaultLabel: '12px',
        disableNativeSpellChecker: false,
        language:'<%=locale1%>',//Added to externalize tooltips on Description editor tool bar
        menu_groups: 'clipboard,form,tablecell,tablecellproperties,tablerow,tablecolumn,table,anchor,link,flash,checkbox,radio,textfield,hiddenfield,imagebutton,button,select,textarea',
        removePlugins: 'elementspath',
        uiColor: '#F1F1F1',
        keystrokes: [[CKEDITOR.CTRL + CKEDITOR.ALT + 13/*Enter*/, 'maximize'], [CKEDITOR.CTRL + CKEDITOR.ALT + 108/*Enter*/, 'maximize'], [CKEDITOR.CTRL + 90 /*Z*/, 'undo'], [CKEDITOR.CTRL + 89 /*Y*/, 'redo'], [CKEDITOR.CTRL + CKEDITOR.SHIFT + 90 /*Z*/, 'redo'], [CKEDITOR.CTRL + 66 /*B*/, 'bold'], [CKEDITOR.CTRL + 73 /*I*/, 'italic'], [CKEDITOR.CTRL + 76 /*L*/, 'link'], [CKEDITOR.CTRL + 85 /*U*/, 'underline']],
        toolbar: [['PasteText','PasteFromWord'],['Undo','Redo','-','Find','Replace','-','SelectAll','RemoveFormat'],['NewPage'],['Bold','Italic','Underline','Strike','-','Subscript','Superscript'],['NumberedList','BulletedList','-','Outdent','Indent'],['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],['Link','Unlink'],['Table','HorizontalRule'],['Format','Font','FontSize'],['TextColor','BGColor'],['Maximize']]
    });

</script>
<script>

   function validateRecipients() {
    if(window.document.forms.mainform.actionName.value == 'emailPage') {
      for (i = 0; i < document.mainform.elements.length; i++) {
         field = document.mainform.elements[i];
         if (field.type != "checkbox") {
            continue;
         }
         var parentNode = field.parentNode.id;
         var EMAIL_PAGE_TEAM_TABLE_ID = "windchill.enterprise.team.emailPageTeam";
         
         if((parentNode.indexOf(EMAIL_PAGE_TEAM_TABLE_ID) > -1)||(parentNode.indexOf("netmarkets.team.emailPage")>-1)){
             if (field.checked) {
                return true;
             }
         }
      }
      alert("<%=recipientError%>");
      window.document.forms.mainform.dialogButton.value = "none";
     return false;
    }
    if(window.document.forms.mainform.selectedOid.value == 'None') {
       alert("<%=recipientError%>");
       window.document.forms.mainform.dialogButton.value = "none";
       return false;
    }
     return true;
    }

   function validateSubject() {
      if (document.mainform.subject.value == "") {
         document.mainform.subject.focus()
         alert("<%=subjectError%>")
         window.document.forms.mainform.dialogButton.value = "none";
         return false;
      }

      if (document.mainform.subject.value.length > 1000) {
         document.mainform.subject.focus()
         alert("<%=subjectLengthError%>");
         window.document.forms.mainform.dialogButton.value = "none";
         return false;
      }

      return true;
   }

   function validateEntries() {
      return (validateRecipients() && validateSubject());
   }

   setUserSubmitFunction(validateEntries);
   
   
   PTC.wizard.fireOnAfterAction = function( formResult ) {
         var status = formResult.status;
         if( status == 0 /*SUCCESS*/ || status == 2 /*NON_FATAL_ERROR*/ ) {
             var url = formResult.extraData.downloadURL;
             if( url && url.trim() != '' ) {
                 formResult.nextAction = PTC.cat.FormResult.NONE;
            var pWindow = window.location.replace(url);
            pWindow.setTimeout(function(){pWindow.close()},10000);

             } else {
                 PTC.navigation.reload();
             }
         }
         return false;
  };
				 
</script>



<%@ include file="/netmarkets/jsp/util/end.jspf"%>
