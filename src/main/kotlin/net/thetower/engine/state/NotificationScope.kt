package net.thetower.engine.state

enum class NotificationScope {
    //Only the person initiating the action
    SELF,

    //Initiator and anyone affected by it
    SUBJECTS,

    //Everyone in the locale of the initiator
    SELF_LOCALE,

    //Self and all the target's locale
    TARGET_LOCALE,

    //Everyone in both the self locale and target locale (as they might be different)
    SUBJECT_LOCALES,

    //Everyone without exception across all locales
    GLOBAL;

    val visibleToSource: Boolean
        get() = true

    val visibleToTarget: Boolean
        get() = SUBJECTS == this || TARGET_LOCALE == this || SUBJECT_LOCALES == this || GLOBAL == this

    val visibleToSelfLocale: Boolean
        get() = SELF_LOCALE == this || SUBJECT_LOCALES == this || GLOBAL == this

    val visibleToTargetLocale: Boolean
        get() = TARGET_LOCALE == this || SUBJECT_LOCALES == this || GLOBAL == this

    val visibleToAll: Boolean
        get() = GLOBAL == this
}