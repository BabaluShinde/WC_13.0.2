package ext.dtx;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTGroup;
import wt.pds.StatementSpec;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.WhereExpression;
import wt.util.WTException;

public class ParticipantProvider {

	public static WTGroup getWTGroup(String groupName) {
		QuerySpec qs;
		WTGroup group = null;
		try {
			qs = new QuerySpec(WTGroup.class);
			SearchCondition sc = new SearchCondition(WTGroup.class, WTGroup.NAME,
					SearchCondition.EQUAL, groupName,false);
			qs.appendWhere((WhereExpression) sc, new int[] { 0 });
//			System.out.println(qs);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			
			while (qr.hasMoreElements()) {
				group = (WTGroup) qr.nextElement();
			}

		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return group;
	}
	
	
}
