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
        
        String input_filename = args[0];
        String output_filename = args[3];
        
        int width = Integer.parseInt(args[1]);
        int height = Integer.parseInt(args[2]);
       
        System.out.println("Processing " + input_filename + " with size of " + width +"x" + height);
    }
    
    
    


}
