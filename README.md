# ShrugItOff
Minecraft mod that gives Toughness more purpose. Any normal physical damage has a chance to be "shrugged off", based on the toughness of the target.
Current formula is:

    CHANCE = 10% * Toughness/Damage

So for example, with 8 toughness (what a full set of diamond armor provides), and a Skeleton arrow coming in (often 4 damage), the chance would be:
    
    10% * 8/4 = 10% * 2 = 20%
    
If the damage is successfully shrugged off, a "clink!" sound will play.

Built for Minecraft 1.12.2. 
~Only works with vanilla damage types~.
~Currently no config options exist for this mod~.

## Additional Features with respect to the original mod
Configuration!

* Option to only work on a whitelist of damage types
* Option to work on all damage types except a blacklist
* Option to disable sound
* Option to blacklist items (if the attacker has equipped those items in the main hand, then the mod will ignore the attack)
* Option to log hits to chat and log (for debugging and modpack makers)
* Option to specify a list of small damage sources. Those damage sources will never play the sound.

## Update 0.1.0
* Removed some unused classes, refactored code
* Added a new function, based on sigmoid
* Added a config option to fine tune the new formula
* Added a config option for the base chance to the old formula
* Added a config option for the cap value to the old formula

## Update 0.1.1
* Added config to ignore Absolute and Unblockable attributes
* Added config to log Logic to chat
* A little code refactor and polishing
