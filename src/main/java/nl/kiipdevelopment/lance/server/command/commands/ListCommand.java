package nl.kiipdevelopment.lance.server.command.commands;

import com.google.gson.JsonElement;
import nl.kiipdevelopment.lance.network.LanceMessage;
import nl.kiipdevelopment.lance.network.LanceMessageBuilder;
import nl.kiipdevelopment.lance.network.StatusCode;
import nl.kiipdevelopment.lance.server.ServerConnectionHandler;
import nl.kiipdevelopment.lance.server.command.Command;
import nl.kiipdevelopment.lance.server.storage.FileStorage;

public class ListCommand extends Command {
	public ListCommand() {
		super("list", "Lists the files in a directory. Works only for file storage.");
	}
	
	@Override
	public LanceMessage execute(ServerConnectionHandler handler, int id, String trigger, JsonElement json, String[] args) {
		LanceMessageBuilder builder = new LanceMessageBuilder();
		
		builder.setId(id).setStatusCode(StatusCode.OK);
		
		if (handler.server.storage.isJson()) {
			builder.setStatusCode(StatusCode.ERROR);
			builder.setMessage("Not file storage");
		} else {
			try {
				builder.setJson(((FileStorage) handler.server.storage).list());
			} catch (Exception e) {
				e.printStackTrace();
				return getInternalErrorMessage(id);
			}
		}
		
		return builder.build();
	}
}
