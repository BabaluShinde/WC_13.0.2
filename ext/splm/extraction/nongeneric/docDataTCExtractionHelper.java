package ext.splm.extraction.nongeneric;

import java.io.IOException;

import wt.content.ApplicationData;
import wt.content.ContentServerHelper;
import wt.method.RemoteAccess;
import wt.util.WTException;
public class docDataTCExtractionHelper implements RemoteAccess {


	public static void writeInFolderLocation(ApplicationData obj,String filepath) throws IOException, WTException
	{
		
		if(obj instanceof ApplicationData)
		{
			ApplicationData data = obj;
				ContentServerHelper.service.writeContentStream(data, filepath);	
		}
		
	}
}
