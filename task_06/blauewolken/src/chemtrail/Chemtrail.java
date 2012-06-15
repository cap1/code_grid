package chemtrail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.globus.exec.generated.JobDescriptionType;
import org.globus.exec.generated.MultiJobDescriptionType;
import org.globus.rft.generated.RFTOptionsType;
import org.globus.rft.generated.TransferRequestType;
import org.globus.rft.generated.TransferType;

import chemtrail.SubmitJob;
import chemtrail.MDS4Client;

public class Chemtrail {

	public static void main(String[] args) {
		String usage = "Usage:\n./chemtrail inputfile width height outputfile";
		if (args.length != 4) {
			System.out.println(usage);
			System.exit(-1);
		}

		String InputFileName = args[0];
		String OutputFileName = args[3];

		int Width = Integer.parseInt(args[1]);
		int Heigth = Integer.parseInt(args[2]);

		System.out.println("Processing " + InputFileName + " with size of "
				+ Width + "x" + Heigth);

		MultiJobDescriptionType multi = new MultiJobDescriptionType();
		List<JobDescriptionType> multiJobs = new ArrayList<JobDescriptionType>();

		// TODO: Query middleware 'bout this
		int Nodes = 4;

		int yStep = Math.round(Heigth / Nodes);
		int yRest = Heigth % Nodes;
		
		String BaseName = (new File(InputFileName)).getName();

		String InputFileOnNode = "/tmp/griduser9/" + BaseName;

		for (int y = 0; y < Heigth - yStep; y += yStep) {
			int Offset = 0;
			if (yRest != 0) {
				Offset = 1;
				yRest--;
			}
			JobDescriptionType tempJob = new JobDescriptionType();
			tempJob.setExecutable("/usr/bin/povray");
			String arguments[] = new String[10];
			arguments[0] =  "-I" + InputFileName;
			arguments[1] =  "-FT";
			arguments[2] = "-WL0";
			arguments[3] = "-W" + Width;
			arguments[4] = "-H" + Heigth;
			arguments[5] = "-SR" + y + 1;
			arguments[6] = "-ER" + y + yStep + Offset;
			arguments[7] = "-SC1";
			arguments[8] = "-EC" + Width;
			// TODO: Proper output filename
			arguments[9] = "+O" + OutputFileName;
			tempJob.setArgument(arguments);

			// this is probably wrong
			TransferType inFileTransfer = new TransferType();
			inFileTransfer.setSourceUrl("file://" + InputFileName);
			inFileTransfer.setDestinationUrl(InputFileOnNode);

			TransferRequestType request = new TransferRequestType();
			request.setTransfer(new TransferType[1]);
			request.setTransfer(0, inFileTransfer);
			tempJob.setFileStageIn(request);

			multiJobs.add(tempJob);
		}
		multi.setJob((JobDescriptionType[]) multiJobs
				.toArray(new JobDescriptionType[0]));
		
		SubmitJob jobsubmitter = new SubmitJob();
		try {
			jobsubmitter.submitJob(multi);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
