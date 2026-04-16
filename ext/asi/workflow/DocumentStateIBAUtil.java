package ext.asi.workflow;

import java.rmi.RemoteException;
import java.sql.Timestamp;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.fc.Persistable;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTCollection;
import wt.iba.definition.litedefinition.*;
import wt.iba.definition.service.StandardIBADefinitionService;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.IBAHolder;
import wt.iba.value.litevalue.*;
import wt.iba.value.service.IBAValueHelper;
import wt.iba.value.service.StandardIBAValueService;
import wt.lifecycle.LifeCycleManaged;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.WorkInProgressServerHelper;

public class DocumentStateIBAUtil {

	public static void storeLifecycleStateInIBA(WTObject obj)
	        throws RemoteException, WTException, WTPropertyVetoException {

	    if (obj instanceof WTDocument) {
	        WTDocument doc = (WTDocument) obj;
	        String lifecycleState = ((LifeCycleManaged) doc).getLifeCycleState().toString();
	        System.out.println("Lifecycle State to store in IBA: " + lifecycleState);
	        updateIBAValue(doc, "REDPACKET_TYPE", lifecycleState);
	    } else {
	        System.out.println("Not a WTDocument. Skipping IBA update.");
	    }
	}


    public static Persistable updateIBAValue(IBAHolder ibaHolder, String ibaName, Object ibaValue)
            throws RemoteException, WTException, WTPropertyVetoException {

        ibaHolder = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, null, null, null);
        StandardIBADefinitionService defService = new StandardIBADefinitionService();
        DefaultAttributeContainer attributeContainer = (DefaultAttributeContainer) ibaHolder.getAttributeContainer();
        AttributeDefDefaultView attributeDefinition = defService.getAttributeDefDefaultViewByPath(ibaName);

        AbstractContextualValueDefaultView attrValue = null;
        AbstractValueView[] abstractValueView = attributeContainer.getAttributeValues(attributeDefinition);

        if (abstractValueView.length == 0) {
            if (ibaValue != null) {
                if (attributeDefinition instanceof TimestampDefView && ibaValue instanceof Timestamp) {
                    attrValue = new TimestampValueDefaultView((TimestampDefView) attributeDefinition,
                            (Timestamp) ibaValue);
                } else if (attributeDefinition instanceof StringDefView) {
                    attrValue = new StringValueDefaultView((StringDefView) attributeDefinition,
                            ibaValue.toString());
                } else if (attributeDefinition instanceof FloatDefView) {
                    attrValue = new FloatValueDefaultView((FloatDefView) attributeDefinition,
                            (Double) ibaValue, 5);
                } else if (attributeDefinition instanceof IntegerDefView) {
                    attrValue = new IntegerValueDefaultView((IntegerDefView) attributeDefinition,
                            (Long) ibaValue);
                }
                attributeContainer.addAttributeValue(attrValue);
            } else {
                return (Persistable) ibaHolder;
            }
        } else {
            AbstractValueView avv = abstractValueView[0];
            if (ibaValue == null) {
                attributeContainer.deleteAttributeValue(avv);
            } else {
                if (avv instanceof TimestampValueDefaultView) {
                    ((TimestampValueDefaultView) avv).setValue((Timestamp) ibaValue);
                } else if (avv instanceof StringValueDefaultView) {
                    ((StringValueDefaultView) avv).setValue(ibaValue.toString());
                } else if (avv instanceof FloatValueDefaultView) {
                    ((FloatValueDefaultView) avv).setValue((Double) ibaValue);
                } else if (avv instanceof IntegerValueDefaultView) {
                    ((IntegerValueDefaultView) avv).setValue((Long) ibaValue);
                }
                attributeContainer.updateAttributeValue(avv);
            }
        }

        ibaHolder.setAttributeContainer(attributeContainer);
        StandardIBAValueService.theIBAValueDBService.updateAttributeContainer(ibaHolder, null, null, null);

        WTCollection byPassIterationModifierSet = new WTHashSet();
        byPassIterationModifierSet.add(ibaHolder);
        WorkInProgressServerHelper.putInTxMapForValidateModifiable(byPassIterationModifierSet);

        return PersistenceHelper.manager.save((Persistable) ibaHolder);
    }
}
