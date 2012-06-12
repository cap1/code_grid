package chemtrail;
import chemtrail.SubmitJob;
import chemtrail.MDS4Client;

public class Chemtrail {
	
	


    public static void main(String[] args) {
    	String usage="Usage:\n./chemtrail inputfile width height outputfile";
        if(args.length != 4) {
            System.out.println(usage);
            System.exit(-1);
        }
        
        String InputFilename = args[0];
        String OutputFilename = args[3];
        
        int Width = Integer.parseInt(args[1]);
        int Height = Integer.parseInt(args[2]);
       
        System.out.println("Processing " + InputFilename + " with size of " + Width +"x" + Height);


		
	MultiJobDescriptionType multi = new MultiJobDescriptionType;
	List<JobDescriptionType> multiJobs = new ArrayList<JobDescriptionType>();

	//TODO: Query middleware 'bout this
	int Nodes = 4;
	
	int yStep = Math.round(Heigth/Nodes);
	int yRest = Heigth % Nodes;


	for ( int y = 0; y < Heigth-yStep; y+= yStep ) {
		int Offset = 0;	
		if(yRest != 0) {
			Offset=1;
			yRest--;
		}
		tempJob = new JobDescriptionType ();
		tempJob.setExecutable("/usr/bin/povray");
		tempJob.setArgument(0,"-I" + InputFilename);
		tempJob.setArgument(1,"-FT");
		tempJob.setArgument(2,"-WL0");
		tempJob.setArgument(3,"-W" + Width);
		tempJob.setArgument(4,"-H" + Height);
		tempJob.setArgument(5,"-SR" + y+1);
		tempJob.setArgument(6,"-ER" + y + yStep + Offset);
		tempJob.setArgument(7,"-SC1");
		tempJob.setArgument(8,"-EC" + Width);
		//TODO: Proper output filename
		tempJob.setArgument(9,"+O" + OutputFilename);
		multiJobs.add(tempJob);
	}
	multi.setJob((JobDescriptionType[]) multiJobs.toArray(new JobDescriptionType[0]));
	
		

    }
}
