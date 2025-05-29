package com.dark.neuroverse.neurov.mcp.model

import com.dark.neuroverse.neurov.mcp.tools.ProgramsTool

enum class ToolYard(val toolName: String, val toolId: Int, val tool: Any) {
    PROGRAM_TOOL("Program Tool", 1, ProgramsTool())
}