package net.thetower.engine.state

/*
Actions can be sorted into various categories. Think of them like search tags.
These are a create-as-needed thing. If you have an AI and you want it to respond to a new type of thing,
create a category for that thing and update the relevant actions to match
 */
enum class NotificationCategory {
    //TODO these are all just examples for me to use as later reference. Might delete

    // Any action that can the ability to do damage
    COMBAT,

    // Won't do damage it of itself, but could lead to it. Drawing weapons, saying threats, combat buffs, etc
    THREAT,

    //Just a social exchange
    SOCIAL,

    DEBUG
}