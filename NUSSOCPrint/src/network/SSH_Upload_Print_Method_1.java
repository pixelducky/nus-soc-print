package network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import ui.MainActivity;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.yeokm1.nussocprint.R;

public class SSH_Upload_Print_Method_1 extends SSHManager {

	Float progressIncrement;
	Float currentProgress = (float) 0;

	public SSH_Upload_Print_Method_1(MainActivity caller) {
		super(caller);
	}

	@Override
	protected String doInBackground(String... params) {

		progressIncrement = (float) 100 / 8;

		String filePath = params[0];
		String printerName = params[1];
		String pagesPerSheet = params[2];
		String startRange = params[3];
		String endRange = params[4];
		String lineBorder = params[5];

		File toBePrinted = new File(filePath);

		String tempDir = callingActivity.getString(R.string.server_temp_dir) + "/";
		String fileName = tempDir + "\"" + toBePrinted.getName() + "\"";
		String psFileName;
		String formattedPsFileName;

		if(toBePrinted.getName().endsWith("pdf")){
			psFileName =  fileName.substring(0, fileName.length() - 4) + "ps\"";  //-4 to remove pdf"
			formattedPsFileName = fileName.substring(0, fileName.length() - 4) + "psf\"";
		} else {
			psFileName = fileName;
			formattedPsFileName = fileName.substring(0, fileName.length() - 3) + "psf\"";
		}




		try {

			publishProgress("Uploading File");
			super.uploadFile(toBePrinted);


			publishProgress("Upload Complete");

			if(toBePrinted.getName().endsWith("pdf")){

				String convertToPSCommand = "pdftops";

				if(startRange != null){
					convertToPSCommand += " -f " + startRange;
				}

				if(endRange != null){
					convertToPSCommand += " -l " + endRange;
				}

				convertToPSCommand += " " + fileName + " "  + psFileName;	


				publishProgress("Converting to PostScript using: " + convertToPSCommand);

				String conversionMessage = super.sendCommand(convertToPSCommand);


				if(conversionMessage.isEmpty()){
					publishProgress("Conversion to Postscript Complete");
				} else {
					publishProgress(conversionMessage);
					return "Cannot convert file";
				}

			}

			//if no special parameters, we just send the ps file direct to printer
			if((lineBorder == null) && (Integer.parseInt(pagesPerSheet) == 1)){
				formattedPsFileName = psFileName;
			} else {

				String psformatCommand = "psnup -pa4";

				if(lineBorder != null){
					psformatCommand += " -d";
				}

				psformatCommand += " -" + pagesPerSheet;
				psformatCommand += " " + psFileName + " " + formattedPsFileName;


				publishProgress("PS format command used: " + psformatCommand);

				String psFormatMessage = super.sendCommand(psformatCommand);


				if(psFormatMessage.isEmpty()){
					publishProgress("Formatting of Postscript file Complete");
				} else {
					publishProgress(psFormatMessage);
					return "Cannot format file";
				}

			}
			String printCommand = "lpr -P ";


			printCommand += printerName + " ";

			printCommand += formattedPsFileName;

			publishProgress("Sending print command : \n" + printCommand);



			String printReply = super.sendCommand(printCommand);

			if(printReply.isEmpty()){
				return "Print command sent successfully";
			} else {
				return printReply;
			}


		} catch (FileNotFoundException e) {
			return String.format(FILE_NOT_FOUND_EXCEPTION_FORMAT, e.getMessage());
		} catch (SftpException e) {
			return String.format(SFTP_EXCEPTION_FORMAT, e.getMessage());
		} catch (JSchException e) {
			return String.format(JSCH_EXCEPTION_FORMAT, e.getMessage());
		} catch (IOException e) {
			return String.format(IO_EXCEPTION_FORMAT, e.getMessage());
		} finally {
			super.close();
		}



	}

	@Override
	protected void onProgressUpdate(String... progress){
		currentProgress += progressIncrement;
		String soFar = progress[0];
		callingActivity.updatePrintingStatusProgressBar(soFar, currentProgress.intValue());
	}


	@Override
	protected void onPostExecute(String output){
		callingActivity.updatePrintingStatusProgressBar(output, 100);
	}

}
