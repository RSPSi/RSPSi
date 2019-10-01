import org.displee.CacheLibrary;
import org.displee.cache.index.Index;
import org.displee.cache.index.Index317;
import org.displee.cache.index.archive.Archive;
import org.displee.cache.index.archive.Archive317;
import org.displee.cache.index.archive.file.File;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test317 extends TestCase {
   protected java.io.File cachePath;
   
   // assigning the values
   protected void setUp(){
      cachePath = new java.io.File("F:/cacheosrs180/");
   }

   // test method to add two values
   public void testAdd() throws IOException{
      CacheLibrary cacheLib = new CacheLibrary(cachePath.toPath());
      
      assertTrue(cacheLib.is317());
      
      Index modelIndex = cacheLib.getIndex(1);
      
      assertTrue(modelIndex != null && modelIndex instanceof Index317);
      
      System.out.println(Arrays.toString(modelIndex.getArchiveIds()));
      
      for(int i = 0;i<37272;i++) {
    	  Archive archive = modelIndex.getArchive(i);
    	  assertTrue(archive instanceof Archive317);
    	  byte[] f = archive.readFile(0);
    	  assertTrue(f != null);
      }
      
   }
}