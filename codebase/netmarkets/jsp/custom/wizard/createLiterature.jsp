<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<jca:initializeItem 
    operation="${createBean.create}" 
    baseTypeName="wt.doc.WTDocument"/>

<jca:wizard title="Create Literature"
            buttonList="DefaultWizardButtons">

    <!-- Your Step -->
    <jca:wizardStep action="createLiteratureStep" type="Novel"/>
	 <jca:wizardStep action="defineItemAttributesWizStep" type="object"/>
      <jca:wizardStep action="attachments_step" type="attachments" />

</jca:wizard>

<%@include file="/netmarkets/jsp/util/end.jspf"%>