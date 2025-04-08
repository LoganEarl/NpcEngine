# Intents and Effects

Entities interact with the world through the use of an Intent. Each intent represents just that, their intent to act in
a particular way. However, they may not be successful. An intent may require a contested roll based on stats, it may
require some specific item, etc. In addition, they may be interrupted or delayed in some way. Intents are processed
immediately as the Entity creates them, however intent in of itself does not alter game state in any way. It is simply a
mechanism for Entities to express how they would like to interact with the world.

Effects, are what actually happens as a result of an intent. Effects are ordered, and are processed in a deterministic
way will take into effect each Entity's relative alacrity and the speed each intent can be performed. Things like
contested rolls and business logic all happen in an Effect. This includes things like state modifications. 