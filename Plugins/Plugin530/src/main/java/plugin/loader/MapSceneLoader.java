package plugin.loader;

import com.jagex.Client;
import com.jagex.cache.graphics.Sprite;
import com.jagex.io.Buffer;
import lombok.RequiredArgsConstructor;
import com.displee.cache.index.Index;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;
import lombok.val;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class MapSceneLoader {

	private Sprite[] mapScenes;

	public void init(Client client, Archive archive, Index spriteIndex) {
		val highestId = Arrays.stream(archive.fileIds()).max().getAsInt();
		mapScenes = new Sprite[highestId];
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
