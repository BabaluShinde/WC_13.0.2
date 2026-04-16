<%@ taglib uri="http://www.ptc.com/windchill/taglib/configlinkui" prefix="configlinkui"%>
<!-- This tag verifies that the action is sending the type of configurable link.
     If not an exception with an informative message is thrown. -->
<configlinkui:linkTypeChecker  method="isConfigurableDescribeLink" var="isConfigurableDescribeLink"/>

<%@ include file="/netmarkets/jsp/changeNotice/create.jsp"%>
