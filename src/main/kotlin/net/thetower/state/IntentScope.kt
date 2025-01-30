package net.thetower.state

enum class IntentScope {
    //Only the person initiating the action
    SELF,

    //Initiator and anyone affected by it
    TARGETS,

    //Everyone in the locale of the initiator
    SELF_LOCALE,

    //Everyone in the locale of the target
    TARGET_LOCALE,

    //Everyone in the target locale AND initiator locale
    LOCALE,

    //Everyone without exception
    GLOBAL
}