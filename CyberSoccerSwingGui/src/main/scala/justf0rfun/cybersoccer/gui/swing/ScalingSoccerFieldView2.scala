package justf0rfun.cybersoccer.gui.swing

import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.GraphicsEnvironment
import java.awt.Image
import java.awt.Rectangle
import java.awt.RenderingHints
import scala.annotation.target.setter
import javax.swing.JComponent
import javax.swing.SwingUtilities
import justf0rfun.cybersoccer.model.SoccerField
import justf0rfun.mathematics.geometry.linear.LineSegment
import justf0rfun.cybersoccer.model.MatchState
import justf0rfun.cybersoccer.model.MatchConfiguration

class ScalingSoccerFieldView2 (matchConfiguration: MatchConfiguration) extends JComponent {

	private var lastRenderedMatchState: MatchState = null
	private var currentMatchState: MatchState = null
	private val margin = 10
	private var fieldImage: Image = null
	//	private var matchImage: Image = null
	private var ballImage: Image = null
	private var hostPlayerImage: Image = null
	private var guestPlayerImage: Image = null
	private var transform: AffineTransform = null

	addComponentListener(new ComponentAdapter {
		override def componentResized(e: ComponentEvent) = {
			resize
		}
		override def componentShown(e: ComponentEvent) = {
			resize
		}
	})

	def update(matchState: MatchState) {

		//		SwingUtilities.invokeLater(new Runnable {
		//			override def run = {
		//				if (lastRenderedMatchState != null) {
		//					//						cleanMatchState(matchImage.getGraphics().asInstanceOf[Graphics2D], lastRenderedMatchState)
		//				}
		//				renderMatchState(matchImage.getGraphics().asInstanceOf[Graphics2D], matchState)
		//				lastRenderedMatchState = matchState
		//			}
		//		})
		if (lastRenderedMatchState != null) {
			repaintMatchState(lastRenderedMatchState)
		}
		currentMatchState = matchState
		repaintMatchState(currentMatchState)
	}

	private def repaintMatchState(matchState: MatchState) = {
		matchState.bodies.foreach(body => {
			val rectangle = new Rectangle((body.location.x - body.rangeRadius).toInt, (body.location.y - body.rangeRadius).toInt, (2 * body.rangeRadius).toInt, (2 * body.rangeRadius).toInt)
			repaint(transform.createTransformedShape(rectangle).getBounds())
		})
		val ballRectangle = new Rectangle((matchState.ball.location.x - matchState.ball.radius).toInt, (matchState.ball.location.y - matchState.ball.radius).toInt, matchState.ball.diameter.toInt, matchState.ball.diameter.toInt)
		repaint(transform.createTransformedShape(ballRectangle).getBounds())

	}

	private def resize = {
		SwingUtilities.invokeLater(new Runnable {
			override def run = {
				transform = createTransform
				fieldImage = createCompatibleImage(getWidth(), getHeight)
				fieldImage = renderField(fieldImage.getGraphics().asInstanceOf[Graphics2D], matchConfiguration.field)
				ballImage = createBallImage()
				hostPlayerImage = createPlayerImage(Color.BLUE, matchConfiguration.playerRangeRadius)
				guestPlayerImage = createPlayerImage(Color.RED, matchConfiguration.playerRangeRadius)
				//				matchImage = copyImage(fieldImage)
				lastRenderedMatchState = null
				if (currentMatchState != null) {
					repaintMatchState(currentMatchState)
				}
			}
		})
	}

	override def paintComponent(graphics: Graphics) = {
		//		println("paint")
		//		graphics.setClip(0, 0, getWidth, getHeight)
		copyClipFromImage(graphics, fieldImage)
		if (lastRenderedMatchState != null) {
			cleanMatchState(graphics.asInstanceOf[Graphics2D], lastRenderedMatchState)
		}
		if (currentMatchState != null && currentMatchState != lastRenderedMatchState) {
			renderMatchState(graphics.asInstanceOf[Graphics2D], currentMatchState)
			lastRenderedMatchState = currentMatchState
		}
	}

	private def renderMatchState(graphics: Graphics2D, matchState: MatchState): Unit = {
		if (matchState != null) {
			setRenderingHints(graphics)
			graphics.setTransform(transform)
			matchState.bodies.foreach(body => {
				val rectangle = new Rectangle((body.location.x - body.rangeRadius).toInt, (body.location.y - body.rangeRadius).toInt, (2 * body.rangeRadius).toInt, (2 * body.rangeRadius).toInt)
				val playerImage = if (body.team == matchState.host) hostPlayerImage else guestPlayerImage
				graphics.drawImage(playerImage, rectangle.x, rectangle.y, this)
				//				repaint(transform.createTransformedShape(rectangle).asInstanceOf[Rectangle])
			})
			val ballRectangle = new Rectangle((matchState.ball.location.x - matchState.ball.radius).toInt, (matchState.ball.location.y - matchState.ball.radius).toInt, matchState.ball.diameter.toInt, matchState.ball.diameter.toInt)
			graphics.drawImage(ballImage, ballRectangle.x, ballRectangle.y, this)
			//			repaint(transform.createTransformedShape(ballRectangle).asInstanceOf[Rectangle])
		}
	}
	private def cleanMatchState(graphics: Graphics2D, matchState: MatchState): Unit = {
		if (matchState != null) {
			setRenderingHints(graphics)
			graphics.setTransform(transform)
			matchState.bodies.foreach(body => {
				val rectangle = new Rectangle((body.location.x - body.rangeRadius).toInt, (body.location.y - body.rangeRadius).toInt, (2 * body.rangeRadius).toInt, (2 * body.rangeRadius).toInt)
				graphics.setClip(rectangle)
				copyClipFromImage(graphics, fieldImage)
				//				repaint(transform.createTransformedShape(rectangle).asInstanceOf[Rectangle])
			})
			val ballRectangle = new Rectangle((matchState.ball.location.x - matchState.ball.radius).toInt, (matchState.ball.location.y - matchState.ball.radius).toInt, matchState.ball.diameter.toInt, matchState.ball.diameter.toInt)
			graphics.setClip(ballRectangle)
			copyClipFromImage(graphics, fieldImage)
			//			repaint(transform.createTransformedShape(ballRectangle).asInstanceOf[Rectangle])
		}
	}

	//	private def retransformAndRepaint(userSpaceRectangle: Rectangle) = {
	//		val inverseTransform = transform.createInverse
	//		val deviceSpaceRectangle = inverseTransform.createTransformedShape(userSpaceRectangle)
	//		repaint(deviceSpaceRectangle)
	//	}

	private def renderField(graphics: Graphics2D, field: SoccerField) = {
		setRenderingHints(graphics)
		//background
		val backgroundColor = Color.GREEN.darker().darker()
		graphics.setColor(backgroundColor)
		graphics.fillRect(0, 0, getWidth, getHeight)
		graphics.asInstanceOf[Graphics2D].setTransform(transform)
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
		//outer lines
		//		graphics.fillRect(13,13,20,20)
		//		graphics.drawLine(margin + 0, margin + 0, margin + 20, margin + 20)
		drawLineSegment(field.upperLine, graphics)
		drawLineSegment(field.bottomLine, graphics)
		drawLineSegment(field.leftLine, graphics)
		drawLineSegment(field.rightLine, graphics)
		drawLineSegment(field.middleLine, graphics)

		//		//		graphics.drawRect(minimumMargin, minimumMargin, field.width.toInt, field.height.toInt)
		//		//		graphics.drawLine(fieldImage.getWidth / 2, minimumMargin, fieldImage.getWidth / 2, fieldImage.getHeight - minimumMargin)
		//		graphics.drawOval(fieldImage.getWidth / 2 - middleCircleRadius, fieldImage.getHeight / 2 - middleCircleRadius, middleCircleRadius * 2, middleCircleRadius * 2)
		//		//		graphics.fillOval(fieldImage.getWidth / 2 - matchConfiguration., fieldImage.getHeight / 2 - middleCircleRadius, middleCircleRadius * 2, middleCircleRadius * 2)
		//		//goals
		//		graphics.setColor(Color.RED)
		//		graphics.drawLine(0, minimumMargin + field.leftGoal.leftPostLocation.y.toInt, minimumMargin + field.leftGoal.leftPostLocation.x.toInt, minimumMargin + field.leftGoal.leftPostLocation.y.toInt)
		//		graphics.drawLine(0, minimumMargin + field.leftGoal.rightPostLocation.y.toInt, minimumMargin + field.leftGoal.rightPostLocation.x.toInt, minimumMargin + field.leftGoal.rightPostLocation.y.toInt)
		//		graphics.drawLine(fieldImage.getWidth, minimumMargin + field.rightGoal.leftPostLocation.y.toInt, fieldImage.getWidth - (minimumMargin + field.rightGoal.leftPostLocation.x.toInt), minimumMargin + field.rightGoal.leftPostLocation.y.toInt)
		//		graphics.drawLine(fieldImage.getWidth, minimumMargin + field.rightGoal.rightPostLocation.y.toInt, fieldImage.getWidth - (minimumMargin + field.rightGoal.rightPostLocation.x.toInt), minimumMargin + field.rightGoal.rightPostLocation.y.toInt)
		graphics.dispose()
		fieldImage
	}

	private def drawLineSegment(lineSegment: LineSegment, graphics: Graphics) = {
		graphics.drawLine(lineSegment.pointA.x.toInt, lineSegment.pointA.y.toInt, lineSegment.pointB.x.toInt, lineSegment.pointB.y.toInt)
	}

	private def setRenderingHints(graphics: Graphics) = {
		graphics.asInstanceOf[Graphics2D].setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
		graphics.asInstanceOf[Graphics2D].setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
	}

	private def createPlayerImage(color: Color, radius: Double) = {
		val image = new BufferedImage((radius * 2 * scaleFactor).toInt, (radius * 2 * scaleFactor).toInt, BufferedImage.TYPE_INT_ARGB)
		val graphics = image.getGraphics()
		setRenderingHints(graphics)
		graphics.setColor(color)
		graphics.fillOval(0, 0, image.getWidth(), image.getHeight())
		graphics.dispose()
		image
	}

	private def createBallImage() = {
		val radius = matchConfiguration.ballRadius
		val image = new BufferedImage((radius * 2 * scaleFactor).toInt, (radius * 2 * scaleFactor).toInt, BufferedImage.TYPE_INT_ARGB)
		val graphics = image.getGraphics()
		setRenderingHints(graphics)
		graphics.setColor(Color.BLACK)
		val ballDiameter = 2 * matchConfiguration.ballRadius
		graphics.fillOval(0, 0, ballDiameter.toInt, ballDiameter.toInt)
		graphics.dispose()
		image
	}

	private def createCompatibleImage: Image = {
		createCompatibleImage(getWidth, getHeight)
	}

	private def createCompatibleImage(width: Int, height: Int) = {
		GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width, height)
	}

	private def copyImage(sourceImage: Image): Image = {
		copyImage(sourceImage, createCompatibleImage(sourceImage.getWidth(this), sourceImage.getHeight(this)))
	}

	private def copyImage(sourceImage: Image, targetImage: Image): Image = {
		//		sourceImage.copyData(targetImage.getRaster())
		val graphics = targetImage.getGraphics()
		setRenderingHints(graphics)
		graphics.drawImage(sourceImage, 0, 0, this)
		targetImage
	}

	private def copyClipFromImage(graphics: Graphics, image: Image) = {
		val clip = graphics.getClipBounds()
		graphics.drawImage(image, clip.x, clip.y, clip.x + clip.width, clip.y + clip.height, clip.x, clip.y, clip.x + clip.width, clip.y + clip.height, this)
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