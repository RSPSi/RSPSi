package plugin.loader;

import com.rspsi.jagex.Client;
import com.rspsi.jagex.cache.graphics.Sprite;
import com.rspsi.jagex.io.Buffer;
import lombok.RequiredArgsConstructor;
import com.displee.cache.index.Index;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;
import com.rspsi.jagex.cache.ArchiveUtils;

import java.nio.ByteBuffer;

public class MapSceneLoader {

	private Sprite[] mapScenes;

	public void init(Client client, Archive archive, Index spriteIndex) {
		mapScenes = new Sprite[ArchiveUtils.getHighestFile(archive).getId()];
		for(File file : archive.files()) {
			if(file != null && file.getData() != null) {
				MapScene mapScene = decode(file.getId(), new Buffer(file.getData()));
				if(mapScene.spriteId != -1)
				mapScenes[mapScene.id] = Sprite.decode(ByteBuffer.wrap(spriteIndex.archive(mapScene.spriteId).file(0).getData()));
			}
		}
		client.mapScenes = this.mapScenes;
	}
	
	private MapScene decode(int id, Buffer buffer) {
		MapScene area = new MapScene(id);
		while (true) {
			int opcode = buffer.readUByte();
			if (opcode == 0)
				break;

			if(opcode == 1) {
				area.spriteId = buffer.readUShort();
			} else if(opcode == 2) {
				area.colourSomething = buffer.readUTriByte();
			} else if (opcode == 3) {
				area.someBoolean = true;
			} else if (opcode == 4) {
				area.spriteId = -1;
			}
		}
		return area;
	}

	@RequiredArgsConstructor
	public class MapScene {
		private final int id;
		int spriteId = -1, colourSomething;
		boolean someBoolean;
	}
}
