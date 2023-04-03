package de.morihofi.vp.discord;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import de.morihofi.iscvplan.ISCVertretungsplanHandler;
import org.json.JSONObject;

public class Main {

	public static String config_password = ""; // Passwort für den Stundenplan
	public static String config_botuserid = ""; // User-Id vom Bot-Admin (Erwähnung, wenn Passwort ungültig
	public static String config_bot_token = ""; // Discord Bot Token

	public static ISCVertretungsplanHandler vpHandler;

	// Invite link
	// https://discord.com/api/oauth2/authorize?client_id=1014920514139066399&permissions=2147670080&scope=bot

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub


		String settingsFilePath = getWorkingDirectory() + File.separator + "settings.json";
		File settingsFile = new File(settingsFilePath);
		if (settingsFile.exists()) {
			System.out.println("Reading " + settingsFilePath + "...");
			String settingsJson = readLineByLineJava8(settingsFilePath);
			JSONObject obj = new JSONObject(settingsJson);
			config_bot_token = obj.getString("token");
			config_password = obj.getString("password");
			config_botuserid = obj.getString("botuserid");
			// startchar = obj.getString("startcharacter");
			System.out.println("Reading complete!");

			vpHandler = new ISCVertretungsplanHandler(config_password);

		} else {
			System.out.println(settingsFilePath + " doesn't exist. Exiting...");
			System.exit(1);
		}

		Bot.StartBot();

	}

	/**
	 * Function to load file contents to string
	 * @param filePath path to file
	 */
	public static String readLineByLineJava8(String filePath) {
		StringBuilder contentBuilder = new StringBuilder();

		try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return contentBuilder.toString();
	}

	public static String getWorkingDirectory() {
		return System.getProperty("user.dir");
	}

}
