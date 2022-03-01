import java.util.HashMap;

public class ArgParser{

    public static HashMap<String, String> parse(String[] args) throws Exception{
        HashMap<String, String> data = new HashMap<String, String>();

        if( args.length < 1) ArgParser.help();

        for(int i=0; i < args.length; i++){
            String opt = args[i];
			try {
				switch (opt) {
					case "-d":
						if (data.containsKey("operation"))
							ArgParser.help();
						data.put("operation", "decompress");
						if (!(args[i + 1].startsWith("-")))
							data.put("files", args[i + 1]);
						else
							ArgParser.help();
						break;

					case "-c":
						if (data.containsKey("operation"))
							ArgParser.help();
						data.put("operation", "compress");
						for (int j = i + 1; j < args.length; j++) {
							String files = data.containsKey("files") ? data.get("files") : "";
							if (!(args[j].startsWith("-")))
								data.put("files", files + args[j] + ';');
							else {
								i = j - 1;
								break;
							}
						}
						break;

					case "-f":
						data.put("force", "true");
						break;

					case "-r":
						data.put("source", args[i + 1]);
						break;

					case "-v":
						data.put("verbose", "true");
						break;

					case "-h":
						if (data.containsKey("operation"))
							ArgParser.help();
						data.put("operation", "help");
						break;
				}
			} catch (Exception e) {
				ArgParser.help();
			}
		}

		return ArgParser.validate(data);
	}

	private static HashMap<String, String> validate(HashMap<String, String> data) {
		String operation = data.get("operation");
		if (operation == null)
			ArgParser.help();

		else if (operation == "compress") {
			if (data.get("files") == null) {
				System.out.println("Fournir les fichiers a archiver");
				ArgParser.help();
			}
		}

		else if (operation == "decompress") {
			if (data.get("files") == null) {
				System.out.println("Fournir le fichier a decrompresser");
				ArgParser.help();
			} else if (data.get("files").split(";", 0).length != 1) {
				System.out.println("Un seul fichier peut eter decompresser");
				ArgParser.help();
			}
		}

		return data;
	}

	public static void help() {
		String[] opts = { "-h permet d'afficer le menu d'aide",
				"-c fich1 fich2 fich3 [-r [chemin destination]] [-f]",
				"-d [fichier].sfc [-r [chemin destination]] [-f]"
		};
		for (int i = 0; i < opts.length; i++) {
			System.out.println(opts[i]);
		}
		System.exit(0);
	}
}
