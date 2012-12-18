package justf0rfun.cybersoccer.gui.swing
import java.awt.image.BufferedImage
import java.awt.Color
import java.awt.Graphics

import javax.swing.JPanel
import justf0rfun.cybersoccer.model.MatchState
import justf0rfun.cybersoccer.model.SoccerField

class SoccerFieldView extends JPanel {

  private var matchState: MatchState = null
  private var fieldImage: BufferedImage = null
  private var matchImage: BufferedImage = null
//  private val fieldSoccerField = new SoccerField
  
  def update(newMatchState: MatchState) = {
    matchState = newMatchState
//    matchImage.cl
  }

  def drawField(field: SoccerField, graphics: Graphics) = {
    val margin: Int = 10
    val middleCircleRadius: Int = 10
    graphics.setColor(Color.GREEN)
    graphics.fillRect(0, 0, getWidth, getHeight)
    graphics.setColor(Color.WHITE)
    graphics.fillRect(margin, margin, getWidth - margin, getHeight - margin)
    graphics.drawLine(getWidth / 2, margin, getWidth / 2, getHeight - margin)
    graphics.drawOval(getWidth / 2 - middleCircleRadius, getHeight / 2 - middleCircleRadius, middleCircleRadius * 2, middleCircleRadius * 2)
    graphics.setColor(Color.RED)
    graphics.drawLine(0, field.leftGoal.leftPostLocation.y.toInt, field.leftGoal.leftPostLocation.x.toInt, field.leftGoal.leftPostLocation.y.toInt)
    graphics.drawLine(0, field.leftGoal.rightPostLocation.y.toInt, field.leftGoal.rightPostLocation.x.toInt, field.leftGoal.rightPostLocation.y.toInt)
    graphics.drawLine(0, field.rightGoal.leftPostLocation.y.toInt, field.rightGoal.leftPostLocation.x.toInt, field.rightGoal.leftPostLocation.y.toInt)
    graphics.drawLine(0, field.rightGoal.rightPostLocation.y.toInt, field.rightGoal.rightPostLocation.x.toInt, field.rightGoal.rightPostLocation.y.toInt)
    graphics.dispose()
  }

  override def paintComponent(graphics: Graphics) = {
    graphics.fillOval(matchState.ball.location.x.toInt, matchState.ball.location.y.toInt, matchState.ball.radius.toInt, matchState.ball.radius.toInt)
    matchState.bodies.foreach(body => graphics.fillOval(body.location.x.toInt, body.location.y.toInt, body.rangeRadius.toInt, body.rangeRadius.toInt))
  }

}