package com.sparkynox.lumimotion

import com.sparkynox.lumimotion.screen.LumiConfigScreen
import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi

// Optional integration - only loaded if Mod Menu is present (see fabric.mod.json custom entrypoint)
class LumiModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory { parent -> LumiConfigScreen(parent) }
    }
}
