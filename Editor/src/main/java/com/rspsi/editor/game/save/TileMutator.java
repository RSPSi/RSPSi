package com.rspsi.editor.game.save;

import com.rspsi.editor.game.save.tile.snapshot.TileSnapshot;

public interface TileMutator {

    TileMutation newMutation();

    void preserve(TileSnapshot snapshot) throws InvalidMutationException;

    void commit();

}
