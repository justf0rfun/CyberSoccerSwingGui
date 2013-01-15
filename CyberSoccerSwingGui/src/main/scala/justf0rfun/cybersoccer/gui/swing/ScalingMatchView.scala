package justf0rfun.cybersoccer.gui.swing

import java.awt.geom.AffineTransform
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import scala.annotation.target.setter
import javax.swing.JComponent
import justf0rfun.cybersoccer.model.MatchConfiguration
import justf0rfun.cybersoccer.model.MatchState
import justf0rfun.cybersoccer.model.SoccerField
import justf0rfun.mathematics.geometry.Angle

class ScalingMatchView(matchConfiguration: MatchConfiguration, hostColor: Color, guestColor: Color, margin: Int) extends JComponent {

	private var currentMatchState: MatchState = null

	def update(matchState: MatchState) {
		currentMatchState = matchState
		repaint()
	}

	override def paintComponent(graphics: Graphics) = {
		if (currentMatchState != null) {
			setRenderingHints(graphics)
			graphics.asInstanceOf[Graphics2D].setTransform(createTransform)
			currentMatchState.bodies.foreach(body => {
				val bodyColor = if (body.team == currentMatchState.host) hostColor else guestColor
				graphics.setColor(bodyColor)
				graphics.fillOval((body.location.x - body.rangeRadius).toInt, (body.location.y - body.rangeRadius).toInt, (2 * body.rangeRadius).toInt, (2 * body.rangeRadius).toInt)
				graphics.setColor(Color.BLACK)
				graphics.fillArc((body.location.x - body.rangeRadius).toInt, (body.location.y - body.rangeRadius).toInt, (2 * body.rangeRadius).toInt, (2 * body.rangeRadius).toInt, (Angle.degree90 - body.move.vector.angle).degree.toInt, 180.toInt)
				graphics.drawLine(body.location.x.toInt, body.location.y.toInt, (body.move.vector.unitVector * 20d).point(body.location).x.toInt, (body.move.vector.unitVector * 20d).point(body.location).y.toInt)
			})
			graphics.setColor(Color.BLACK)
			graphics.fillOval((currentMatchState.ball.location.x - currentMatchState.ball.radius).toInt, (currentMatchState.ball.location.y - currentMatchState.ball.radius).toInt, currentMatchState.ball.diameter.toInt, currentMatchState.ball.diameter.toInt)
		}
	}

	private def setRenderingHints(graphics: Graphics) = {
		graphics.asInstanceOf[Graphics2D].setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
		graphics.asInstanceOf[Graphics2D].setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
	}

	private def fieldFormat = matchConfiguration.field.width / matchConfiguration.field.height

	private def componentFormat = (getWidth.toDouble - 2 * margin) / (getHeight.toDouble - 2 * margin)

	private def scaleFactor: Double = if (fieldFormat <= componentFormat) (getHeight.toDouble - 2 * margin) / matchConfiguration.field.height else (getWidth.toDouble - 2 * margin) / matchConfiguration.field.width

	private def createTransform: AffineTransform = {
		val translate = AffineTransform.getTranslateInstance(getWidth / 2, getHeight / 2)
		val scale = AffineTransform.getScaleInstance(scaleFactor, scaleFactor)
		translate.concatenate(scale)
		translate
	}

}