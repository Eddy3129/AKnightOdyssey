package aknightodyssey.game

abstract class Tiles {
  def encounter(player: Player): String
}

class MonsterTile extends Tiles {
  override def encounter(player: Player): String = {
    player.move(-1)
    "Oh no! A monster appeared. Move 1 step back."
  }
}

class NormalTile extends Tiles {
  override def encounter(player: Player): String = "You landed on a normal tile."
}

class PowerUpTile extends Tiles {
  override def encounter(player: Player): String = {
    player.move(2)
    "You found a power-up! Move 2 steps forward."
  }
}

class SpecialEncounterTile extends Tiles {
  override def encounter(player: Player): String = "You've triggered a special encounter!"
}
