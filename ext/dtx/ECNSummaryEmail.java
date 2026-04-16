package ext.dtx;

import com.ptc.core.lwc.server.LWCNormalizedObject;
import com.ptc.core.meta.common.OperationIdentifier;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import wt.change2.AffectedActivityData;
import wt.change2.ChangeHelper2;
import wt.change2.ChangeRecord2;
import wt.change2.Changeable2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTReference;
import wt.method.RemoteAccess;
import wt.org.DirectoryContextProvider;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTProperties;

public class ECNSummaryEmail implements RemoteAccess {
	public static void main(String[] arg) throws WTException {
		ReferenceFactory rf = new ReferenceFactory();
		WTReference ref = rf
				.getReference("OR:wt.change2.WTChangeOrder2:11634484");
		WTChangeOrder2 wtChangeOrder2 = (WTChangeOrder2) ref.getObject();
	}

	public static void main() throws WTException, IOException {
		ReferenceFactory rf = new ReferenceFactory();
		WTReference ref = rf
				.getReference("OR:wt.change2.WTChangeOrder2:11634484");
		WTChangeOrder2 wtChangeOrder2 = (WTChangeOrder2) ref.getObject();
		sendEmail(wtChangeOrder2, "c:\\PTC\\ECo1.htm");
	}

	public static void sendMail(WTChangeOrder2 order, String filename)
			throws WTException, IOException {
		Properties prop = new Properties();
		FileInputStream input = null;
		String mailHost = WTProperties.getLocalProperties().getProperty(
				"wt.mail.mailhost");
		input = new FileInputStream(filename);
		prop.load(input);
		String to = "PostMaster@detex.com";
		String from = "PostMaster@detex.com";
		Properties props = new Properties();
		props.put("mail.smtp.auth", "false");
		props.put("mail.smtp.starttls.enable", "false");
		props.put("mail.smtp.host", mailHost);
		props.put("mail.smtp.port", "25");
		Session session = Session.getInstance(props);

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(RecipientType.TO, InternetAddress.parse(to));
			String rtn = "";
			rtn = getUsersEmailID("Change Notice Notification", rtn);
			message.setRecipients(RecipientType.CC, InternetAddress.parse(rtn));
			message.setSubject("ECN Summary -" + order.getNumber());
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText("Please see attached file.");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			messageBodyPart = new MimeBodyPart();
			File tempFile = new File(filename);
			if (tempFile.exists()) {
				System.out.println(filename);
				DataSource source = new FileDataSource(filename);
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName("ECO.html");
				multipart.addBodyPart(messageBodyPart);
			}

			message.setContent(multipart);
			Address[] allrec = message.getAllRecipients();
			Address[] arr$ = allrec;
			int len$ = allrec.length;

			for (int i$ = 0; i$ < len$; ++i$) {
				Address ad = arr$[i$];
				System.out.println(ad);
			}

			Transport.send(message);
			System.out.println("Sent message successfully....");
			tempFile.delete();
		} catch (MessagingException var20) {
			throw new RuntimeException(var20);
		}
	}

	public static void sendEmail(WTChangeOrder2 order, String FILE)
			throws WTException, IOException {
		Set routingHistory = ReportHelper.getRoutingHistory(order);
		
		Iterator iter = routingHistory.iterator();
		String toWrite = "<Table border=0 cellspacing=10 cellpadding=1> <tr><td> <table  width=100%><tr><td width=10%><img src='http://windchill-as.detex.com/Windchill/netmarkets/images/logo.png'/></td><th><h2 ><u> ECN Summary </u></h2><th></tr></table></td></tr>";
		addToLogs(toWrite, FILE);
		toWrite = "<tr><td> <br/><B> Engineering Change Notice (ECN) : </b> "
				+ order.getNumber() + " </td></tr>";
		addToLogs(toWrite, FILE);
		toWrite = "<tr> <td>\t<table width='100%' border=01 cellpadding=2 cellspacing=0>\t<tr class='thick'> <td > ECR  Number </td> <td> ECR Name</td> <td>Need Date</td> </tr>";
		addToLogs(toWrite, FILE);
		toWrite = "<tr> <td> " + order.getNumber() + "</td> <td><a href="
				+ ReportHelper.getInfoURL(order) + " target=_blank>"
				+ order.getName() + " </a></td> <td>" + order.getNeedDate()
				+ "   </td> </tr>";
		addToLogs(toWrite, FILE);
		toWrite = "</table></td></tr><tr> <td> <table width='100%' border=1 cellpadding=2  cellspacing=0><tr class='thick'><td> Description </td></tr> <tr><td> "
				+ order.getDescription() + " </td></tr></table>";
		addToLogs(toWrite, FILE);
		toWrite = "</td> </tr><tr><td width='100%'><table width='100%' border=1 cellpadding=2  cellspacing=0><tr  class='thick' > <td> Created On </td><td> Created By </td><td> Lifecycle State  </td> <td> Resolution Date    </td>  </tr>";
		addToLogs(toWrite, FILE);
		toWrite = "<tr> <td> "
				+ order.getCreateTimestamp()
				+ " </td> <td> "
				+ order.getCreatorFullName()
				+ "</td><td>  "
				+ order.getState().getState().getDisplay()
				+ "  </td> <td>  "
				+ (order.getResolutionDate() == null ? "-/-" : order
						.getResolutionDate()) + "  </td>  </tr></table>";
		addToLogs(toWrite, FILE);
		toWrite = "</td></tr><tr><td> <b>Implementation Plan </b>  <table width='100%' border=1 cellpadding=2  cellspacing=0> <tr class='thick'> <td> Number </td> <td> Name </td> <td> State </td> <td> Need Date </td> </tr>";
		addToLogs(toWrite, FILE);
		QueryResult changeActivities1 = ChangeHelper2.service
				.getChangeActivities(order);

		while (changeActivities1.hasMoreElements()) {
			WTChangeActivity2 wtChangeActivity2 = (WTChangeActivity2) changeActivities1
					.nextElement();
			addToLogs("<tr>", FILE);
			addToLogs("<td>" + wtChangeActivity2.getNumber() + "</td>", FILE);
			addToLogs("<td>" + wtChangeActivity2.getName() + "</td>", FILE);
			addToLogs("<td>"
					+ wtChangeActivity2.getState().getState().getDisplay()
					+ "</td>", FILE);
			addToLogs("<td>" + wtChangeActivity2.getNeedDate() + "</td>", FILE);
			addToLogs("</tr>", FILE);
		}

		toWrite = "</table>";
		addToLogs(toWrite, FILE);
		toWrite = "<tr><td><br/><B>Change Notice's Task History:</b> <table border=1 cellspacing=0 cellpadding=5><tr class='thick'><th> </th><th> Activity Name</th><th> Assignee</th><th> Vote </th><th> Comments </th><th> Start </th><th> End </th><th> Duration (Days)  </th></tr>";
		addToLogs(toWrite, FILE);

		while (iter.hasNext()) {
			TaskHistory history = (TaskHistory) iter.next();
			addToLogs("<tr>", FILE);
			addToLogs("<td> <img src='" + history.getActStatus() + "'/></td>",
					FILE);
			addToLogs("<td>" + history.getActivityName() + "</td>", FILE);
			addToLogs("<td>" + history.getAssigne() + "</td>", FILE);
			addToLogs("<td>" + history.getVote() + "</td>", FILE);
			addToLogs("<td>" + history.getComments() + "</td>", FILE);
			addToLogs("<td>" + history.getCompleted() + "</td>", FILE);
			addToLogs("<td>" + history.getStart() + "</td>", FILE);
			addToLogs("<td>" + history.getduration() + "</td>", FILE);
			addToLogs("</tr>", FILE);
		}

		QueryResult changeActivities = ChangeHelper2.service
				.getChangeActivities(order);

		WTChangeActivity2 wtChangeActivity2;
		while (changeActivities.hasMoreElements()) {
			wtChangeActivity2 = (WTChangeActivity2) changeActivities
					.nextElement();
			routingHistory = ReportHelper.getRoutingHistory(wtChangeActivity2);
			

			iter = routingHistory.iterator();

			while (iter.hasNext()) {
				TaskHistory history = (TaskHistory) iter.next();
				addToLogs("<tr>", FILE);
				addToLogs("<td> <img src='" + history.getActStatus()
						+ "'/></td>", FILE);
				addToLogs("<td>" + history.getActivityName() + "</td>", FILE);
				addToLogs("<td>" + history.getAssigne() + "</td>", FILE);
				addToLogs("<td>" + history.getVote() + "</td>", FILE);
				addToLogs("<td>" + history.getComments() + "</td>", FILE);
				addToLogs("<td>" + history.getCompleted() + "</td>", FILE);
				addToLogs("<td>" + history.getStart() + "</td>", FILE);
				addToLogs("<td>" + history.getduration() + "</td>", FILE);
				addToLogs("</tr>", FILE);
			}
		}

		toWrite = "</table><br/><br/><b>Affected Objects:</b><table border=1 cellspacing=0><tr class='thick'><th> Name </th><th>Number</th><th> Version </th><th>State </th></tr>";
		addToLogs(toWrite, FILE);
		changeActivities = ChangeHelper2.service.getChangeActivities(order);

		Changeable2 persistable;
		WTPart part;
		QueryResult resulting;
		EPMDocument cad;
		WTDocument doc;
		while (changeActivities.hasMoreElements()) {
			wtChangeActivity2 = (WTChangeActivity2) changeActivities
					.nextElement();
			resulting = ChangeHelper2.service.getChangeablesBefore(
					wtChangeActivity2, false);

			while (resulting.hasMoreElements()) {
				AffectedActivityData aaData = (AffectedActivityData) resulting
						.nextElement();
				persistable = aaData.getChangeable2();
				LWCNormalizedObject obj1;
				if (persistable.getClass().isAssignableFrom(WTPart.class)) {
					part = (WTPart) persistable;
					obj1 = new LWCNormalizedObject(aaData, (String) null,
							(Locale) null, (OperationIdentifier) null);
					obj1.load(new String[]{"theOnOrderDisposition",
							"theFinishedDisposition", "theInventoryDisposition"});
					addToLogs("<tr>", FILE);
					addToLogs(
							"<td> <img src='http://windchill-as.detex.com/Windchill/wtcore/images/part.gif'/>"
									+ part.getName() + "</td>", FILE);
					addToLogs("<td>" + part.getNumber() + "</td>", FILE);
					addToLogs("<td>" + part.getVersionIdentifier().getValue()
							+ "." + part.getIterationIdentifier().getValue()
							+ "</td>", FILE);
					addToLogs("<td>" + part.getState().getState().getDisplay()
							+ "</td>", FILE);
					addToLogs("</tr>", FILE);
				} else if (persistable.getClass().isAssignableFrom(
						EPMDocument.class)) {
					cad = (EPMDocument) persistable;
					obj1 = new LWCNormalizedObject(aaData, (String) null,
							(Locale) null, (OperationIdentifier) null);
					obj1.load(new String[]{"theOnOrderDisposition",
							"theFinishedDisposition", "theInventoryDisposition"});
					addToLogs("<tr>", FILE);
					addToLogs(
							"<td>  <img src='http://windchill-as.detex.com/Windchill/wt/clients/images/proe/prt_with_generic.gif'/> "
									+ cad.getName() + "</td>", FILE);
					addToLogs("<td>" + cad.getNumber() + "</td>", FILE);
					addToLogs("<td>" + cad.getVersionIdentifier().getValue()
							+ "." + cad.getIterationIdentifier().getValue()
							+ "</td>", FILE);
					addToLogs("<td>" + cad.getState().getState().getDisplay()
							+ "</td>", FILE);
					addToLogs("</tr>", FILE);
				} else if (persistable.getClass().isAssignableFrom(
						WTDocument.class)) {
					doc = (WTDocument) persistable;
					obj1 = new LWCNormalizedObject(aaData, (String) null,
							(Locale) null, (OperationIdentifier) null);
					obj1.load(new String[]{"theOnOrderDisposition",
							"theFinishedDisposition", "theInventoryDisposition"});
					addToLogs("<tr>", FILE);
					addToLogs(
							"<td> <img src='http://windchill-as.detex.com/Windchill/netmarkets/images/doc_document.gif'/>"
									+ doc.getName() + "</td>", FILE);
					addToLogs("<td>" + doc.getNumber() + "</td>", FILE);
					addToLogs("<td>" + doc.getVersionIdentifier().getValue()
							+ "." + doc.getIterationIdentifier().getValue()
							+ "</td>", FILE);
					addToLogs("<td>" + doc.getState().getState().getDisplay()
							+ "</td>", FILE);
					addToLogs("</tr>", FILE);
				}
			}
		}

		toWrite = "</table><br/><br/><b>\tResulting Objects:</b><table border=1 cellspacing=0><tr class='thick'><th> Name </th><th> Number </th><th> Revision </th><th> State </th></tr>";
		addToLogs(toWrite, FILE);
		changeActivities = ChangeHelper2.service.getChangeActivities(order);

		while (changeActivities.hasMoreElements()) {
			wtChangeActivity2 = (WTChangeActivity2) changeActivities
					.nextElement();
			resulting = ChangeHelper2.service.getChangeablesAfter(
					wtChangeActivity2, false);

			while (resulting.hasMoreElements()) {
				ChangeRecord2 changeRecord2 = (ChangeRecord2) resulting
						.nextElement();
				persistable = changeRecord2.getChangeable2();
				if (persistable.getClass().isAssignableFrom(WTPart.class)) {
					part = (WTPart) persistable;
					addToLogs("<tr>", FILE);
					addToLogs(
							"<td>  <img src='http://windchill-as.detex.com/Windchill/wtcore/images/part.gif'/> "
									+ part.getName() + "</td>", FILE);
					addToLogs("<td>" + part.getNumber() + "</td>", FILE);
					addToLogs("<td>" + part.getVersionIdentifier().getValue()
							+ "." + part.getIterationIdentifier().getValue()
							+ "</td>", FILE);
					addToLogs("<td>" + part.getState().getState().getDisplay()
							+ "</td>", FILE);
					addToLogs("</tr>", FILE);
				} else if (persistable.getClass().isAssignableFrom(
						WTDocument.class)) {
					doc = (WTDocument) persistable;
					addToLogs("<tr>", FILE);
					addToLogs(
							"<td>  <img src='http://windchill-as.detex.com/Windchill/netmarkets/images/doc_document.gif'/> "
									+ doc.getName() + "</td>", FILE);
					addToLogs("<td>" + doc.getNumber() + "</td>", FILE);
					addToLogs("<td>" + doc.getVersionIdentifier().getValue()
							+ "." + doc.getIterationIdentifier().getValue()
							+ "</td>", FILE);
					addToLogs("<td>" + doc.getState().getState().getDisplay()
							+ "</td>", FILE);
					addToLogs("</tr>", FILE);
				} else if (persistable.getClass().isAssignableFrom(
						EPMDocument.class)) {
					cad = (EPMDocument) persistable;
					addToLogs("<tr>", FILE);
					addToLogs(
							"<td>  <img src='http://windchill-as.detex.com/Windchill/wt/clients/images/proe/prt_with_generic.gif'/> "
									+ cad.getName() + "</td>", FILE);
					addToLogs("<td>" + cad.getNumber() + "</td>", FILE);
					addToLogs("<td>" + cad.getVersionIdentifier().getValue()
							+ "." + cad.getIterationIdentifier().getValue()
							+ "</td>", FILE);
					addToLogs("<td>" + cad.getState().getState().getDisplay()
							+ "</td>", FILE);
					addToLogs("</tr>", FILE);
				}
			}
		}

		addToLogs("</td></tr></table>", FILE);
		// sendMail(order, FILE);
		System.out.println("DEBUG MODE: sendMail skipped");

	}

//	private static String getUsersEmailID(String groupName, String rtn)
//			throws WTException {
////		String[] services = OrganizationServicesHelper.manager
////				.getDirectoryServiceNames();
////		DirectoryContextProvider dc_provider = OrganizationServicesHelper.manager
////				.newDirectoryContextProvider(services, new String[]{"subtree"});
//		WTGroup gr = ParticipantProvider.getWTGroup(groupName);
////		gr = OrganizationServicesHelper.manager
////				.getGroup(groupName, dc_provider);
//		Enumeration enum1 = OrganizationServicesHelper.manager.members(gr, true);
//		HashSet emails = new HashSet();
//
//		while (enum1.hasMoreElements()) {
//			Object obj = enum1.nextElement();
//			if (obj instanceof WTUser) {
//				WTUser user = (WTUser) obj;
//				System.out.println("-->" + user.getEMail());
//				rtn = rtn + user.getEMail() + ",";
//				emails.add(user.getEMail());
//			} else if (obj instanceof WTGroup) {
//				WTGroup group = (WTGroup) enum1.nextElement();
//				rtn = rtn + getUsersEmailID(group.getName(), rtn);
//			}
//		}
//
//		System.out.println("emails" + emails);
//		return rtn;
//	}

	private static String getUsersEmailID(String groupName, String rtn)
	        throws WTException {

	    System.out.println("DEBUG MODE: getUsersEmailID skipped for group = " + groupName);
	    return "";
	}

	
	public static void addToLogs(String message, String fileName) {
		String WTHOME = null;
		PrintWriter pw = null;
		File f = new File(fileName);

		try {
			FileOutputStream fos;
			if (f.exists()) {
				fos = new FileOutputStream(f.getAbsolutePath(), true);
			} else {
				f.createNewFile();
				fos = new FileOutputStream(f.getAbsolutePath());
			}

			pw = new PrintWriter(fos, true);
			pw.println(message);
			pw.flush();
			pw.close();
			pw = null;
		} catch (IOException var8) {
			var8.printStackTrace();
		}

	}
}