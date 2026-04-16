package ext.splm.extraction.nongeneric;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap; // Added import
import java.util.Map;     // Added import

import wt.content.ApplicationData;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.lifecycle.LifeCycleManaged;
import wt.method.RemoteMethodServer;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.vc.config.ConfigSpec;
import wt.vc.Iterated; // Added import for Iterated
import wt.vc.IterationIdentifier; // Added import for IterationIdentifier


public class NonGenericCADDataExtractionUtility {

	/**
	 * @param args
	 */
	@SuppressWarnings("unused")
	private static final String txtSep = "|";
	private static DateFormat dateFormat = new SimpleDateFormat(
			"dd-MMM-yyyy hh:mm");// yyyy_MM_dd_HHmmss
	private static Calendar cal = Calendar.getInstance();
	private static final String USER_NAME = "wcadmin";
	private static final String USER_PWD = "wcadmin";
	private static File folderDir = null;
	private static String baseDir = "C:" + File.separator + "cad_contents";

	static StringBuilder stringbuilder;
	static String[] tmpStg, tmpStgCur, tmpStgCur2, strAsmDetails;
	static String OT, QueStg, StStg, epmNo, epmNmIn, epmNoIn, epmNoLg, epmNoSh,
			parStg, fullStg, fullStgSuppressed, childStg;
	static String oid, oidEpm, linkEpmStg, oidCh, oidPar, verStg, iterStg,
			typStg;
	static long OBID;
	static boolean isGen, isInst, isQty;
	static int ftStat, z, iDebug = 0;
	static EPMDocument epm, epm1, epmChild, epmChildp1, epmTop;
	static LifeCycleManaged LMObject;
	static QueryResult qrUses, qrChildChecker, qr;
	static ArrayList arraylistTopEPM;
	static ArrayList skipEPM = new ArrayList();

	static ArrayList arraylistTopEPMLink;
	static ConfigSpec configSpec;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Date lv_start = new Date();

		// java ext.cms.mr.epm.dataTCNonGenericDetails wcadmin wcadmin asm false
		// java ext.cms.mr.epm.dataTCNonGenericDetails wcadmin wcadmin prt false
		// java ext.cms.mr.epm.dataTCNonGenericDetails wcadmin wcadmin drw false

		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		String strUserName = new String();
		String strUserPass = new String();
		boolean needContent = false;

		String whatCADData = new String();

		skipEPM.add("SK-08-108.ASM");
		skipEPM.add("SK-08-108.DRW"); // it doestn't have primary content.
		skipEPM.add("BATT_PACK_HRNS.PRT");
		skipEPM.add("JAWA44.ASM");
		if (args.length == 4) {

			strUserName = args[0];
			strUserPass = args[1];
			whatCADData = args[2];
			needContent = Boolean.parseBoolean(args[3].toString().trim());

		} else
			throw new Exception(
					"\n\n_______________________________________________________________________\n\n "
							+ "Usage : java ext.custom.extraction.NonGenericCADDataExtractionUtility wcadmin wcadmin asm true\n\n "
							+ "	UserName  : Admin User ID \n "
							+ "	Password  : Admin Password \n "
							+ "	whatCADData  :\n\t asm or prt or drw"
							+ "	canDownload  :\n\t true or false"
							+ "\n\n_______________________________________________________________________\n");

		whatCADData = whatCADData.trim().toUpperCase();

		rms.setUserName(strUserName);
		rms.setPassword(strUserPass);

		/*
		 * rms.setUserName("wcadmin"); rms.setPassword("wcadmin"); whatCADData =
		 * "asm"; getEPMMasterByCADType(whatCADData.trim().toUpperCase(), true);
		 * whatCADData = "prt";
		 * getEPMMasterByCADType(whatCADData.trim().toUpperCase(), true);
		 */
		getEPMMasterByCADType(whatCADData.trim().toUpperCase(), needContent);

		System.out.println("Start Time :: " + lv_start);
		System.out.println("End   Time :: " + new Date());

	}

	@SuppressWarnings( { "deprecation" })
	public static QueryResult getEPMMasterByCADType(String whatItemType,
			boolean canDownload) throws Exception {
		QueryResult localQueryResult = new QueryResult();
		EPMDocumentMaster masterepm = null;

				String outputDir = "C:" + File.separator + "extraction";
		File directory = new File(outputDir);
		if (!directory.exists()) {
			directory.mkdir();
		}
		String createItemFileName = outputDir + File.separator + whatItemType + "_"
				+ "tcCadItemCreationDetails.csv";
		String cadDataSetFileName = outputDir + File.separator + whatItemType + "_"
				+ "tcCADDataSetDetails.csv";
		String cadModelDataFileName = outputDir + File.separator + whatItemType
				+ "_" + "tcCADModelDetails.csv";

		BufferedWriter fwriterCadDetails = new BufferedWriter(new FileWriter(
				new File(createItemFileName)));
		BufferedWriter fwriterCadModelDetails = new BufferedWriter(
				new FileWriter(new File(cadModelDataFileName)));
		BufferedWriter fwriterCadDataSetDetails = new BufferedWriter(
				new FileWriter(new File(cadDataSetFileName)));

		fwriterCadDetails
				.write("#ItemType|ItemID(Number)|RevID(REv andversion)|Description|ModifiedDate|Rel_status(State)|Date_created|Last_mod_user|owner|Item_Name|Group|APPROVED_BY|CHECKED_BY|DRW_STATUS|MATERIAL|ASSY_NO|WEIGHT|REMARKS|Supplier|surfacearea|description|mfgproces|drawingref|surfacefinish|WCVersion|TC State|EOL\n");
		fwriterCadModelDetails
				.write("#Level|ItemRev ID|Rev|QTY|Seq|Occ|UOM|Item Type\n");
		fwriterCadDataSetDetails
				.write("#DatasetType|DatasetName|AttachedTo|Filepath|Filename|Rev|Rel_status|Date_created|Modified_Date|owner|Last_mod_user|Group|TYPE_OF_DOC|EOL\n");

		try {
			QuerySpec qs = new QuerySpec(EPMDocumentMaster.class);

			qs.appendWhere(new SearchCondition(EPMDocumentMaster.class,
					EPMDocumentMaster.NUMBER, "LIKE", "%."
							+ whatItemType.toUpperCase().trim()), new int[] {
					0, 1 });

//			 qs.appendWhere(new SearchCondition(EPMDocumentMaster.class,
//			 EPMDocumentMaster.NUMBER, SearchCondition.EQUAL,
//			 "SK-621-01-33.PRT"), new int[] { 0, 1 }); // 531-00-50.ASM

			localQueryResult = PersistenceHelper.manager
                    .find((StatementSpec) qs);
            System.out.println("For ITEM " + whatItemType.toUpperCase().trim()
                    + " Result Size == " + localQueryResult.size());
            int totalNonGeneric = localQueryResult.size();
            for (int i = 0; i < totalNonGeneric; i++) {
                masterepm = (EPMDocumentMaster) localQueryResult.nextElement();

                QueryResult allIterationsQr = wt.vc.VersionControlHelper.service.allVersionsOf(masterepm);
                Map<String, EPMDocument> latestIterationsPerRevision = new HashMap<>();

                while (allIterationsQr.hasMoreElements()) {
                    EPMDocument currentEpm = (EPMDocument) allIterationsQr.nextElement();
                    String revision = currentEpm.getVersionDisplayIdentifier().toString();

                    EPMDocument existingLatest = latestIterationsPerRevision.get(revision);

                    if (existingLatest == null || isLaterIteration(currentEpm, existingLatest)) {
                        latestIterationsPerRevision.put(revision, currentEpm);
                    }
                }

                for (EPMDocument epmToProcess : latestIterationsPerRevision.values()) {
                    System.out.println((i + 1) + " of " + localQueryResult.size()
                            + " | " + epmToProcess.getNumber() + " | "
                            + epmToProcess.getVersionDisplayIdentifier()
                            + "." + epmToProcess.getIterationIdentifier().getValue()
                            + "| Family Status Is "
                            + epmToProcess.getFamilyTableStatus());

                    boolean tempResult = (epmToProcess.getFamilyTableStatus() == 0)
                            && !(skipEPM.contains(epmToProcess.getNumber()
                                    .toUpperCase()));

                    if (tempResult) {
                        String versionState = epmToProcess.getState().getState().getDisplay().toLowerCase();
                        String folderPath = dataUtil.buildFolderPath(epmToProcess, baseDir, versionState);
                        String fullVersionIteration = epmToProcess.getVersionDisplayIdentifier().toString() + "." + epmToProcess.getIterationIdentifier().getValue();
                        String outData = dataUtil.getCADNonGenericDetails(epmToProcess, fullVersionIteration);
                        fwriterCadDetails.write(outData + "|" + versionState + "|\n");

                        // GET CAD MODEL Details Start
                        String docType = "" + epmToProcess.getDocType();
                        if (docType.equalsIgnoreCase("CADASSEMBLY")) {
                            String bomData = dataUtil.getCADModelDetails(epmToProcess, false); // Assuming false as it's not the overall latest
                            fwriterCadModelDetails.write(bomData);
                        }
                        // END

                        // GET DATA SET DETAILS START
                        String fileName = new String();
                        ApplicationData appData = dataUtil.getEPMAppData(epmToProcess);
                        fileName = dataUtil.getEPMFileName(epmToProcess, appData);
                        String DatasetType = fileName.substring(
                                fileName.lastIndexOf(".") + 1,
                                fileName.length()).toLowerCase();
                        if (canDownload) {
                            folderDir = new File(folderPath);
                            folderDir.mkdirs();
                        }
                        String currfileWithPathName = folderPath
                                + File.separator + fileName;

                        if (canDownload) {
                            Class[] classprms = { ApplicationData.class,
                                    String.class };
                            Object[] objectprms = { appData,
                                    currfileWithPathName };
                            RemoteMethodServer.getDefault().invoke(
                                    "writeInFolderLocation",
                                    docDataTCExtractionHelper.class.getName(),
                                    null, classprms, objectprms);
                        }

                        outData = dataUtil.getEPMDataSetDetails(epmToProcess,
                                fullVersionIteration,
                                dataUtil.getDataSetCADType(DatasetType),
                                fileName, currfileWithPathName);

                        fwriterCadDataSetDetails.write(outData + "\n");
                        appData = null;
                        // END
                    } else {
                        System.out.println(":: FAMILY ::");
                    }
                }
                fwriterCadDetails.flush();
                fwriterCadModelDetails.flush();
                fwriterCadDataSetDetails.flush();
            }
            fwriterCadDetails.flush();
            fwriterCadDetails.close();

            fwriterCadModelDetails.flush();
            fwriterCadModelDetails.close();

            fwriterCadDataSetDetails.flush();
            fwriterCadDataSetDetails.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return localQueryResult;
    }

    /**
     * Helper method to determine if currentEpm is a later iteration than existingLatest.
     * Assumes both EPMDocuments belong to the same revision.
     */
    private static boolean isLaterIteration(EPMDocument currentEpm, EPMDocument existingLatest) {
        // Parse the iteration values to integers for comparison
        int currentIteration = Integer.parseInt(currentEpm.getIterationIdentifier().getValue());
        int existingIteration = Integer.parseInt(existingLatest.getIterationIdentifier().getValue());
        return currentIteration > existingIteration;
    }
}
