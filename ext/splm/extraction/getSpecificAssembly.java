package ext.splm.extraction;

import java.util.ArrayList;
import java.util.HashSet;

import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.structure.EPMMemberLink;
import wt.epm.structure.EPMStructureHelper;
import wt.epm.workspaces.EPMAsStoredConfigSpec;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteMethodServer;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;
import wt.vc.struct.StructHelper;
import wt.epm.workspaces.EPMAsStoredConfig;
import wt.epm.workspaces.EPMAsStoredConfigSpec;
import wt.epm.workspaces.EPMAsStoredHelper;

public class getSpecificAssembly {

	public static QueryResult childlist;
	public static QueryResult structure;

	public static void main(String args[]) throws WTException,
			WTPropertyVetoException {
		getAllItems("815-00-09.ASM");
	}

	@SuppressWarnings("deprecation")
	public static void getAllItems(String ASMNumber) throws WTException,
			WTPropertyVetoException

	{
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");
		EPMDocument latestdoc = null;
		ArrayList<EPMDocument> arraylistTopEPM = new ArrayList();
		ArrayList<EPMMemberLink> arraylistTopEPMLink = new ArrayList();

		// arraylistTopEPMLink = new ArrayList();
		QuerySpec qs = new QuerySpec(EPMDocumentMaster.class);

		// qs.appendWhere(new SearchCondition(EPMDocumentMaster.class,
		// EPMDocumentMaster.NUMBER, SearchCondition.EQUAL, ASMNumber));

		qs.appendWhere(new SearchCondition(EPMDocumentMaster.class,
				EPMDocumentMaster.NUMBER, SearchCondition.EQUAL, ASMNumber),
				new int[] { 0, 1 });
		QueryResult localQueryResult = PersistenceHelper.manager
				.find((StatementSpec) qs);
		System.out.println("Query size" + localQueryResult.size());
		EPMDocumentMaster master = (EPMDocumentMaster) localQueryResult
				.nextElement();

		latestdoc = (EPMDocument) VersionControlHelper.service.allVersionsOf(
				master).nextElement();
		
		
		getEPMAsStoredConfigSpec(latestdoc);
		System.out.println("Latestdoc iteration"
				+ latestdoc.getIterationDisplayIdentifier().toString());
		System.out.println(wt.vc.VersionControlHelper.service
				.isFirstIteration((wt.vc.Iterated) latestdoc));
		EPMAsStoredConfigSpec configSpec = EPMAsStoredConfigSpec
				.newEPMAsStoredConfigSpec(latestdoc);
		childlist = EPMStructureHelper.service.navigateUsesToIteration(
				latestdoc, null, false, configSpec);
		QueryResult nqr = StructHelper.service.navigateUses(latestdoc, true);
		System.out.println("nqr.size())" + nqr.size());
		if (childlist.size() > 0) {
			for (int x = 0; x < childlist.size(); x++) {

				Persistable apersistable1[] = (Persistable[]) childlist
						.nextElement();
				if (apersistable1[1] instanceof EPMDocument) {
					EPMMemberLink bomUsage = (EPMMemberLink) apersistable1[0];
					EPMDocument child = (EPMDocument) apersistable1[1];

					arraylistTopEPM.add(child);
					arraylistTopEPMLink.add(bomUsage);
				}
			}

		}
		System.out.println("Number of First level Child parts ;; "
				+ arraylistTopEPM.size());
		for (int y = 0; y < arraylistTopEPM.size(); y++) {
			StringBuilder stringbuilder = null;

			EPMDocument epmChild = (EPMDocument) arraylistTopEPM.get(y);
			EPMMemberLink epmmemberlink = (EPMMemberLink) arraylistTopEPMLink
					.get(y);

			if (epmmemberlink != null) {

				stringbuilder = new StringBuilder();

				stringbuilder.append('[').append(
						epmmemberlink.isRequired() ? "(Required)" : "(O)")
						.append(
								epmmemberlink.isSuppressed() ? "(Suppressed)"
										: "").append(
								epmmemberlink.isSubstitute() ? "(Substitute)"
										: "").append(
								epmmemberlink.isPlaced() ? "(Placed)" : "")
						.append(
								epmmemberlink.isAnnotated() ? "(Annotated)"
										: "").append(']');

			}
			System.out.println(epmChild.getNumber() +"  "+ epmChild.getVersionDisplayIdentifier().toString());
			// System.out.println("stringbuilder" + stringbuilder.toString());
		}
		HashSet<EPMDocument> list = new HashSet<EPMDocument>();
		list.addAll(arraylistTopEPM);
		System.out.println("Hashset size" + list.size());
		for (EPMDocument x : list) {
			String number = x.getNumber();
			String docType = "" + x.getDocType();
			if (docType.equalsIgnoreCase("CADASSEMBLY")) {
				structure = getStructure(x);
			}
		}

	}

	
	private static EPMAsStoredConfigSpec getEPMAsStoredConfigSpec(final EPMDocument EPMDocument) throws WTException {
		EPMAsStoredConfigSpec configSpec = null;

		try {
			final EPMAsStoredConfig epmAsStoredConfig = EPMAsStoredHelper.getAsStoredConfig(EPMDocument);
		
			System.out.println("epmAsStoredConfig"+epmAsStoredConfig);
			configSpec = EPMAsStoredConfigSpec.newEPMAsStoredConfigSpec(EPMDocument);
			System.out.println("configSpec"+configSpec);
		}
		catch(final WTException e){
			e.printStackTrace();
		}
		catch(final WTPropertyVetoException e){
			e.printStackTrace();
		}
		return configSpec;
	}
	
	public static QueryResult getStructure(EPMDocument x)
			throws WTPropertyVetoException, WTException {

		EPMAsStoredConfigSpec configSpec = EPMAsStoredConfigSpec
				.newEPMAsStoredConfigSpec(x);
		childlist = EPMStructureHelper.service.navigateUsesToIteration(x, null,
				true, configSpec);
		return childlist;

	}
}
