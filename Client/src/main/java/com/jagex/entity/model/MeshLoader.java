package com.jagex.entity.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.rspsi.cache.CacheFileType;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jagex.net.ResourceProvider;
import com.jagex.net.ResourceResponse;

@Slf4j
public class MeshLoader {

    public MeshLoader(ResourceProvider provider) throws Exception {
        if (singleton != null)
            throw new Exception("MeshLoader.class already loaded!");
        this.provider = provider;
        EventBus.getDefault().register(this);
        singleton = this;
    }

    private Map<Integer, Mesh> loadedMeshes = Collections.synchronizedMap(Maps.newHashMap());
    private List<Integer> awaitingLoad = Collections.synchronizedList(Lists.newArrayList());
    private ResourceProvider provider;


    public void clear(int id) {
        loadedMeshes.remove(id);
    }

    public void dispose() {
        clearAll();
        singleton = null;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onResourceResponse(ResourceResponse response) {
        if (response.getRequest().getType() == CacheFileType.MODEL) {
            load(response.decompress(), response.getRequest().getFile());
        }
    }

    public static Mesh load(byte[] data) {
        MeshRevision revision = MeshUtils.getRevision(data);
        switch (revision) {
            case REVISION_622:
                //return new Mesh622(data);
            case REVISION_525:
                return new Mesh525(data);
            case REVISION_OSRS202_TYPE3:
                return new MeshOSRSType3(data);
            case REVISION_OSRS202_TYPE2:
                return new MeshOSRSType2(data);
            case REVISION_317:
            default:
                return new MeshOldFormat(data);

        }
    }

    public Mesh load(byte[] data, int id) {
        MeshRevision revision = MeshUtils.getRevision(data);
        //System.out.println("Attempting to load model " + id + " revision " + revision.name());
        Mesh mesh = null;
        try {
            switch (revision) {
                case REVISION_622:
                    //mesh = new Mesh622(data);
                    //	break;
                case REVISION_525:
                    mesh = new Mesh525(data);
                    break;
                case REVISION_OSRS202_TYPE3:
                    mesh = new MeshOSRSType3(data);
                    break;
                case REVISION_OSRS202_TYPE2:
                    mesh = new MeshOSRSType2(data);
                    break;
                default:
                case REVISION_317:
                    mesh = new MeshOldFormat(data);
                    break;

            }
            mesh.id = id;
            mesh.revision = revision;

            loadedMeshes.put(id, mesh);
            awaitingLoad.remove(Integer.valueOf(id));
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return mesh;
    }

    public boolean loaded(int id) {
        if (loadedMeshes.containsKey(id))
            return true;

        boolean alreadyLoading = awaitingLoad.contains(id);
        if (!alreadyLoading) {
            awaitingLoad.add(id);
            System.out.println("Requested model " + id);
            provider.requestFile(CacheFileType.MODEL, id);
            return false;
        }
        return false;
    }

    public Mesh lookup(int id) {
        if (loaded(id)) {
            return loadedMeshes.get(id);
        }
        return null;
    }

    public void requestMesh(int id) {
        provider.requestFile(CacheFileType.MODEL, id);
    }


    public static MeshLoader getSingleton() {
        return singleton;
    }

    private static MeshLoader singleton;

    public void clearAll() {
        loadedMeshes.clear();
        awaitingLoad.clear();
    }
}
