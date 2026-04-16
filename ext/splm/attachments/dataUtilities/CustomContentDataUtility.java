package ext.splm.attachments.dataUtilities;

import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Map;

import wt.type.Typed;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.rendering.guicomponents.CheckBox;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.impl.TypeIdentifierUtilityHelper;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.attachments.dataUtilities.ContentDataUtility;
import wt.change2.Changeable2;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTReference;
import wt.part.WTPart;
import wt.type.TypedUtilityService;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;

public class CustomContentDataUtility extends ContentDataUtility {

	@Override
	public Object getDataValue(String component_id, Object obj, ModelContext modelContext) throws WTException {

		if (obj == null || component_id == null || modelContext == null) {
			return super.getDataValue(component_id, obj, modelContext);
		}

		NmCommandBean nmCommandBean = modelContext.getNmCommandBean();

		Map<String, String[]> paramMap = nmCommandBean.getRequest().getParameterMap();
		
		ReferenceFactory rf = new ReferenceFactory();

		// Handle initialRows
		String[] initialRows = paramMap.get("initialRows_issues_affectedData_table");
		if (initialRows != null) {
			for (String row : initialRows) {
				for (String oidStr : row.split("#")) { // split in case multiple too
					try {
						WTReference ref = rf.getReference(oidStr.trim());
						Persistable obj1 = (Persistable) ref.getObject();

						// Get the TypeIdentifier from the object
						TypeIdentifier typeId = TypeIdentifierUtilityHelper.service.getTypeIdentifier((Typed) obj1);

						// Internal name
	                String internalName = typeId.toString();

						// Localized display name
						TypedUtilityService service = TypedUtilityServiceHelper.service;
						String displayName = service.getLocalizedTypeName(typeId, Locale.US);
//
                        System.out.println("SoftType Internal = " + internalName);
//		                System.out.println("SoftType Display  = " + displayName);

						System.out.println("Initial Affected Obj: " + displayName + " | Number = "
								+ (obj1 instanceof wt.doc.WTDocument ? ((wt.doc.WTDocument) obj1).getNumber() : "N/A"));
					} catch (Exception e) {
						System.out.println("Failed to resolve initial OID: " + oidStr + " | " + e.getMessage());
					}
				}
			}
		}

		// Handle addRows
		String[] addRows = paramMap.get("addRows_issues_affectedData_table");
		if (addRows != null) {
			for (String row : addRows) {
				for (String oidStr : row.split("#")) {
					try {
						WTReference ref = rf.getReference(oidStr.trim());
						Persistable obj2 = (Persistable) ref.getObject();

						// Get the TypeIdentifier from the object
						TypeIdentifier typeId = TypeIdentifierUtilityHelper.service.getTypeIdentifier((Typed) obj2);

						// Internal name
//			                String internalName = typeId.toString();

						// Localized display name
						TypedUtilityService service = TypedUtilityServiceHelper.service;
						String displayName = service.getLocalizedTypeName(typeId, Locale.US);
//
//			                System.out.println("SoftType Internal = " + internalName);
//			                System.out.println("SoftType Display  = " + displayName);
//			           
//						
						String objNumber = "N/A";

						// Handle WTDocument and its subtypes
						if (obj2 instanceof wt.doc.WTDocument) {
							objNumber = ((wt.doc.WTDocument) obj2).getNumber();
						}
						// Handle WTPart and its subtypes
						else if (obj2 instanceof wt.part.WTPart) {
							objNumber = ((wt.part.WTPart) obj2).getNumber();
						}
						// Handle EPMDocument and its subtypes
						else if (obj2 instanceof wt.epm.EPMDocument) {
							objNumber = ((wt.epm.EPMDocument) obj2).getNumber();
						}
						// Handle Change Notice (ECN)
						else if (obj2 instanceof wt.change2.WTChangeOrder2) {
							objNumber = ((wt.change2.WTChangeOrder2) obj2).getNumber();
						}
						// Handle Change Activity
						else if (obj2 instanceof wt.change2.WTChangeActivity2) {
							objNumber = ((wt.change2.WTChangeActivity2) obj2).getNumber();
						}
						// Handle any other Persistable with getNumber() via reflection
						else {
							try {
								java.lang.reflect.Method m = obj2.getClass().getMethod("getNumber");
								Object numberObj = m.invoke(obj2);
								if (numberObj != null) {
									objNumber = numberObj.toString();
								}
							} catch (NoSuchMethodException nsme) {
								// Not all types have getNumber(), ignore
							}
						}

						System.out.println("Affected Obj: " + displayName + " | Number = " + objNumber);

					} catch (Exception e) {
						System.out.println("Failed to resolve OID: " + oidStr + " | " + e.getMessage());
					}

				}
			}
		}

		// get the pbo
		Persistable primaryObj = null;
		if (modelContext.getNmCommandBean() != null && modelContext.getNmCommandBean().getPrimaryOid() != null) {

			primaryObj = (Persistable) modelContext.getNmCommandBean().getActionOid().getRefObject();
			if (primaryObj instanceof WTChangeOrder2) {
				WTChangeOrder2 co = (WTChangeOrder2) primaryObj;

				// Get Change Activities under this ECN
				// Get Affected Objects for each Change Activity
				QueryResult affected = wt.change2.ChangeHelper2.service.getChangeablesBefore(co);
				while (affected.hasMoreElements()) {
					Changeable2 changeable = (Changeable2) affected.nextElement();

					String objNumber = "N/A";
					String softTypeDisplay = "Unknown Type";

					try {
						if (changeable instanceof Typed) {
							TypeIdentifier typeId = TypeIdentifierUtilityHelper.service
									.getTypeIdentifier((Typed) changeable);
							TypedUtilityService typeService = TypedUtilityServiceHelper.service;
							softTypeDisplay = typeService.getLocalizedTypeName(typeId, Locale.US);
						}
					} catch (Exception e) {
						System.out.println("Failed to resolve soft type: " + e.getMessage());
					}

					if (changeable instanceof WTDocument) {
						WTDocument doc = (WTDocument) changeable;
						objNumber = doc.getNumber();
					} else if (changeable instanceof WTPart) {
						WTPart part = (WTPart) changeable;
						objNumber = part.getNumber();
					} else if (changeable instanceof EPMDocument) {
						EPMDocument epm = (EPMDocument) changeable;
						objNumber = epm.getNumber();
					} else if (changeable instanceof WTChangeOrder2) {
						WTChangeOrder2 ecn = (WTChangeOrder2) changeable;
						objNumber = ecn.getNumber();
					} else {
						// Fallback: try reflection
						try {
							java.lang.reflect.Method m = changeable.getClass().getMethod("getNumber");
							Object numberObj = m.invoke(changeable);
							if (numberObj != null) {
								objNumber = numberObj.toString();
							}
						} catch (Exception ignore) {
						}
					}

					System.out.println("Affected Obj: " + softTypeDisplay + " | Number = " + objNumber);
				}

			}

			if ("contentDistributable".equalsIgnoreCase(component_id)
					&& (ComponentMode.CREATE.equals(modelContext.getDescriptorMode())
							|| ComponentMode.EDIT.equals(modelContext.getDescriptorMode()))) {

				// Get the OOTB component
				Object ootbComponent = super.getDataValue(component_id, obj, modelContext);

				if (ootbComponent instanceof CheckBox) {
					CheckBox cb = (CheckBox) ootbComponent;

					cb.addJsAction("onclick", "return validateDistributableExcelOnly(this);");

					return cb;
				}
			}
		}
		return super.getDataValue(component_id, obj, modelContext);
	}
}
