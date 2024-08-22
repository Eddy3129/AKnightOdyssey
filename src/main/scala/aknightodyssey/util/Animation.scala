package aknightodyssey.util

import scalafx.animation.{FadeTransition, KeyFrame, Timeline}
import scalafx.scene.control.Label
import scalafx.scene.media.AudioClip
import scalafx.util.Duration

abstract class LabelAnimation[T <: Label](val label: T, val duration: Int) {

  def play(texts: List[String])(onFinish: => Unit): Unit = {
    animateTexts(texts, onFinish)
  }

  def animateTexts(texts: List[String], onFinish: => Unit): Unit = {
    label.text = ""
    label.visible = true
    label.opacity = 1.0

    val typingSound = new AudioClip(getClass.getResource("/aknightodyssey/sounds/Gameplay-Effect-Typing.wav").toString)

    def animateNextText(remainingTexts: List[String]): Unit = {
      remainingTexts match {
        case text :: rest =>
          val typingTimeline = new Timeline {
            keyFrames = (0 until text.length).map { i =>
              KeyFrame(Duration(duration * i), onFinished = _ => {
                label.text.value += text.charAt(i)
                typingSound.play()
              })
            }
          }
          typingTimeline.setOnFinished { _ =>
            if (rest.nonEmpty) {
              // If there are more texts, pause before showing the next one
              new Timeline {
                keyFrames = Seq(KeyFrame(Duration(1000), onFinished = _ => {
                  label.text.value += "\n"
                  animateNextText(rest)
                }))
              }.play()
            } else {
              // If this was the last text, fade out
              fadeOutLabel(onFinish)
            }
          }
          typingTimeline.play()
        case Nil =>
          // No more texts, finish the animation
          onFinish
      }
    }

    animateNextText(texts)
  }

  private def fadeOutLabel(onFinish: => Unit): Unit = {
    val fadeOutTransition = new FadeTransition(Duration(3000), label) {
      fromValue = 1.0
      toValue = 0.5
    }
    fadeOutTransition.setOnFinished(_ => onFinish)
    fadeOutTransition.play()
  }
}

class MessageAnimation(label: Label, duration: Int) extends LabelAnimation(label, duration) {
  override def play(texts: List[String])(onFinish: => Unit): Unit = {
    animateTexts(texts, onFinish)
  }
}

class EffectAnimation(label: Label, duration: Int) extends LabelAnimation(label, duration) {
  override def play(texts: List[String])(onFinish: => Unit): Unit = {
    animateTexts(texts, onFinish)
  }
}