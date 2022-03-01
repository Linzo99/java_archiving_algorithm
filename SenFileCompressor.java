import java.util.HashMap;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

public class SenFileCompressor {
	int bufferSize = 8 * 1024;
	HashMap<String, String> data;
	String dest;
	String[] files;
	Boolean verbose;
	Boolean force;

	public SenFileCompressor(HashMap<String, String> val){
		this.data = val;
		this.dest = data.containsKey("source") ? data.get("source") : "./" ;
		this.verbose = data.containsKey("verbose") ? true : false;
		this.force = data.containsKey("force") ? true : false;
		this.files = data.containsKey("files") ? data.get("files").split(";", 0) : null;
		
		if( !this.dest.endsWith("/") ) this.dest += "/";
		if( this.force){
			File destination = new File(this.dest);
			if( !destination.isDirectory())
				destination.mkdirs();

		}
	}

	private void init() throws Exception{
		String ope = this.data.get("operation");
		switch( ope ){
			case "compress":
				this.compress();
				break;
			case "decompress":
				this.decompress();
				break;
			default:
				ArgParser.help();
		}
	}

	private void archive() throws Exception{
		//On Archive et Compresse en meme Temps
		//Donc pas besoin d'un fichier tmp
		DeflaterOutputStream output = new DeflaterOutputStream( new FileOutputStream(this.dest+"compressed.sfc"));
		for(String file : this.files){
			String[] name = file.split("/", 0);
			String filename = name[name.length-1];
			if( this.verbose) System.out.println("Compression de "+filename+" ...");
			BufferedInputStream input = new BufferedInputStream( new FileInputStream(file), bufferSize );
			int n = input.available();
			byte[] b = new byte[n];
			input.read(b);
			output.write(filename.getBytes());
			output.write(("\n"+n+"\n").getBytes());
			output.write(b);
			input.close();

		}
		output.close();
	}

	private void dearchive() throws Exception{
		//On decompresse et dearchive en meme temps
		//Donc pas besoin d'un fichier tmp
		char v;
		InflaterInputStream input = new InflaterInputStream( new FileInputStream( this.files[0] ) );
		while( input.available() > 0){
			String name ="";
			String size ="";
			while( (v = (char)input.read() ) != '\n') name += v;
			while( (v = (char)input.read() ) != '\n') size += v;
			if( this.verbose) System.out.println("Decompression de "+name+" ...");
			FileOutputStream output = new FileOutputStream( this.dest+name );
			for(int i=0; i < Integer.valueOf(size); i++) output.write(input.read());
			output.close();
		}
		input.close();
	}

	private void compress() throws Exception{
		//J'archive et je compresse en mm temps
		this.archive();
	}

	private void decompress() throws Exception{
		//Je decompresse et dearchive en mm temps
		this.dearchive();
	}

	public static void main(String[] args) {
		try{

			HashMap<String, String> data = ArgParser.parse(args);
			SenFileCompressor Compressor = new SenFileCompressor(data);
			Compressor.init();

		}
		catch( Exception e){
			System.out.println(e.getMessage());
		}

	} 
}
