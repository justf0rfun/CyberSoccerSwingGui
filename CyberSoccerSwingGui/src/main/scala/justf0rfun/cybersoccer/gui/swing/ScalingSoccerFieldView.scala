package justf0rfun.cybersoccer.gui.swing
import java.awt.event.ComponentAdapter
import java.awt.image.BufferedImage
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.GraphicsEnvironment
import java.awt.Rectangle
import java.awt.RenderingHints
import javax.swing.JComponent
import justf0rfun.cybersoccer.model.MatchConfiguration
import justf0rfun.cybersoccer.model.MatchState
import justf0rfun.cybersoccer.model.SoccerField
import justf0rfun.mathematics.geometry.linear.LineSegment
import justf0rfun.mathematics.geometry.Point
import java.awt.Image
import java.awt.geom.AffineTransform
import javax.swing.SwingConstants
import scala.annotation.target.setter
import java.awt.event.ComponentEvent
import javax.swing.SwingUtilities
import justf0rfun.mathematics.geometry.circular.Circle

class ScalingSoccerFieldView(field: SoccerField, val margin: Int) extends JComponent {

	private var fieldImage: Image = null
	//	setBackground(new Color(68,123,21))

	override def paintComponent(graphics: Graphics) = {
		renderField(graphics.asInstanceOf[Graphics2D], field)
	}

	private def renderField(graphics: Graphics2D, field: SoccerField) = {
		super.paintComponent(graphics)
		setRenderingHints(graphics)
		//background
		val backgroundColor = new Color(68, 123, 21)
		//		val backgroundColor = Color.GREEN.darker()
		graphics.setColor(backgroundColor)
		graphics.fillRect(0, 0, getWidth, getHeight)
		graphics.asInstanceOf[Graphics2D].setTransform(createTransform)
		//stripes
		graphics.setColor(new Color(97, 158, 54))
		graphics.fillRect(field.upperLeft.x.toInt, field.upperLeft.y.toInt, field.width.toInt, field.height.toInt)
		val stripeWidth: Double = .1 * field.width
		val numberOfStripes: Int = (field.width / stripeWidth).toInt
		graphics.setColor(new Color(87, 148, 29))
		for (i <- 0 until numberOfStripes / 2) {
			if (i % 2 == 0) {
				graphics.fillRect((field.kickOffPoint.location.x - (-stripeWidth / 2 + stripeWidth + i * stripeWidth)).toInt, (field.kickOffPoint.location.y - field.height / 2).toInt, stripeWidth.toInt, field.height.toInt)
				graphics.fillRect((field.kickOffPoint.location.x + (-stripeWidth / 2 + i * stripeWidth)).toInt, (field.kickOffPoint.location.y - field.height / 2).toInt, stripeWidth.toInt, field.height.toInt)
			}
		}
		//fieldlines
		val middleCircleRadius: Int = 10
		graphics.setColor(Color.WHITE)
		//outer lines
		drawLineSegment(field.upperLine, graphics)
		drawLineSegment(field.bottomLine, graphics)
		drawLineSegment(field.leftLine, graphics)
		drawLineSegment(field.rightLine, graphics)
		drawLineSegment(field.middleLine, graphics)
		fillCircle(field.kickOffPoint, graphics)
		drawCircle(field.middleCircle, graphics)
		val width2: Int = 100
		val height2: Int = 200
		val rectangle = new Rectangle(field.leftGoal.leftPostLocation.x.toInt, (field.leftGoal.goalLine.middle.y - height2 * .5).toInt, field.leftGoal.leftPostLocation.x.toInt + width2, (field.leftGoal.goalLine.middle.y + height2 * .5).toInt)
//		println(rectangle)
		graphics.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height)

		//goals
		graphics.setColor(Color.BLACK)
		graphics.drawLine(field.leftGoal.leftPostLocation.x.toInt - margin, field.leftGoal.leftPostLocation.y.toInt, field.leftGoal.leftPostLocation.x.toInt, field.leftGoal.leftPostLocation.y.toInt)
		graphics.drawLine(field.leftGoal.rightPostLocation.x.toInt - margin, field.leftGoal.rightPostLocation.y.toInt, field.leftGoal.rightPostLocation.x.toInt, field.leftGoal.rightPostLocation.y.toInt)
		graphics.drawLine(field.rightGoal.leftPostLocation.x.toInt, field.rightGoal.leftPostLocation.y.toInt, field.rightGoal.leftPostLocation.x.toInt + margin, field.rightGoal.leftPostLocation.y.toInt)
		graphics.drawLine(field.rightGoal.rightPostLocation.x.toInt, field.rightGoal.rightPostLocation.y.toInt, field.rightGoal.rightPostLocation.x.toInt + margin, field.rightGoal.rightPostLocation.y.toInt)
		graphics.dispose()
	}

	private def drawLineSegment(lineSegment: LineSegment, graphics: Graphics) = {
		graphics.drawLine(lineSegment.pointA.x.toInt, lineSegment.pointA.y.toInt, lineSegment.pointB.x.toInt, lineSegment.pointB.y.toInt)
	}

	private def drawCircle(circle: Circle, graphics: Graphics) = {
		graphics.drawOval((circle.location.x - circle.radius).toInt, (circle.location.y - circle.radius).toInt, circle.diameter.toInt, circle.diameter.toInt)
	}

	private def fillCircle(circle: Circle, graphics: Graphics) = {
		graphics.fillOval((circle.location.x - circle.radius).toInt, (circle.location.y - circle.radius).toInt, circle.diameter.toInt, circle.diameter.toInt)
	}

	private def setRenderingHints(graphics: Graphics) = {
		graphics.asInstanceOf[Graphics2D].setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
		graphics.asInstanceOf[Graphics2D].setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
	}

	private def createCompatibleImage: Image = {
		createCompatibleImage(getWidth, getHeight)
	}

	private def createCompatibleImage(width: Int, height: Int) = {
		GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width, height)
	}

	private def copyClipFromImage(graphics: Graphics, image: Image) = {
		val clip = graphics.getClipBounds()
		graphics.drawImage(image, clip.x, clip.y, clip.x + clip.width, clip.y + clip.height, clip.x, clip.y, clip.x + clip.width, clip.y + clip.height, this)
	}

	private def fieldFormat = field.width / field.height

	private def componentFormat = (getWidth.toDouble - 2 * margin) / (getHeight.toDouble - 2 * margin)

	private def scaleFactor: Double = if (fieldFormat <= componentFormat) (getHeight.toDouble - 2 * margin) / field.height else (getWidth.toDouble - 2 * margin) / field.width

	private def createTransform: AffineTransform = {
		val translate = AffineTransform.getTranslateInstance(getWidth / 2, getHeight / 2)
		val scale = AffineTransform.getScaleInstance(scaleFactor, scaleFactor)
		translate.concatenate(scale)
		translate
	}

}