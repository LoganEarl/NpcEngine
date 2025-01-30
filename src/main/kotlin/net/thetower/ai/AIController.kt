package com.thetower.ai

import EntityController

class AIController(
        private val personality: Personality,
        private val mood: Mood,
        private val opinionsByEntityId: MutableMap<String, Opinion>,
) : EntityController{
    //TODO actually do any of these things
}

