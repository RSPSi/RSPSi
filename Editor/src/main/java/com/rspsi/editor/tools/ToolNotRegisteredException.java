package com.rspsi.editor.tools;

public class ToolNotRegisteredException extends Throwable {

    public ToolNotRegisteredException(String identifier) {
        super(identifier);
    }
}
