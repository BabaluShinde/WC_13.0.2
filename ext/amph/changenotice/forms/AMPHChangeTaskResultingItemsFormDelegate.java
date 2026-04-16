package ext.amph.changenotice.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.change2.forms.delegates.ChangeTaskResultingItemsFormDelegate;
import wt.change2.AffectedActivityData;
import wt.change2.ChangeHelper2;
import wt.change2.ChangeRecord2;
import wt.change2.Changeable2;
import wt.change2.WTChangeActivity2;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;

public class AMPHChangeTaskResultingItemsFormDelegate extends ChangeTaskResultingItemsFormDelegate {

	@Override
	public FormResult postProcess(NmCommandBean cmdBean, List<ObjectBean> objectBeans) throws WTException {

		FormResult result = super.postProcess(cmdBean, objectBeans);

		WTChangeActivity2 changeTask = null;

		// ------------------------------------------------
		// Identify Change Task
		// ------------------------------------------------
		for (ObjectBean ob : objectBeans) {
			if (ob.getObject() instanceof WTChangeActivity2) {
				changeTask = (WTChangeActivity2) ob.getObject();
				System.out.println("DEBUG: ChangeTask -> " + changeTask.getNumber());
				break;
			}
		}

		if (changeTask == null) {
			return result;
		}

		// ------------------------------------------------
		// Get PERSISTED affected objects
		// ------------------------------------------------
		List<Changeable2> affectedList = new ArrayList<>();

		QueryResult qr = ChangeHelper2.service.getChangeablesBefore(changeTask);

		while (qr.hasMoreElements()) {
			Object obj = qr.nextElement();
			if (obj instanceof Changeable2) {
				affectedList.add((Changeable2) obj);
			}
		}

		// ------------------------------------------------
		// Process affected objects
		// ------------------------------------------------
		for (Changeable2 changeable : affectedList) {

			// already in Resulting → skip everything
			if (isAlreadyResulting(changeTask, changeable)) {
				System.out.println("DEBUG: Already in Resulting → skip");
				continue;
			}

			LifeCycleManaged lcm = (LifeCycleManaged) changeable;
			State state = lcm.getLifeCycleState();

			System.out.println("DEBUG: Processing " + changeable + " state=" + state);

			// =============================================
			// CASE 1: INWORK → Move SAME object
			// =============================================
			if (State.INWORK.equals(state)) {

				Vector<Object> vec = new Vector<>();
				vec.add(changeable);

				vec = ChangeHelper2.service.storeAssociations(ChangeRecord2.class, changeTask, vec);

				ChangeHelper2.service.saveChangeRecord(vec);

				ChangeHelper2.service.unattachChangeable(changeable, changeTask, AffectedActivityData.class,
						AffectedActivityData.CHANGE_ACTIVITY2_ROLE);

				System.out.println("DEBUG: INWORK moved to Resulting");
			}

			// =============================================
			// CASE 2: RELEASED → Revise ONCE
			// =============================================
			else if (State.RELEASED.equals(state)) {

				Versioned versioned = (Versioned) changeable;

				try {
					Versioned revised = (Versioned) VersionControlHelper.service.newVersion(versioned);

					revised = (Versioned) PersistenceHelper.manager.save(revised);

					Vector<Object> vec = new Vector<>();
					vec.add(revised);

					vec = ChangeHelper2.service.storeAssociations(ChangeRecord2.class, changeTask, vec);

					ChangeHelper2.service.saveChangeRecord(vec);

					System.out.println("DEBUG: RELEASED revised and added");

				} catch (WTPropertyVetoException e) {
					throw new WTException(e);
				}
			}
		}

		return result;
	}

	// ------------------------------------------------
	// Utility: check resulting by MASTER
	// ------------------------------------------------
	private boolean isAlreadyResulting(WTChangeActivity2 task, Changeable2 obj) throws WTException {

		QueryResult qr = ChangeHelper2.service.getChangeablesAfter(task);

		while (qr.hasMoreElements()) {
			Object existing = qr.nextElement();

			if (existing instanceof Versioned && obj instanceof Versioned) {

				Versioned ev = (Versioned) existing;
				Versioned ov = (Versioned) obj;

				if (ev.getMaster().equals(ov.getMaster())) {
					return true;
				}
			}
		}
		return false;
	}
}
