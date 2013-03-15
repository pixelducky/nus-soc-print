package network;

import java.io.IOException;
import java.util.List;

import ui.MainActivity;

import android.widget.Toast;

import com.jcraft.jsch.JSchException;
import com.yeokm1.nussocprint.R;

public class SSH_Print_Quota extends SSHManager {

	public SSH_Print_Quota(MainActivity caller) {
		super(caller);
	}

	@Override
	protected String doInBackground(String... params) {
		
		String fileName = "quotaUsage.txt";
		
		try {
			List<String> stringList= super.sendShellCommand("pusage");
//			publishProgress("pusage > " + fileName);

	//		super.sendCommand("rm -f " + fileName);
			
			for(String entry : stringList){
				publishProgress(entry);
			}
			
			return "";
		} catch (JSchException e) {
			return "Jsch exception " + e.getMessage();
		} catch (IOException e) {
			return "IO exception " + e.getMessage();
		} finally {
			super.close();
		}
	}
	
	@Override
	protected void onProgressUpdate(String... progress){
		callingActivity.showToast(progress[0]);
	}
	
	@Override
	protected void onPostExecute(String output){
		callingActivity.showToPrinterStatus(output);

	}

}
