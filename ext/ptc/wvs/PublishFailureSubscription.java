
package ext.ptc.wvs;

import wt.fc.Persistable;
import wt.org.WTPrincipal;
import wt.org.WTGroup;
import wt.org.OrganizationServicesHelper;
import wt.session.SessionHelper;
import wt.identity.IdentityFactory;
import wt.util.WTProperties;
import wt.fc.collections.WTHashSet;
import wt.admin.AdministrativeDomainHelper;

import wt.notify.NotificationHelper;
import wt.notify.NotificationSubscription;
import wt.notify.NotifySubscriptionRecipient;
import wt.notify.Notifiable;
import wt.notify.CompositeNotificationSubscription;

import com.ptc.wvs.server.publish.PublishServiceEvent;

import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Date;
import java.sql.Timestamp;

public class PublishFailureSubscription {
	// this method is configured using the wvs.properties entry
	// publish.service.filterpublishmethod=ext.ptc.wvs.PublishFailureSubscription/filterPublishToAddSubscription
	// it will be called any time an object in published by WVS.
	//
	// to compile
	// windchill -cp <WT_HOME>\codebase;<WT_HOME>\srclib\tool\* shell
	// javac -d <WT_HOME>\codebase PublishFailureSubscription.java
 
	private static final String SUB_KEY = "WVS_PUB_SUB";
	private static final String EVENT_KEY = PublishServiceEvent
			.generateEventKey(PublishServiceEvent.PUBLISH_NOT_SUCCESSFUL);
	private static String JOB_MONITOR = "";

	static {
		try {
			// the URL of the job monitor may need changing depending on the version of
			// Windchill being used.
			// JOB_MONITOR = WTProperties.getServerCodebase().toString() + "ptc1/wvs/queueMonitorMain";
			JOB_MONITOR = WTProperties.getServerCodebase().toString();
			if (!JOB_MONITOR.endsWith("/")) {
				JOB_MONITOR += "/";
			}
			JOB_MONITOR += "ptc1/wvs/queueMonitorMain";
		} catch (Exception e) {
		}
	}

	public static Boolean filterPublishToAddSubscription(Persistable p, Boolean publishFromDB) {
		if ((p instanceof Notifiable)) {
			try {
				WTPrincipal principal = SessionHelper.manager.getPrincipal();

				// if the user already has a subscription then do not add a new one
				try {
					Collection existingSub = NotificationHelper.manager.getNotificationSubscriptions((Notifiable) p, principal, EVENT_KEY, SUB_KEY);
					if (existingSub != null && !existingSub.isEmpty()) {
						// delete any existing automatically added subscriptions for this user on this
						// object
						for (Iterator it = existingSub.iterator(); it.hasNext();) {
							CompositeNotificationSubscription s = (CompositeNotificationSubscription) it.next();
							NotificationSubscription ns = s.getNotificationSubscription();
							if (ns != null)
								NotificationHelper.manager.deleteSubscription(ns);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				// crate a new subscription
				NotificationSubscription subscriptionAttributes = NotificationSubscription
						.newNotificationSubscription();
				long time = (new Date()).getTime() + (long) (1000 * 60 * 60 * 48); // 48hrs from now
				subscriptionAttributes.setExpirationTime(new Timestamp(time));
				subscriptionAttributes.setFromUser(NotificationSubscription.SYSTEM_FROM_USER);
				subscriptionAttributes.setName("WVS Automated Publish Subscription");
				subscriptionAttributes
						.setMessage("Publish Job Failed for \"" + IdentityFactory.getDisplayIdentity(p).toString()
								+ "\" use the WVS Job Monitor to view the job details " + JOB_MONITOR);
				subscriptionAttributes
						.setSubject("WVS Publish Failed: " + IdentityFactory.getDisplayIdentity(p).toString());
				subscriptionAttributes.setSendImmediate(true);
				subscriptionAttributes.setMutable(false);
				subscriptionAttributes.setSubscriptionKey(SUB_KEY);

				WTHashSet subscribers = new WTHashSet(2);
				subscribers.add(NotifySubscriptionRecipient.newNotifySubscriptionRecipient(principal, NotifySubscriptionRecipient.TO_ADDRESS));
				WTGroup admin = OrganizationServicesHelper.manager.getGroup(AdministrativeDomainHelper.ADMIN_GROUP_NAME, AdministrativeDomainHelper.ADMIN_GROUP_SERVICE);
				if (admin != null)
					subscribers.add(NotifySubscriptionRecipient.newNotifySubscriptionRecipient(admin, NotifySubscriptionRecipient.CC_ADDRESS));

				WTHashSet targetObjects = new WTHashSet(1);
				targetObjects.add(p);

				HashMap eventKeysAndAttributeValueMaps = new HashMap();
				eventKeysAndAttributeValueMaps.put(EVENT_KEY, null);

				NotificationHelper.manager.createObjectSubscriptions(subscriptionAttributes, subscribers, targetObjects, eventKeysAndAttributeValueMaps, false/* not all versions */);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return Boolean.TRUE; // allow publish always
	}
}