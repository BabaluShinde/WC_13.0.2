package ext.dtx;

import com.infoengine.object.factory.Element;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmURLFactoryBean;
import com.ptc.netmarkets.util.misc.NetmarketURL;
import com.ptc.windchill.enterprise.workflow.WorkflowCommands;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.httpgw.URLFactory;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.workflow.engine.TheWorkItemTheWfVotingEventAudit;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;
import wt.workflow.engine.WfVotingEventAudit;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WfAssignmentState;
import wt.workflow.work.WorkItem;

public class ReportHelper {
   public static WTChangeRequest2 getECR(String objectID) throws WTException {
      ReferenceFactory rf = new ReferenceFactory();
      WTReference ref = rf.getReference(objectID);
      WTChangeRequest2 wtChangeRequest2 = (WTChangeRequest2)ref.getObject();
      return wtChangeRequest2;
   }

   public static WTChangeOrder2 getECN(String objectID) throws WTException {
      ReferenceFactory rf = new ReferenceFactory();
      WTReference ref = rf.getReference(objectID);
      WTChangeOrder2 wtChangeOrder2 = (WTChangeOrder2)ref.getObject();
      return wtChangeOrder2;
   }

   public static Set getRoutingHistory(Persistable wtChangeRequest2) {
      Set routingHistory = new HashSet();
      System.out.println("WithoutRepeter");

      try {
         Enumeration processes = WfEngineHelper.service.getAssociatedProcesses(wtChangeRequest2, (WfState)null);

         label83:
         while(processes.hasMoreElements()) {
            WfProcess process = (WfProcess)processes.nextElement();
            NmOid nmOid = new NmOid(process);
            QueryResult status = WorkflowCommands.getRouteStatus(nmOid);

            while(true) {
               TaskHistory history;
               String activityName;
               String assigne;
               String role;
               String comments;
               String vote;
               String actStatus;
               String completed;
               String start;
               String completedBy;
               String duration;
               while(true) {
                  if (!status.hasMoreElements()) {
                     continue label83;
                  }

                  history = new TaskHistory();
                  Object obj = status.nextElement();
                  activityName = "N/A";
                  assigne = "N/A";
                  role = "N/A";
                  comments = "N/A";
                  vote = "N/A";
                  actStatus = "N/A";
                  completed = "N/A";
                  start = "N/A";
                  String signature = "N/A";
                  completedBy = "N/A";
                  duration = "N/A";
                  if (obj.getClass().isAssignableFrom(WorkItem.class)) {
                     WorkItem workitem = (WorkItem)obj;
                     activityName = ((WfAssignedActivity)workitem.getSource().getObject()).getName();
                     assigne = workitem.getOwnership().getOwner().getFullName();
                     role = workitem.getRole().getDisplay();
                     if (workitem.getStatus().toString().equalsIgnoreCase(WfAssignmentState.COMPLETED.toString())) {
                        actStatus = (new URLFactory()).getHREF("/netmarkets/images/checked.gif");
                     } else {
                        actStatus = (new URLFactory()).getHREF("/netmarkets/images/unchecked.gif");
                     }

                     WfVotingEventAudit wfVotingEventAudit = getAuditEvent(workitem);
                     if (wfVotingEventAudit != null) {
                        comments = wfVotingEventAudit.getUserComment();
                        vote = wfVotingEventAudit.getEventList().toString();
                        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyy");
                        completed = wfVotingEventAudit.getTimestamp() == null ? "" : dateFormat.format(wfVotingEventAudit.getTimestamp());
                        start = wfVotingEventAudit.getCreateTimestamp() == null ? "" : dateFormat.format(workitem.getCreateTimestamp());
                        duration = (wfVotingEventAudit.getTimestamp().getTime() - workitem.getCreateTimestamp().getTime()) / 86400000L + "";
                        completedBy = wfVotingEventAudit.getUserRef().getFullName();
                     }
                     break;
                  }

                  if (!obj.getClass().isAssignableFrom(Element.class)) {
                     if (obj.getClass().isAssignableFrom(WfVotingEventAudit.class)) {
                        WfVotingEventAudit wfVotingEventAudit = (WfVotingEventAudit)obj;
                        activityName = wfVotingEventAudit.getActivityName();
                        assigne = wfVotingEventAudit.getAssigneeRef().getFullName();
                        role = wfVotingEventAudit.getRole() == null ? "" : wfVotingEventAudit.getRole().getDisplay();
                        comments = wfVotingEventAudit.getUserComment();
                        vote = wfVotingEventAudit.getEventList().toString();
                        actStatus = (new URLFactory()).getHREF("/netmarkets/images/checked.gif");
                        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyy");
                        completed = wfVotingEventAudit.getTimestamp() == null ? "N/A" : dateFormat.format(wfVotingEventAudit.getTimestamp());
                        start = wfVotingEventAudit.getCreateTimestamp() == null ? "N/A" : dateFormat.format(wfVotingEventAudit.getCreateTimestamp());
                        completedBy = wfVotingEventAudit.getUserRef().getFullName();
                        WorkItem workitem = wfVotingEventAudit.getWorkItem();
                        duration = (wfVotingEventAudit.getTimestamp().getTime() - workitem.getCreateTimestamp().getTime()) / 86400000L + "";
                     }
                     break;
                  }
               }

               comments = comments.trim().length() == 0 ? "N/A" : comments;
               System.out.println("Comments-->" + comments);
               vote = vote.replace("[", "");
               vote = vote.replace("]", "");
               vote = vote.length() == 0 ? "N/A" : vote;
               history.setAssigne(assigne);
               history.setRole(role);
               history.setComments(comments);
               history.setVote(vote);
               history.setActivityName(activityName);
               history.setActStatus(actStatus);
               history.setCompleted(completed);
               history.setStart(start);
               history.setDuraton(duration);
               System.out.println("Completed by " + completedBy);
               routingHistory.add(history);
            }
         }
      } catch (WTException var22) {
         var22.printStackTrace();
      } catch (WTRuntimeException var23) {
         var23.printStackTrace();
      }

      return routingHistory;
   }

   private static WfVotingEventAudit getAuditEvent(WorkItem workItem) throws WTException {
      WfVotingEventAudit wfVotingEventAudit = null;

      for(QueryResult auditEvent = PersistenceHelper.manager.navigate(workItem, "theWfVotingEventAudit", TheWorkItemTheWfVotingEventAudit.class, true); auditEvent.hasMoreElements(); wfVotingEventAudit = (WfVotingEventAudit)auditEvent.nextElement()) {
      }

      return wfVotingEventAudit;
   }

   public static QueryResult getChangeRequest(WTChangeOrder2 wtChangeOrder2) throws ChangeException2, WTException {
      QueryResult requests = ChangeHelper2.service.getChangeRequest(wtChangeOrder2);
      return requests;
   }

   public static QueryResult getChangeIssues(WTChangeRequest2 wtChangeRequest2) throws ChangeException2, WTException {
      QueryResult reports = ChangeHelper2.service.getChangeIssues(wtChangeRequest2);
      return reports;
   }

   public static String getInfoURL(WTObject object) throws WTException {
      NmOid contentOid = new NmOid(object);
      NmURLFactoryBean urlFactoryBean = new NmURLFactoryBean();
      urlFactoryBean.setRequestURI(NetmarketURL.BASEURL);
      String URL = NetmarketURL.buildURL(urlFactoryBean, "object", "view", contentOid, (HashMap)null);
      return URL;
   }
}
