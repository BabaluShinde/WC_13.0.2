<%@taglib tagdir="/WEB-INF/tags" prefix="wctags"%>
<%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<jsp:useBean id="pickerConfig" class="com.ptc.windchill.enterprise.change2.search.ChangeItemPickerConfig" scope="page">
   <jsp:setProperty name="pickerConfig" property="changeItemClass"  value="wt.change2.ChangeRequestIfc"/>
</jsp:useBean>

<wctags:itemPicker id="associatedChangeRequestsPicker" pickerConfig="${pickerConfig}"/>

<%@include file="/netmarkets/jsp/util/end.jspf" %>