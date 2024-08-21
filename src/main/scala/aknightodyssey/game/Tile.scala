package aknightodyssey.game

trait Tile {
  def getMessage: String
  def getEncounterDetails: Option[(String, String, String)]
}

class NormalTile extends Tile {
  def getMessage: String = "You landed on a normal tile."
  def getEncounterDetails: Option[(String, String, String)] = None
}

class MonsterTile extends Tile {
  def getMessage: String = "You landed on a Monster tile."
  def getEncounterDetails: Option[(String, String, String)] = Some(("/aknightodyssey/images/Gameplay-Monster-Encounter.jpg", "/aknightodyssey/sounds/Gameplay-Monster-Encounter.wav", "You have encountered the Goblin Monster"))
}

class PowerUpTile extends Tile {
  def getMessage: String = "You landed on a Power Up tile."
  def getEncounterDetails: Option[(String, String, String)] = Some(("/aknightodyssey/images/Gameplay-Power-up-Encounter.jpg", "/aknightodyssey/sounds/Gameplay-Power-up-Encounter.mp3", "You have encountered the Magical Unicorn!"))
}

class SpecialEncounterTile extends Tile {
  def getMessage: String = "You landed on a special encounter tile."
  def getEncounterDetails: Option[(String, String, String)] = Some(("/aknightodyssey/images/Gameplay-Special-Encounter.jpg", "/aknightodyssey/sounds/Gameplay-Special-Encounter.mp3","You have discovered a secret treasure chest!"))
}
