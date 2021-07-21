package com.rspsi.editor.game.save;

import com.rspsi.editor.game.save.tile.snapshot.TileSnapshot;

public abstract class DefaultTileMutator implements TileMutator {

    public UndoRedoSystem undoRedoSystem;

    public DefaultTileMutator(UndoRedoSystem undoRedoSystem) {
        this.undoRedoSystem = undoRedoSystem;
    }

    public TileSnapshot currentSnaphot;
    public boolean commitAfterPreserve;


    @Override
    public void preserve(TileSnapshot snapshot) throws InvalidMutationException {
        if(!undoRedoSystem.currentState.canPreserve(snapshot))
            throw new InvalidMutationException(newMutation(), snapshot);

        if(undoRedoSystem.currentState != null) {
            currentSnaphot = snapshot;
            undoRedoSystem.currentState.storeSnapshot(currentSnaphot);
            if(commitAfterPreserve)
                commit();
        }
    }

    @Override
    public void commit() {
        currentSnaphot = null;
        undoRedoSystem.commitChanges();
    }
}
