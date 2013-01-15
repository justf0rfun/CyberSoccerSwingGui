package justf0rfun.cybersoccer.gui.swing
import javax.swing.JComponent
import justf0rfun.cybersoccer.model.Body
import justf0rfun.cybersoccer.model.MatchConfiguration
import justf0rfun.cybersoccer.model.MatchState
import justf0rfun.cybersoccer.model.SoccerField
import java.awt.Rectangle
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.awt.Graphics2D
import java.awt.Color
import akka.actor.Actor
import java.awt.RenderingHints

class SoccerFieldView(matchConfiguration: MatchConfiguration) extends JComponent with Actor {

	private val field = matchConfiguration.field
	private val fieldImage: BufferedImage = drawField(field)
	private val matchImage: BufferedImage = new BufferedImage(fieldImage.getWidth, fieldImage.getHeight, BufferedImage.TYPE_INT_RGB)
	private val margin: Int = 10
	private var lastPaintSize = getSize()
	private val ballPlayerImage = createBallImage
	private val hostPlayerImage = createPlayerImage(Color.BLUE, matchConfiguration.playerRangeRadius)
	private val guestPlayerImage = createPlayerImage(Color.RED, matchConfiguration.playerRangeRadius)
	//TODO Will this ever be accessed before it is initialized?
	private var matchState: MatchState = null

	def update(newMatchState: MatchState) = {
		matchState = newMatchState
	}
	
	override def receive = {
		case 'renderNextMatchState => renderMatchState(matchState)
	}

	private def renderMatchState(matchState: MatchState) = {
		//TODO resolve possible concurrency to EDT
		if (lastPaintSize != getSize()) {
			lastPaintSize = getSize()
			val graphics = matchImage.getGraphics()
			graphics.drawImage(fieldImage, 0, 0, null)
			graphics.dispose()
			repaint()
		} else {
			val graphics = matchImage.getGraphics()
			matchState.bodies.foreach(body => graphics.drawImage(if (body.team == matchState.host) hostPlayerImage else guestPlayerImage, (body.location.x - body.rangeRadius).toInt, (body.location.y - body.rangeRadius).toInt, null))
			graphics.setColor(Color.BLACK)
			graphics.fillOval(margin + matchState.ball.location.x.toInt, margin + matchState.ball.location.y.toInt, matchState.ball.radius.toInt, matchState.ball.radius.toInt)
			graphics.dispose()
			val ballDiameter = (matchState.ball.radius * 2).toInt
			val scaleFactorX = getWidth().toDouble / matchImage.getWidth().toDouble
			val scaleFactorY = getHeight().toDouble / matchImage.getHeight().toDouble
			val rectangle = new Rectangle((matchState.ball.location.x - matchState.ball.radius).toInt, (matchState.ball.location.y - matchState.ball.radius).toInt, ballDiameter, ballDiameter)
			val scaledRectangle = scale(rectangle, scaleFactorX, scaleFactorY)
//			println("trigger: " + rectangle)
//			println("trigger: " + scaledRectangle)
			//			repaint(scaledRectangle)
			repaint()
		}
	}

	override def paintComponent(graphics: Graphics) = {
		graphics.asInstanceOf[Graphics2D].setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
		graphics.asInstanceOf[Graphics2D].setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
		val tmp = matchImage
		val scaleFactorX = getWidth().toDouble / tmp.getWidth().toDouble
		val scaleFactorY = getHeight().toDouble / tmp.getHeight().toDouble
		val destinationRectangle = graphics.getClipBounds()
		val sourceRectangle = scale(destinationRectangle, 1 / scaleFactorX, 1 / scaleFactorY)
		println("paint: " + destinationRectangle)
		println("paint: " + sourceRectangle)
		//		println("--------------------------------------------")

		//		matchImage.getGraphics().asInstanceOf[Graphics2D].scale(scaleFactor, scaleFactor)
		graphics.drawImage(tmp, 0, 0, getWidth, getHeight, 0, 0, 0, 0, null)
		graphics.dispose()
		self ! 'renderNextMatchState
	}

	private def scale(rectangle: Rectangle, scaleFactorX: Double, scaleFactorY: Double) = {
		new Rectangle((rectangle.getX() * scaleFactorX).toInt, (rectangle.getY() * scaleFactorY).toInt, (rectangle.getWidth() * scaleFactorX).toInt, (rectangle.getHeight() * scaleFactorY).toInt)
	}

	private def drawField(field: SoccerField) = {
		val fieldImage = new BufferedImage(field.width.toInt + 2 * margin, field.height.toInt + 2 * margin, BufferedImage.TYPE_INT_RGB)
		val graphics = fieldImage.getGraphics()
		//background
		val backgroundColor = Color.GREEN.darker().darker()
		graphics.setColor(backgroundColor)
		graphics.fillRect(0, 0, fieldImage.getWidth, fieldImage.getHeight)
		//stripes
		//		val numberOfStripes = 10
		//		val stripeWidth: Int = (field.width / numberOfStripes).toInt
		//		for (i <- 0 until numberOfStripes) {
		//			graphics.setColor(backgroundColor.brighter())
		//			if (i % 2 == 0) {
		//				graphics.fillRect(margin + i * stripeWidth, margin, margin + i * stripeWidth + stripeWidth, margin + field.height.toInt)
		//			}
		//		}
		//fieldlines
		val middleCircleRadius: Int = 10
		graphics.setColor(Color.WHITE)
		graphics.drawRect(margin, margin, field.width.toInt, field.height.toInt)
		graphics.drawLine(fieldImage.getWidth / 2, margin, fieldImage.getWidth / 2, fieldImage.getHeight - margin)
		graphics.drawOval(fieldImage.getWidth / 2 - middleCircleRadius, fieldImage.getHeight / 2 - middleCircleRadius, middleCircleRadius * 2, middleCircleRadius * 2)
		//		graphics.fillOval(fieldImage.getWidth / 2 - matchConfiguration., fieldImage.getHeight / 2 - middleCircleRadius, middleCircleRadius * 2, middleCircleRadius * 2)
		//goals
		graphics.setColor(Color.RED)
		graphics.drawLine(0, margin + field.leftGoal.leftPostLocation.y.toInt, margin + field.leftGoal.leftPostLocation.x.toInt, margin + field.leftGoal.leftPostLocation.y.toInt)
		graphics.drawLine(0, margin + field.leftGoal.rightPostLocation.y.toInt, margin + field.leftGoal.rightPostLocation.x.toInt, margin + field.leftGoal.rightPostLocation.y.toInt)
		graphics.drawLine(fieldImage.getWidth, margin + field.rightGoal.leftPostLocation.y.toInt, fieldImage.getWidth - (margin + field.rightGoal.leftPostLocation.x.toInt), margin + field.rightGoal.leftPostLocation.y.toInt)
		graphics.drawLine(fieldImage.getWidth, margin + field.rightGoal.rightPostLocation.y.toInt, fieldImage.getWidth - (margin + field.rightGoal.rightPostLocation.x.toInt), margin + field.rightGoal.rightPostLocation.y.toInt)
		graphics.dispose()
		fieldImage
	}

	private def createPlayerImage(color: Color, radius: Double) = {
		val image = new BufferedImage((radius * 2).toInt, (radius * 2).toInt, BufferedImage.TYPE_INT_ARGB)
		val graphics = image.getGraphics()
		graphics.setColor(color)
		graphics.fillOval(0, 0, image.getWidth(), image.getHeight())
		graphics.dispose()
		image
	}

	private def createBallImage() = {
		val radius = matchConfiguration.ballRadius
		val image = new BufferedImage((radius * 2).toInt, (radius * 2).toInt, BufferedImage.TYPE_INT_ARGB)
		val graphics = image.getGraphics()
		graphics.setColor(Color.BLACK)
		graphics.fillOval(0, 0, image.getWidth(), image.getHeight())
		graphics.dispose()
		image
	}

}