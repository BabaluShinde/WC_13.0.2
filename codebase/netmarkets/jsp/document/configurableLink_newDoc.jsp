<%@ taglib uri="http://www.ptc.com/windchill/taglib/configlinkui" prefix="configlinkui"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<!-- This tag verifies that the action is sending the type of configurable link.
     If not an exception with an informative message is thrown. -->
<configlinkui:linkTypeChecker  method="isConfigurableDescribeLink" var="isConfigurableDescribeLink"/>
		
<!-- Include the document create wizard -->
<%@ include file="/netmarkets/jsp/document/create.jspf"%>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>		
