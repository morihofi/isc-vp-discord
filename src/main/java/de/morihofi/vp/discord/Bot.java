package de.morihofi.vp.discord;

import javax.security.auth.login.LoginException;

import de.morihofi.vp.discord.bot.MessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;


public class Bot {

	public static void StartBot() throws LoginException {

		JDABuilder builder = JDABuilder.createDefault(Main.config_bot_token);

		builder.setActivity(Activity.watching("Type !vp"));
		/*
		 * builder.addEventListeners(new EventListener() {
		 * 
		 * @Override public void onEvent(GenericEvent event) { //Check if Bot is ready
		 * if (event instanceof ReadyEvent) { System.out.println("Bot is ready!"); } }
		 * 
		 * });
		 */
		builder.addEventListeners(new MessageListener());
		//builder.addEventListeners(new SlashListener());
		builder.enableIntents(GatewayIntent.MESSAGE_CONTENT); // enables explicit access to message.getContentDisplay()
		builder.enableIntents(GatewayIntent.DIRECT_MESSAGES);
		JDA jda = builder.build();
		
		//jda.upsertCommand("vp", "Vertretungsplan anfordern. Beispiel /vp oder /vp DATUM").queue(); // This can take up to 1 hour to show up in the client
	}


}
