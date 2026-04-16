<!-- bcwti
 *
 * Copyright (c) 2006 Parametric Technology Corporation (PTC). All Rights
 * Reserved.
 *
 * This software is the confidential and proprietary information of PTC.
 * You shall not disclose such confidential information and shall use it
 * only in accordance with the terms of the license agreement.
 *
 * ecwti
 * -->

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib prefix="docmgnt" uri="http://www.ptc.com/windchill/taglib/docmgnt" %>

<docmgnt:validateNameJSTag template="${docmgnt:isPrimaryOidATemplate(commandBean)}"/>

<jca:wizard buttonList="renameObjectWizardButtons" helpSelectorKey="OverviewRename_help">
  <jca:wizardStep action="renameDocumentWizardStep" type="document"/>
</jca:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>