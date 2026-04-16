<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
    <jca:wizard buttonList="AttachmentsAttributesWizardButtons">
        <jca:wizardStep action="primary_attributes_step" type="attachments" />
    </jca:wizard>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>
