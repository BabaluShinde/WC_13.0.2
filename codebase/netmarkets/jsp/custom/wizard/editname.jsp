<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="comp"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/core" prefix="wc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>

<wctags:itemPicker id="customized_item_picker" objectType="wt.doc.WTDocument" componentId="customizedItemPickerForSoftPart"/>



<%@ include file="/netmarkets/jsp/util/end.jspf"%>
