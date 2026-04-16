package ext.amph.workflow;

import java.util.HashMap;

import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.util.WTException;
import wt.workflow.engine.WfActivity;
import wt.workflow.work.WorkItem;

import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.work.StandardNmWorkItemService;

public class AMPHNmWorkItemService extends StandardNmWorkItemService {

	public static AMPHNmWorkItemService newAMPHNmWorkItemService() throws WTException {
		AMPHNmWorkItemService service = new AMPHNmWorkItemService();
		service.initialize();
		return service;
	}

	@Override
	public void complete(NmCommandBean commandBean, HashMap map) throws WTException {

		NmOid nmoid = commandBean.getPageOid();
		WorkItem workitem = getWorkItem(nmoid);

		ObjectReference sourceRef = workitem.getSource();

		if (sourceRef != null) {

			Persistable sourceObj = sourceRef.getObject();

			if (sourceObj instanceof WfActivity) {

				WfActivity activity = (WfActivity) sourceObj;

				System.out.println("Activity Name: " + activity.getName());

				// Activity check FIRST
				if ("Review Task".equals(activity.getName())) {

					// Only then validate
					validateComments(commandBean);
				}
			}
		}

		// Always call super at end
		super.complete(commandBean, map);
	}

	private void validateComments(NmCommandBean commandBean) throws WTException {

		String comments = null;

		java.util.Enumeration<String> paramNames = commandBean.getRequest().getParameterNames();

		while (paramNames.hasMoreElements()) {
			String param = paramNames.nextElement();

			if (param.contains("___comments___textarea")) {
				comments = commandBean.getRequest().getParameter(param);
				System.out.println("Found comment param: " + param);
				break;
			}
		}

		System.out.println("Comments Value: " + comments);

		if (comments == null || comments.trim().isEmpty()) {
			throw new WTException("Comments are mandatory for this task!!.");
		}
	}
}