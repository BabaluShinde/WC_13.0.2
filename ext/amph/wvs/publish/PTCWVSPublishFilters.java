package ext.amph.wvs.publish;

import wt.epm.EPMDocument;
import wt.fc.Persistable;

/**
 * Custom Publish Queue Filter — Combined Routing
 *
 * Queue : lifecycle state → INWORK / RELEASED / null (default) Priority : doc
 * type → H (Assembly) / M (Part) / L (Drawing) Worker : lifecycle state →
 * INWORK / RELEASED [matches agent.ini: queueset=INWORK,RELEASED]
 */
public class PTCWVSPublishFilters {

	// Queue Set Names — must match publish.publishqueue.setnames in wvs.properties
	private static final String QUEUE_INWORK = "INWORK";
	private static final String QUEUE_RELEASED = "RELEASED";

	// Worker Names — must match agent.ini: queueset=INWORK,RELEASED
	private static final String WORKER_INWORK = "INWORK";
	private static final String WORKER_RELEASED = "RELEASED";

	// Lifecycle State Strings — returned by EPMDocument.getLifeCycleState()
	private static final String STATE_INWORK = "INWORK";
	private static final String STATE_RELEASED = "RELEASED";

	// Doc Type Strings
	private static final String TYPE_ASSEMBLY = "CADASSEMBLY";
	private static final String TYPE_PART = "CADCOMPONENT";
	private static final String TYPE_DRAWING = "CADDRAWING";

	// =========================================================================
	// METHOD 1 — publishqueueFiltermethod
	// Called when a publish job is submitted.
	// ret[0] = priority (H/M/L) — driven by doc type
	// ret[1] = queue set name — driven by lifecycle state
	// ret[2] = rep name (null = keep existing)
	// ret[3] = rep desc (null = keep existing)
	// =========================================================================
	public static String[] publishqueueFiltermethod(Persistable p, Integer requestType, Integer requestSource,
			String requestQueuePriority, String requestQueueSet, String repName, String repDesc) {

		String[] ret = { null, null, null, null };

		if (!(p instanceof EPMDocument)) {
			return ret;
		}

		EPMDocument epmdoc = (EPMDocument) p;

		try {
			String stateName = epmdoc.getLifeCycleState().toString().toUpperCase().trim();

			// --- Queue: driven by lifecycle state ---
			if (STATE_INWORK.equals(stateName)) {
				ret[1] = QUEUE_INWORK;
			} else if (STATE_RELEASED.equals(stateName)) {
				ret[1] = QUEUE_RELEASED;
			}

			// --- Priority: driven by doc type (PROE only) ---
			if (epmdoc.getAuthoringApplication().toString().toUpperCase().equals("PROE")) {
				String docType = epmdoc.getDocType().toString();

				if (TYPE_ASSEMBLY.equals(docType)) {
					ret[0] = "H";
				} else if (TYPE_PART.equals(docType)) {
					ret[0] = "M";
				} else if (TYPE_DRAWING.equals(docType)) {
					ret[0] = "L";
				}
			}

			System.out.println("==== publishqueueFiltermethod ====");
			System.out.println("  Object   : " + epmdoc.getNumber());
			System.out.println("  State    : " + stateName);
			System.out.println("  DocType  : " + epmdoc.getDocType());
			System.out.println("  Queue    : " + ret[1]);
			System.out.println("  Priority : " + ret[0]);

		} catch (Exception e) {
			System.out.println("ERROR in publishqueueFiltermethod: " + e.getMessage());
			e.printStackTrace();
		}

		return ret;
	}

	// =========================================================================
	// METHOD 2 — publishUsesetworkersFiltermethod
	// Called when a CAD conversion job is submitted to the CAD Agent.
	// Returns the worker queueset name — must match agent.ini:
	// queueset=INWORK,RELEASED
	// null = any available (default) worker
	// =========================================================================
	public static String publishUsesetworkersFiltermethod(Persistable p, String workerType, String cadType,
			String fileName, String requestQueuePriority, String requestQueueSet) {

		String ret = null; // null = default worker

		if (!(p instanceof EPMDocument)) {
			return ret;
		}

		EPMDocument epmdoc = (EPMDocument) p;

		try {
			String stateName = epmdoc.getLifeCycleState().toString().toUpperCase().trim();

			// --- Worker: driven by lifecycle state (matches agent.ini queueset) ---
			if (STATE_INWORK.equals(stateName)) {
				ret = WORKER_INWORK;
			} else if (STATE_RELEASED.equals(stateName)) {
				ret = WORKER_RELEASED;
			}

			System.out.println("==== publishUsesetworkersFiltermethod ====");
			System.out.println("  Object     : " + epmdoc.getNumber());
			System.out.println("  State      : " + stateName);
			System.out.println("  WorkerType : " + workerType);
			System.out.println("  CADType    : " + cadType);
			System.out.println("  Worker     : " + (ret != null ? ret : "DEFAULT"));

		} catch (Exception e) {
			System.out.println("ERROR in publishUsesetworkersFiltermethod: " + e.getMessage());
			e.printStackTrace();
		}

		return ret;
	}

	public static void main(String[] args) {
		System.out.println("PTCWVSPublishFilters loaded.");
		System.out.println("EXITSTATUS:1");
	}
}