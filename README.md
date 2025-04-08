# NPC Engine

## Overview

The goal of this project is to create an engine that can represent non-player characters and their interactions with
player characters in a way that is consistent with the personality of each NPC.

### Engine vs Implementation Components

The engine will have everything needed for two NPCs to exist in a room and chit-chat.

- Opinions, Personality, and Mood are built into the engine
- The ability for an NPC to select a response from the registered list of available responses is built in

The implementation will determine all the specifics.

- It will determine specifically what the aspects of Opinions, Personality, and Mood are.
- It will handle the list of Intents and Effects
-

However, things like items, player controllers, attacks, stats, etc will all be part of the implementation. this is an
NPC engine, not a full game engine.

### Dependency Injection

Anything in the `engine` package should NOT include stuff for DI. I want that package to eventually be something I can
yank from this project as a standalone setup for NPC interactions. However, everything in the `implementation` package
can use it however they want.

When doing so, I don't really want to bother with lazy loading. Most all of my dependencies are going to need to be
created on startup, so lets just leverage direct dependency retrieval. See more on that in the
docs [here](https://kosi-libs.org/kodein/7.25/core/injection-retrieval.html#direct-retrieval)

## TODO List

1) Get a single PC interacting with an NPC via insults, compliments, and silence
2) Add more NPCs and add targeted interactions. NPCs should care if you diss on their person
3) Hook up a database. Should be able to store NPC controller data and player characters. No auth, no representation of
   a person's account, just game objects.
4) Create locations and a clock. NPCs should be able to move between l