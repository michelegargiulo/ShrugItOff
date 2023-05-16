# ShrugItOff
Minecraft mod that gives Toughness more purpose. Any normal physcial damage has a chance to be "shrugged off", based on the toughness of the target.
Current formula is:

    CHANCE = 10% * Toughness/Damage

So for example, with 8 toughness (what a full set of diamond armor provides), and a Skeleton arrow coming in (often 4 damage), the chance would be:
    10% * 8/4
    10% * 2
    = 20%
If the damage is successfully shrugged off, a "clink!" sound will play.

Built for Minecraft 1.12.2. Only works with vanilla damage types.
Currently no config options exist for this mod.
