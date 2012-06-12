package chemtrail;
import chemtrail.Size2D;
import chemtrail.RenderArea;

public class PovrayJob {
	private String Executable;
	private Size2D OutputSize;
	private String OutputFile;
	private String InputFile;
	private RenderArea Area;
	
	public PovrayJob(String InputFile, String OutputFile, Size2D size)
	{
		Executable="/usr/bin/povray";
		this.InputFile=InputFile;
		this.OutputFile=OutputFile;
		this.OutputSize=size;
	}
	
	public String getInputFile() {
		return InputFile;
	}
	
	public void setInputFile(String inputFile) {
		InputFile = inputFile;
	}
	
	public String getExecutable() {
		return Executable;
	}
	
	public void setExecutable(String executable) {
		Executable = executable;
	}

	public Size2D getOutputSize() {
		return OutputSize;
	}
	
	public void setOutputSize(Size2D outputSize) {
		OutputSize = outputSize;
	}
	
	public String getOutputFile() {
		return OutputFile;
	}
	
	public void setOutputFile(String outputFile) {
		OutputFile = outputFile;
	}
	
	public RenderArea getArea() {
		return Area;
	}

	public void setArea(RenderArea area) {
		Area = area;
	}

	public String getRSL() {
		String result = "<job>";
		result += "<executable>"  + this.getExecutable() + "</executable>";
		result += "<argument>-I"  + this.getInputFile() + "</argument>";
		result += "<argument>-FT</argument>";
		result += "<argument>-WL0</argument>";
		result += "<argument>-W"  + this.getOutputSize().getWidth() + "</argument>";
		result += "<argument>-H"  + this.getOutputSize().getHeight() + "</argument>";
		result += "<argument>-SR" + this.getArea().getStart()+"</argument>";
		result += "<argument>-ER" + this.getArea().getEnd()+"</argument>";
		result += "<argument>-SC0</argument>";
		result += "<argument>-EC" + this.getOutputSize().getWidth() + "</argument>";
		result += "<argument>+O"  + this.getOutputFile() +  "</argument>";
		
		
		result += "</job>";
		return result;
	}

}
