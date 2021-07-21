package com.rspsi.editor.game.save;

import com.google.common.collect.Lists;
import com.rspsi.jagex.Client;
import com.rspsi.jagex.chunk.Chunk;
import com.rspsi.jagex.util.MultiMapEncoder;
import com.rspsi.misc.StatusUpdate;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;

public class AutoSaveJob {

	public static void execute(Client client) {
		try {
			//System.out.println("Autosave job called");
			EventBus.getDefault().post(new StatusUpdate("Autosaving..."));
			

			if(client.chunks.isEmpty())
				return;
			boolean chunksNotLoaded = client.chunks.stream().filter(Objects::nonNull).anyMatch(chunk -> !chunk.hasLoaded());
			
			if(chunksNotLoaded)
				return;
			
			File autosavePath = Paths.get(System.getProperty("user.home"), ".rspsi", "autosave").toFile();
			if(!autosavePath.exists())
				autosavePath.mkdirs();
			
			List<Chunk> currentChunks = Lists.newArrayList(client.chunks);
		
			byte[] saveData = MultiMapEncoder.encode(currentChunks);
			
			File objectFile = new File(autosavePath, "autosave.pack");
			
			File objectFileBackup =  new File(autosavePath, "autosave.pack.bk");
			
			
			if(objectFile.exists()) {
				objectFileBackup.delete();
				Files.move(objectFile.toPath(), objectFileBackup.toPath());
			}
			
			objectFile.delete();

			try {
				Files.write(objectFile.toPath(), saveData, StandardOpenOption.CREATE_NEW);
				objectFileBackup.delete();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				objectFile.delete();

				Files.copy(objectFileBackup.toPath(), objectFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
			EventBus.getDefault().post(new StatusUpdate("Autosaving complete"));
		} catch(Exception ex) {
			ex.printStackTrace();
			//throw new JobExecutionException(ex);
		}
		
	}

}
