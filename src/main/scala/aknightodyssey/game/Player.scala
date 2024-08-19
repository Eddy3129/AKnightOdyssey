package aknightodyssey.game

class Player(var position: Int) {
  private var powerBoost: Boolean = false
  private var debuffTurns: Int = 0

  def move(steps: Int): Unit = {
    position += steps
  }

  def applyPowerBoost(): Unit = {
    powerBoost = true
  }

  def applyDebuff(): Unit = {
    debuffTurns = 3
  }

  def hasPowerBoost: Boolean = powerBoost

  def isDebuffed: Boolean = debuffTurns > 0

  def decrementDebuff(): Unit = {
    if (debuffTurns > 0) debuffTurns -= 1
  }

  def resetPowerBoost(): Unit = {
    powerBoost = false
  }
}