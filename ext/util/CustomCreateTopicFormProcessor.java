package ext.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.forumTopic.processors.CreateTopicFormProcessor;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import wt.fc.PersistenceHelper;
import wt.fc.collections.WTArrayList;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.util.WTException;
import wt.workflow.collaboration.CollaborationHelper;
import wt.workflow.forum.DiscussionRecipientUserLink;
import wt.workflow.forum.DiscussionTopic;

public class CustomCreateTopicFormProcessor extends CreateTopicFormProcessor {
	/*
	 * Overrides the default behavior after a discussion topic is created. Adds the
	 * selected participants as subscribers to the topic.
	 */
	public FormResult doOperation(final NmCommandBean cmdBean, final List<ObjectBean> objectBeans) throws WTException {

		// Call the super class's method to handle default topic creation logic
		final FormResult result = super.doOperation(cmdBean, objectBeans);

		// List to hold selected participants (WTUsers)
		List<WTPrincipal> ParticipantList = new ArrayList<WTPrincipal>();

		// Loop through all the created discussion topic objects
		for (ObjectBean obj : objectBeans) {
			System.out.println(obj.toString());
			Object disobj = obj.getObject();
			System.out.println("disobj" + disobj);

			// Cast to DiscussionTopic
			DiscussionTopic topic = (DiscussionTopic) disobj;
			System.out.println("Got DiscussionTopic: " + topic.getName());

			// Get the list of added participants from the command bean
			List participantslist = cmdBean.getAddedItemsByName("netmarkets.topic.participants");
			System.out.println("Adding participants=" + participantslist);
			System.out.println(participantslist.size());

			// Iterate over each participant and extract WTUser reference
			for (Object participant : participantslist) {
				NmOid objref = (NmOid) participant;
				WTUser user = (WTUser) objref.getRefObject();
				System.out.println("Participant" + user.getName());
				ParticipantList.add(user); // Add to the list of principals
			}
			// Create subscriptions for the topic with selected participants
			createSubscription(ParticipantList, topic);

			// Add feedback message
			List<String> participantNames = new ArrayList<>();
			for (WTPrincipal user : ParticipantList) {
				participantNames.add(user.getName()); // or getFullName() if available
			}

			String feedbackText = "The following participants have been successfully subscribed to the topic '"
					+ topic.getName() + "': " + String.join(", ", participantNames);

			FeedbackMessage messageObj = new FeedbackMessage(FeedbackType.SUCCESS, Locale.US, "Message", // Generic key

					new ArrayList<String>(), new String[] { feedbackText });

			result.addFeedbackMessage(messageObj);
			result.setStatus(FormProcessingStatus.SUCCESS);

		}

		return result;

	}

	// Helper method to create subscriptions and DiscussionRecipientUserLinks for
	// participants.
	private static void createSubscription(final List<WTPrincipal> principals, final DiscussionTopic topic)
			throws WTException {

		final Vector<WTPrincipal> subscribers = new Vector<WTPrincipal>();// NOPMD by TE337032
		final Iterator<WTPrincipal> itr = principals.iterator();
		final WTArrayList wtArrayList = new WTArrayList();

		// Create a DiscussionRecipientUserLink for each participant
		while (itr.hasNext()) {
			final WTPrincipal principal = itr.next();
			subscribers.add(principal);
			wtArrayList.add(DiscussionRecipientUserLink.newDiscussionRecipientUserLink(topic, principal));
		}
		/*
		 * Persist all the created DiscussionRecipientUserLink objects to the database.
		 * This ensures that each participant is officially linked to the discussion
		 * topic as a recipient.
		 */
		PersistenceHelper.manager.save(wtArrayList);

		// Subscribe participants to the topic
		CollaborationHelper.service.subscribe(topic, subscribers);

	}

}
