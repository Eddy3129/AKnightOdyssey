package aknightodyssey.game
//tile class that act as parent class for different type of tiles
trait Tile {
  def getTileMessage: String
  def getEncounterDetails: Option[(String, String, String)]
}
//configure image, sound effect and encounter message for each different tiles
class NormalTile extends Tile {
  def getTileMessage: String = "You landed on a normal tile."
  def getEncounterDetails: Option[(String, String, String)] = None
}

class MonsterTile extends Tile {
  def getTileMessage: String = "You landed on a Monster tile."
  def getEncounterDetails: Option[(String, String, String)] = Some(("/aknightodyssey/images/Gameplay-Monster-Encounter.jpg", "/aknightodyssey/sounds/Gameplay-Monster-Encounter.wav", "Run! You have encountered the Goblin Monster!"))
}

class PowerUpTile extends Tile {
  def getTileMessage: String = "You landed on a Power Up tile."
  def getEncounterDetails: Option[(String, String, String)] = Some(("/aknightodyssey/images/Gameplay-Power-up-Encounter.jpg", "/aknightodyssey/sounds/Gameplay-Power-up-Encounter.mp3", "Wow! You have encountered the Magical Unicorn!"))
}

class SpecialEncounterTile extends Tile {
  def getTileMessage: String = "You landed on a special encounter tile."
  def getEncounterDetails: Option[(String, String, String)] = Some(("/aknightodyssey/images/Gameplay-Special-Encounter.jpg", "/aknightodyssey/sounds/Gameplay-Special-Encounter.mp3","Wow! You have discovered a secret treasure chest!"))
}
