<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ include file="/netmarkets/jsp/attachments/initAttachments.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/attachments" prefix="attachments" %>

<%String mess=WTMessage.getLocalizedMessage( "com.ptc.windchill.enterprise.revisionControlled.insertWizardResource",
                         com.ptc.windchill.enterprise.revisionControlled.insertWizardResource.EMBEDDED_HELP_DOC, null, commandBean.getLocale() );%>

<%request.setAttribute("helpMessage",  mess);%>

<%request.setAttribute("searchObjType",  "wt.doc.WTDocumentMaster");%>

<jca:initializeItem operation="CREATE" attributePopulatorClass="com.ptc.windchill.enterprise.doc.forms.DocAttributePopulator" />

<script>
Event.observe(document, 'keypress', wizardKeyUpListener);

// Supress form submission twice due to hiting enter
function wizardKeyUpListener(event) {
   var target = Event.element(event);
   if (event.keyCode == Event.KEY_RETURN || event.which == Event.KEY_RETURN) {
      if (target != null && (target.tagName == "SELECT" || target.tagName == "TEXTAREA" || target.tagName == "INPUT")) {
          Event.stop(event);
       	  return false;
      }
   }
}
</script>

<jca:wizard helpSelectorKey="DocInsert_help" buttonList="DefaultWizardButtonsNoApply">
        <jca:wizardStep action="search_master_step" type="object" embeddedHelp="${helpMessage}"/>
        <jca:wizardStep action="insertDocWizardStep" type="document" />
        <jca:wizardStep action="securityLabelStep" type="securityLabels"/>
	<jca:wizardStep action="attachments_step" type="attachments" />
</jca:wizard>

<attachments:fileSelectionAndUploadApplet forceApplet='${param.addAttachments != null }'/>

<script language="JavaScript" src='netmarkets/javascript/util/revisionLabelPicker.js'></script>
    
<%@ include file="/netmarkets/jsp/util/end.jspf"%>
