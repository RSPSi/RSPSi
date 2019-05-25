package com.rspsi.game.save;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.google.common.collect.Lists;
import com.jagex.Client;
import com.jagex.chunk.Chunk;
import com.jagex.util.MultiMapEncoder;
import com.rspsi.misc.StatusUpdate;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class AutoSaveJob implements Job {

	@Override
	public void execute(JobExecutionContext context) {
		try {
			//System.out.println("Autosave job called");
			EventBus.getDefault().post(new StatusUpdate("Autosaving..."));
			
			Client client = (Client) context.getJobDetail().getJobDataMap().get("client");

			if(client.chunks.isEmpty())
				return;
			File autosavePath = Paths.get(System.getProperty("user.home"), ".rspsi", "autosave").toFile();
			if(!autosavePath.exists())
				autosavePath.mkdirs();
			
			List<Chunk> currentChunks = Lists.newArrayList(client.chunks);
		
			byte[] saveData = MultiMapEncoder.encode(currentChunks);
			
			File objectFile = new File(autosavePath, "map.autosave");
			
			File objectFileBackup =  new File(autosavePath, "map.bk");
			
			
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
