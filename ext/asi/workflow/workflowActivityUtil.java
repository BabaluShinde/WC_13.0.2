package ext.asi.workflow;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Date;
import java.util.List;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.mail.EMailMessage;
import wt.org.OrganizationServicesHelper;
import wt.org.WTUser;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WfAssignment;
import wt.workflow.work.WorkItem;
import wt.workflow.work.WorkItemLink;

public class workflowActivityUtil {

	// --- Existing reusable methods (getRunningActivityByName,
	// getAssignmentsForActivity, etc.) ---

	public static WfAssignedActivity getRunningActivityByName(WfProcess wfProcess, String activityName)
			throws Exception {
		QueryResult qr = wfProcess.getContainerNodes();
		while (qr.hasMoreElements()) {
			Object obj = qr.nextElement();
			if (obj instanceof WfAssignedActivity) {
				WfAssignedActivity wfaa = (WfAssignedActivity) obj;
				if (activityName.equalsIgnoreCase(wfaa.getName()) && WfState.OPEN_RUNNING.equals(wfaa.getState())) {
					return wfaa;
				}
			}
		}
		return null;
	}

	public static List<WfAssignment> getAssignmentsForActivity(WfAssignedActivity wfaa) throws Exception {
		List<WfAssignment> assignmentsList = new ArrayList<>();
		QueryResult assignments = PersistenceHelper.manager.navigate(wfaa,
				wt.workflow.work.ActivityAssignmentLink.ASSIGNMENT_ROLE, wt.workflow.work.ActivityAssignmentLink.class);
		while (assignments.hasMoreElements()) {
			assignmentsList.add((WfAssignment) assignments.nextElement());
		}
		return assignmentsList;
	}

	public static List<WorkItem> getWorkItemsForAssignment(WfAssignment assignment) throws Exception {
		List<WorkItem> workItemsList = new ArrayList<>();
		Enumeration<?> workItems = PersistenceHelper.manager.navigate(assignment, WorkItemLink.WORK_ITEM_ROLE,
				WorkItemLink.class);
		while (workItems.hasMoreElements()) {
			workItemsList.add((WorkItem) workItems.nextElement());
		}
		return workItemsList;
	}

	public static WTUser getAssigneeForWorkItem(WorkItem wi) throws Exception {
		if (wi.isComplete())
			return null;
		String ownerName = wi.getOwnership().getOwner().getPrincipal().getName();
		return OrganizationServicesHelper.manager.getUser(ownerName);
	}

	public static List<WorkItem> getWorkItemsForActivity(WfAssignedActivity wfaa) throws Exception {
		List<WorkItem> allWorkItems = new ArrayList<>();
		if (wfaa == null) {
			return allWorkItems;
		}

		List<WfAssignment> assignments = getAssignmentsForActivity(wfaa);
		if (assignments == null || assignments.isEmpty()) {
			return allWorkItems;
		}

		for (WfAssignment assign : assignments) {
			List<WorkItem> wils = getWorkItemsForAssignment(assign);
			if (wils != null && !wils.isEmpty()) {
				allWorkItems.addAll(wils);
			}
		}
		return allWorkItems;
	}

	public static List<WTUser> getRunningActivityAssignees(WfProcess wfProcess, String activityName) {
		List<WTUser> assignees = new ArrayList<>();
		try {
			WfAssignedActivity wfaa = getRunningActivityByName(wfProcess, activityName);
			if (wfaa != null) {
				List<WfAssignment> assignments = getAssignmentsForActivity(wfaa);
				for (WfAssignment assign : assignments) {
					List<WorkItem> workItems = getWorkItemsForAssignment(assign);
					for (WorkItem wi : workItems) {
						WTUser user = getAssigneeForWorkItem(wi);
						if (user != null && !assignees.contains(user)) {
							assignees.add(user);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return assignees;
	}

	// --- New Methods for workflow expressions ---

	/**
	 * Get WorkItem URL for a given WorkItem
	 */
	public static String getWorkItemURL(WorkItem wi) {
		try {
			com.ptc.netmarkets.model.NmOid tgtOid = new com.ptc.netmarkets.model.NmOid(
					PersistenceHelper.getObjectIdentifier(wi));

			com.ptc.netmarkets.util.misc.NmAction nmAction = com.ptc.netmarkets.util.misc.NmActionServiceHelper.service
					.getAction(com.ptc.netmarkets.util.misc.NmAction.Type.OBJECT, "view");

			if (nmAction == null) {
				nmAction = com.ptc.netmarkets.util.misc.NmActionServiceHelper.service
						.getAction(com.ptc.netmarkets.util.misc.NmAction.Type.OBJECT, "open");
			}

			if (nmAction != null) {
				nmAction.setContextObject(tgtOid);
				nmAction.setIcon(null);
				return nmAction.getActionUrlExternal();
			}
		} catch (Throwable t) {
			System.err.println("Failed to get WorkItem URL: " + t.getMessage());
		}
		return null;
	}

	/**
	 * Get the due date of a WfAssignedActivity as formatted string
	 */
	public static String getActivityDueDate(WfAssignedActivity wfaa) {
		try {
			Date deadline = wfaa.getDeadline();
			if (deadline != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
//				sdf.setTimeZone(TimeZone.getTimeZone("America/New_York")); // EST/EDT
				return sdf.format(deadline);
			}
		} catch (Exception e) {
			System.err.println("Failed to get activity due date: " + e.getMessage());
		}
		return "Not Defined";
	}

	/**
	 * Get Primary Business Object (PBO) URL for a given WorkItem
	 */
	public static String getPBOURL(WorkItem wi) {
		try {
			if (wi.getPrimaryBusinessObject() != null) {
				WTObject pbo = (WTObject) wi.getPrimaryBusinessObject().getObject();
				com.ptc.netmarkets.model.NmOid pboOid = new com.ptc.netmarkets.model.NmOid(
						PersistenceHelper.getObjectIdentifier(pbo));

				com.ptc.netmarkets.util.misc.NmAction nmActionPBO = com.ptc.netmarkets.util.misc.NmActionServiceHelper.service
						.getAction(com.ptc.netmarkets.util.misc.NmAction.Type.OBJECT, "view");

				if (nmActionPBO == null) {
					nmActionPBO = com.ptc.netmarkets.util.misc.NmActionServiceHelper.service
							.getAction(com.ptc.netmarkets.util.misc.NmAction.Type.OBJECT, "open");
				}

				if (nmActionPBO != null) {
					nmActionPBO.setContextObject(pboOid);
					nmActionPBO.setIcon(null);
					return nmActionPBO.getActionUrlExternal();
				}
			}
		} catch (Throwable t) {
			System.err.println("Failed to get PBO URL: " + t.getMessage());
		}
		return null;
	}

	// --- sendEmailNotifications and sendSingleEmail
	public static void sendEmailNotifications(WfProcess wfProcess, String activityName) {
		try {
			WfAssignedActivity wfaa = getRunningActivityByName(wfProcess, activityName);
			if (wfaa == null) {
				System.out.println("No running activity found for: " + activityName);
				return;
			}

			for (WorkItem wi : getWorkItemsForActivity(wfaa)) {
				if (!wi.isComplete()) {
					WTUser user = getAssigneeForWorkItem(wi);
					if (user != null) {
						sendSingleEmail(wfProcess, wfaa, wi, user);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void sendSingleEmail(WfProcess wfProcess, WfAssignedActivity wfaa, WorkItem wi, WTUser user) {
		try {
			String workItemURL = getWorkItemURL(wi);
			String pboURL = getPBOURL(wi);
			String dueDate = getActivityDueDate(wfaa);
			String initiatorName = getProcessInitiator(wfProcess);

			try {
				InputStream inputStream = workflowActivityUtil.class
						.getResourceAsStream("OverdueNotificationTemplate.html");
				if (inputStream == null) {
					throw new FileNotFoundException(
							"Email template 'overdueNotification.html' not found in ext/asi/workflow/");
				}

				String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

				WTObject pbo = wi.getPrimaryBusinessObject() != null
						? (WTObject) wi.getPrimaryBusinessObject().getObject()
						: null;

				String pboRow = "";
				if (pboURL != null && pbo != null) {
					// Just replace the URL and display name, label comes from template
					// Template can have <tr><td><b>Red Packet:</b></td><td>{pboLink}</td></tr>
					pboRow = "<tr><td><b>Red Packet:</b></td><td><a href='" + pboURL + "' target='_blank'>"
							+ pbo.getDisplayIdentifier() + "</a></td></tr>";
				}

				// Replace all place holders in template
				body = body.replace("{workItemURL}", workItemURL).replace("{activityName}", wfaa.getName())
						.replace("{initiatorName}", initiatorName).replace("{dueDate}", dueDate)
						.replace("{assigneeName}", user.getFullName()).replace("{pboURL}", pboRow);

				String subject = "Overdue Notification: " + wfaa.getName();

				EMailMessage email = new EMailMessage();
				email.setSubject(subject);
				email.addPart(body, "text/html");
				email.addEmailAddress(new String[] { user.getEMail() });
				email.send(true);

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			System.err.println("Error sending email to: " + user.getName() + " — " + e.getMessage());
		}
	}

	private static String getProcessInitiator(WfProcess wfProcess) {
		try {
			if (wfProcess.getCreator() != null) {
				WTUser creator = (WTUser) wfProcess.getCreator().getPrincipal();
				return creator.getFullName();
			}
		} catch (Exception e) {
			System.err.println("Unable to fetch process initiator: " + e.getMessage());
		}
		return "Unknown";
	}
}