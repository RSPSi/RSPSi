package com.rspsi.editor.tools;

import com.google.common.collect.Maps;
import com.rspsi.options.Options;
import lombok.val;

import java.util.Map;

public class ToolRegister {
    public static Map<String, Tool> registeredTools = Maps.newConcurrentMap();

    public static Tool getActiveTool() {
        return registeredTools.get(Options.currentTool.get());
    }

    public static Tool findTool(String identifier) throws ToolNotRegisteredException {
        val tool = registeredTools.get(identifier);
        if(tool == null)
            throw new ToolNotRegisteredException(identifier);
        return tool;
    }

    public void register(Tool tool) {
        registeredTools.put(tool.getId(), tool);
    }


}
