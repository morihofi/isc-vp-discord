package de.morihofi.vp.discord.bot;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.github.mrebhan.crogamp.cli.TableList;

import de.morihofi.iscvplan.vertretungsplan.data.Vertretungsplan;
import de.morihofi.iscvplan.vertretungsplan.data.VertretungsplanEntry;
import de.morihofi.vp.discord.Main;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

public class MessageListener extends ListenerAdapter {

	@Override
	public void onReady(ReadyEvent event) {
		System.out.println("I am ready to go!");
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
//		System.out.printf("[%s]: %s\n", event.getAuthor().getName(), event.getMessage().getContentDisplay());

		String content = event.getMessage().getContentDisplay();
		MessageChannel channel = event.getChannel();

		String startchar = "!";
		if (content.startsWith(startchar)) {

			if (content.substring(startchar.length()).startsWith("vp")) {

				// Count '+' chars in string
				int days = (int) content.chars().filter(ch -> ch == '+').count();


				MessageCreateAction a = channel
						.sendMessage("Vertretungsplan wird abgerufen..."); /* => RestAction<Message> */

				try {

					a.queue(response /* => Message */ -> {
						String reason = "Ein unbekannter Fehler ist aufgetreten";
						List<Vertretungsplan> vpList = null;
						try {
							vpList = Main.vpHandler.getVertretungsplaene();
						}catch (IllegalArgumentException e){
							reason = "Beim Abrufen ist ein Fehler aufgetreten. Die wahrscheinlichste Ursache ist, dass das Passwort falsch ist. Bitte melden Sie diesen Fehler an <@" + Main.config_botuserid
									+ "> und versuche es später erneut.";
							e.printStackTrace();
							response.editMessageFormat("\u26A0 " + reason).queue();
							return;
						}




						if (days > (vpList.size() - 1)) {
							response.editMessageFormat(
											"\u26A0 Der angeforderte Vertretungsplan scheint zu weit in der Zukunft zu liegen.")
									.queue();
							return;
						}

						Vertretungsplan vp = vpList.get(days);
						ArrayList<de.morihofi.iscvplan.vertretungsplan.data.VertretungsplanEntry> vpEntries = vp.getEntries();


						//Entries in Vertretungsplan
						if (vpEntries.size() == 0) {
							reason = "Momentan stehen keine Vertretungspläne zur Verfügung";
							response.editMessageFormat(
									"\u26A0 " + reason)
									.queue();
							return;
						} else {




							StringWriter sw = new StringWriter();
							PrintWriter pw = new PrintWriter(sw);

							TableList tl = new TableList(4, "Klasse", "Stunde(n)", "Maßnahmen", "Verantwortlich")
									.sortBy(0).withUnicode(false);

							for (VertretungsplanEntry entry : vpEntries) {
								tl.addRow(entry.getSchoolClass(), entry.getLesson(), entry.getAction(),
										entry.getTeacher());
							}

							tl.print(pw);


							StringBuilder responsebuilder = new StringBuilder();
							responsebuilder.append("\uD83D\uDCC5 " + vpList.get(days).getDate() + "\n"); // Date
							responsebuilder.append("**Block:** " + vp.getBlock() + "\n"); // Date
							responsebuilder.append("**Zuletzt akt.:** " + vp.getLastUpdate() + "\n"); // Last Updated
							responsebuilder.append("\u2139\uFE0F " + vp.getHeader() + "\n"); // Header Line

							response.editMessageFormat(responsebuilder.toString()).queue();

							StringBuilder secondresponse = new StringBuilder();

							String[] rows = sw.toString().split("\n");

							int lineblocks = 8;

							for (int i = 0; i < rows.length; i += lineblocks) {
								channel.sendMessage(String.join("\n", getEntrys(rows, i, i + lineblocks))).queue();

							}

							if (!vp.getFooter().equals("")) {
								channel.sendMessage(vp.getFooter()).queue();
							}

							// channel.sendMessage(secondresponse.toString());

						}

						// sb.append("Datum: " + vp.getDate() + " - Zuletzt aktualisiert:" +
						// vp.getLastUpdate() + "\n" + sw.toString()
						// + "\n");

					});

				} catch (Exception e) {
					// TODO: handle exception
					channel.sendMessage("Beim Abrufen ist ein Fehler aufgetreren: " + e.getMessage() + " ("
							+ e.getClass().getSimpleName() + ")").queue();
					e.printStackTrace();
				}

			}
		}

	}

	public static List<String> getEntrys(String rows[], int from, int to) {
		List<String> selectedLines = new ArrayList<String>();
		selectedLines.add("```"); // Open codeblock
		for (int i = from; i < to + 1; i += 1) {
			try {
				selectedLines.add(rows[i]);
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
		selectedLines.add("```"); // Close codeblock
		return selectedLines;

	}

}
