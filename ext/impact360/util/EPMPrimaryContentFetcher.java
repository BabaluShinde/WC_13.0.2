package ext.impact360.util;

import java.net.URL;
import java.util.*;
import wt.content.*;
import wt.epm.*;
import wt.epm.structure.EPMStructureHelper;
import wt.epm.workspaces.EPMAsStoredConfigSpec;
import wt.fc.*;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.VersionReference;

public class EPMPrimaryContentFetcher {
	/**
	 * Updated to return a List of Maps containing all metadata.
	 */
	private static final ReferenceFactory rf = new ReferenceFactory();

	public static List<Map<String, Object>> getAllPrimaryURLsDetailed(String asmNumber) throws Exception {
		List<Map<String, Object>> results = new ArrayList<>();
		Set<String> visited = new HashSet<>();
		EPMDocument asm = findLatestEPM(asmNumber);
		if (asm == null)
			return results;
		collectDetailedRecursive(asm, results, visited);
		return results;
	}

	private static void collectDetailedRecursive(EPMDocument parent, List<Map<String, Object>> results,
			Set<String> visited) throws Exception {
		if (parent == null || visited.contains(parent.getNumber()))
			return;
		visited.add(parent.getNumber());
		QueryResult qr = ContentHelper.service.getContentsByRole(parent, ContentRoleType.PRIMARY);
		if (qr.hasMoreElements()) {
			ApplicationData ad = (ApplicationData) qr.nextElement();

			Map<String, Object> row = new HashMap<>();

			String fileName = ad.getFileName();
			if (fileName == null || fileName.contains("{$CAD_NAME}")) {
				fileName = parent.getCADName();
			}
			row.put("FileName", fileName);
			row.put("ID", rf.getReferenceString(ad));

			URL url = ContentHelper.getDownloadURL(parent, ad);
			String urlStr = url.toString();

			// --- Fix {$CAD_NAME} in URL (Explicitly in Java) ---
			if (urlStr.contains("%7B%24CAD_NAME%7D")) {
				String encodedName = java.net.URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
				urlStr = urlStr.replace("%7B%24CAD_NAME%7D", encodedName);
			} else if (urlStr.contains("{$CAD_NAME}")) {
				String encodedName = java.net.URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
				urlStr = urlStr.replace("{$CAD_NAME}", encodedName);
			}
			row.put("DownloadURL", urlStr);

			row.put("VersionID", rf.getReferenceString(VersionReference.newVersionReference(parent)));

			// --- Put the Metadata you calculated into the Map ---
			row.put("FileSize", (double) ad.getFileSizeKB());
			row.put("ContainerName", parent.getContainerName());
			row.put("CreatorFullName", parent.getCreatorFullName());
			row.put("CreateTimestamp", parent.getCreateTimestamp());
			row.put("ModifyTimestamp", parent.getModifyTimestamp());
			row.put("CheckoutStatus", parent.getCheckoutInfo().getState().getDisplay());
			// Use the helper for Value/Display pairs required by DisplayValue.json
			row.put("LifeCycleState", createDisplayMap(parent.getLifeCycleState()));
			row.put("DocType", createDisplayMap(parent.getDocType()));
			row.put("AuthoringApplication", createDisplayMap(parent.getAuthoringApplication()));
			results.add(row);
		}
		EPMAsStoredConfigSpec config = EPMAsStoredConfigSpec.newEPMAsStoredConfigSpec(parent);
		QueryResult children = EPMStructureHelper.service.navigateUsesToIteration(parent, null, false, config);
		while (children.hasMoreElements()) {
			Object[] o = (Object[]) children.nextElement();
			if (o[1] instanceof EPMDocument) {
				collectDetailedRecursive((EPMDocument) o[1], results, visited);
			}
		}
	}

	private static Map<String, String> createDisplayMap(Object obj) {
		if (obj == null)
			return null;
		Map<String, String> map = new HashMap<>();
		if (obj instanceof wt.fc.EnumeratedType) {
			map.put("Value", ((wt.fc.EnumeratedType) obj).toString());
			map.put("Display", ((wt.fc.EnumeratedType) obj).getDisplay());
		} else {
			map.put("Value", obj.toString());
			map.put("Display", obj.toString());
		}
		return map;
	}

	private static EPMDocument findLatestEPM(String number) throws WTException {
		QuerySpec qs = new QuerySpec(EPMDocument.class);
		qs.appendSearchCondition(
				new SearchCondition(EPMDocument.class, EPMDocument.NUMBER, SearchCondition.EQUAL, number, false));
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (!qr.hasMoreElements())
			return null;
		EPMDocument doc = (EPMDocument) qr.nextElement();
		EPMDocumentMaster master = (EPMDocumentMaster) doc.getMaster();
		QueryResult versions = VersionControlHelper.service.allVersionsOf(master);
		EPMDocument latest = null;
		while (versions.hasMoreElements()) {
			latest = (EPMDocument) VersionControlHelper.service.getLatestIteration((EPMDocument) versions.nextElement(),
					true);
		}
		return latest;
	}
}