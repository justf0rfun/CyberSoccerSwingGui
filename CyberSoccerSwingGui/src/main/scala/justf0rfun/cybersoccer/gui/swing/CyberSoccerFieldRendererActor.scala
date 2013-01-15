//package justf0rfun.cybersoccer.gui.swing
//import java.awt.image.BufferedImage
//import java.awt.Color
//import java.awt.Graphics
//import java.awt.Graphics2D
//import java.awt.GraphicsEnvironment
//import java.awt.Rectangle
//import java.awt.RenderingHints
//import akka.actor.actorRef2Scala
//import akka.actor.Actor
//import justf0rfun.cybersoccer.model.MatchConfiguration
//import justf0rfun.cybersoccer.model.MatchState
//import justf0rfun.cybersoccer.model.SoccerField
//import akka.util.Duration
//import java.util.concurrent.TimeUnit
//import justf0rfun.cybersoccer.controller.NextStep
//
//class CyberSoccerFieldRendererActor(matchConfiguration: MatchConfiguration) extends Actor {
//
//	private val fieldImage: BufferedImage = drawField(matchConfiguration.field)
//	private val margin: Int = 10
//	private val ballImage = createBallImage
//	private val hostPlayerImage = createPlayerImage(Color.BLUE, matchConfiguration.playerRangeRadius)
//	private val guestPlayerImage = createPlayerImage(Color.RED, matchConfiguration.playerRangeRadius)
//	//TODO Will this ever be accessed before it is initialized?
//	private var matchState: MatchState = null
////	private var preparedRendering = new ParallelRenderingProtocol.DisplayPreparedRendering(copyImage(fieldImage), List(new Rectangle(0, 0, fieldImage.getWidth(), fieldImage.getHeight())))
//	val component = new ParallelRendererComponent
//	component.image = copyImage(fieldImage)
////	new LazyUpdateRequestingRepaintManager(self, component)
//	context.system.scheduler.schedule(Duration.Zero, Duration.create(200, TimeUnit.MILLISECONDS), self, ParallelRenderingProtocol.PrepareRendering)
//
//	private def createImage(width: Int, height: Int) = {
//		GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width, height)
//	}
//
//	private def copyImage(sourceImage: BufferedImage): BufferedImage = {
//		copyImage(sourceImage, createImage(sourceImage.getWidth, sourceImage.getHeight))
//		//		copyImage(sourceImage, new BufferedImage(sourceImage.getWidth, sourceImage.getHeight, BufferedImage.TYPE_INT_RGB))
//	}
//
//	private def copyImage(sourceImage: BufferedImage, targetImage: BufferedImage): BufferedImage = {
//		sourceImage.copyData(targetImage.getRaster())
//		targetImage
//	}
//
//	override def receive = {
//		case ParallelRenderingProtocol.PrepareRendering => renderMatchState(component.image, matchState); context.parent ! 'UpdateRequest
////		case ParallelRenderingProtocol.PrepareRendering => prepareRendering(copyImage(fieldImage), matchState); println("rendering request received")
////		case ParallelRenderingProtocol.PrepareRendering(bufferImage) => prepareRendering(bufferImage, matchState); println("rendering request received 2")
//		case ParallelRenderingProtocol.ComponentRequest => sender ! component
//		case newMatchState: MatchState => matchState = newMatchState
//	}
//
//	private def display(bufferImage: BufferedImage, updatedAreas: Iterable[Rectangle]) = {
//		component.image = bufferImage
//		updatedAreas.foreach(component.repaint(_))
//	}
////	private def prepareRendering(bufferImage: BufferedImage, matchState: MatchState) = {
////		component.image = preparedRendering.bufferImage
////		preparedRendering.updatedAreas.foreach(component.repaint(_))
////		preparedRendering = renderMatchState(bufferImage, matchState)
////		context.parent ! 'UpdateRequest
////	}
//
//	private def renderMatchState(bufferImage: BufferedImage, matchState: MatchState): Unit = {
//		//TODO resolve possible concurrency to EDT
//		var updatedAreas = List[Rectangle]()
//		if (matchState != null) {
//			val graphics = bufferImage.getGraphics()
//			setRenderingHints(graphics)
//			matchState.bodies.foreach(body => graphics.drawImage(if (body.team == matchState.host) hostPlayerImage else guestPlayerImage, (body.location.x - body.rangeRadius).toInt, (body.location.y - body.rangeRadius).toInt, null))
//			matchState.bodies.foreach(body => new Rectangle((body.location.x - body.rangeRadius).toInt, (body.location.y - body.rangeRadius).toInt) :: updatedAreas)
//			graphics.setColor(Color.BLACK)
//			graphics.drawImage(ballImage, (matchState.ball.location.x - matchState.ball.radius).toInt, (matchState.ball.location.y - matchState.ball.radius).toInt, null)
//			graphics.dispose()
//			val ballDiameter = (matchState.ball.radius * 2).toInt
//			new Rectangle((matchState.ball.location.x - matchState.ball.radius).toInt, (matchState.ball.location.y - matchState.ball.radius).toInt, ballDiameter, ballDiameter) :: updatedAreas
//		}
//		display(bufferImage, updatedAreas)
//	}
////	private def renderMatchState(bufferImage: BufferedImage, matchState: MatchState): ParallelRenderingProtocol.DisplayPreparedRendering = {
////		//TODO resolve possible concurrency to EDT
////		var updatedAreas = List[Rectangle]()
////				if (matchState != null) {
////					val graphics = bufferImage.getGraphics()
////							setRenderingHints(graphics)
////							matchState.bodies.foreach(body => graphics.drawImage(if (body.team == matchState.host) hostPlayerImage else guestPlayerImage, (body.location.x - body.rangeRadius).toInt, (body.location.y - body.rangeRadius).toInt, null))
////							matchState.bodies.foreach(body => new Rectangle((body.location.x - body.rangeRadius).toInt, (body.location.y - body.rangeRadius).toInt) :: updatedAreas)
////							graphics.setColor(Color.BLACK)
////							graphics.drawImage(ballImage, (matchState.ball.location.x - matchState.ball.radius).toInt, (matchState.ball.location.y - matchState.ball.radius).toInt, null)
////							graphics.dispose()
////							val ballDiameter = (matchState.ball.radius * 2).toInt
////							new Rectangle((matchState.ball.location.x - matchState.ball.radius).toInt, (matchState.ball.location.y - matchState.ball.radius).toInt, ballDiameter, ballDiameter) :: updatedAreas
////				}
////		new ParallelRenderingProtocol.DisplayPreparedRendering(bufferImage, updatedAreas)
////	}
//
//	private def drawField(field: SoccerField) = {
//		val fieldImage = createImage(field.width.toInt + 2 * margin, field.height.toInt + 2 * margin)
//		//		val fieldImage = new BufferedImage(field.width.toInt + 2 * margin, field.height.toInt + 2 * margin, BufferedImage.TYPE_INT_RGB)
//		val graphics = fieldImage.getGraphics()
//		setRenderingHints(graphics)
//		//background
//		val backgroundColor = Color.GREEN.darker().darker()
//		graphics.setColor(backgroundColor)
//		graphics.fillRect(0, 0, fieldImage.getWidth, fieldImage.getHeight)
//		//stripes
//		//		val numberOfStripes = 10
//		//		val stripeWidth: Int = (field.width / numberOfStripes).toInt
//		//		for (i <- 0 until numberOfStripes) {
//		//			graphics.setColor(backgroundColor.brighter())
//		//			if (i % 2 == 0) {
//		//				graphics.fillRect(margin + i * stripeWidth, margin, margin + i * stripeWidth + stripeWidth, margin + field.height.toInt)
//		//			}
//		//		}
//		//fieldlines
//		val middleCircleRadius: Int = 10
//		graphics.setColor(Color.WHITE)
//		graphics.drawRect(margin, margin, field.width.toInt, field.height.toInt)
//		graphics.drawLine(fieldImage.getWidth / 2, margin, fieldImage.getWidth / 2, fieldImage.getHeight - margin)
//		graphics.drawOval(fieldImage.getWidth / 2 - middleCircleRadius, fieldImage.getHeight / 2 - middleCircleRadius, middleCircleRadius * 2, middleCircleRadius * 2)
//		//		graphics.fillOval(fieldImage.getWidth / 2 - matchConfiguration., fieldImage.getHeight / 2 - middleCircleRadius, middleCircleRadius * 2, middleCircleRadius * 2)
//		//goals
//		graphics.setColor(Color.RED)
//		graphics.drawLine(0, margin + field.leftGoal.leftPostLocation.y.toInt, margin + field.leftGoal.leftPostLocation.x.toInt, margin + field.leftGoal.leftPostLocation.y.toInt)
//		graphics.drawLine(0, margin + field.leftGoal.rightPostLocation.y.toInt, margin + field.leftGoal.rightPostLocation.x.toInt, margin + field.leftGoal.rightPostLocation.y.toInt)
//		graphics.drawLine(fieldImage.getWidth, margin + field.rightGoal.leftPostLocation.y.toInt, fieldImage.getWidth - (margin + field.rightGoal.leftPostLocation.x.toInt), margin + field.rightGoal.leftPostLocation.y.toInt)
//		graphics.drawLine(fieldImage.getWidth, margin + field.rightGoal.rightPostLocation.y.toInt, fieldImage.getWidth - (margin + field.rightGoal.rightPostLocation.x.toInt), margin + field.rightGoal.rightPostLocation.y.toInt)
//		graphics.dispose()
//		fieldImage
//	}
//
//	private def createPlayerImage(color: Color, radius: Double) = {
//		val image = new BufferedImage((radius * 2).toInt, (radius * 2).toInt, BufferedImage.TYPE_INT_ARGB)
//		val graphics = image.getGraphics()
//		setRenderingHints(graphics)
//		graphics.setColor(color)
//		graphics.fillOval(0, 0, image.getWidth(), image.getHeight())
//		graphics.dispose()
//		image
//	}
//
//	private def createBallImage() = {
//		val radius = matchConfiguration.ballRadius
//		val image = new BufferedImage((radius * 2).toInt, (radius * 2).toInt, BufferedImage.TYPE_INT_ARGB)
//		val graphics = image.getGraphics()
//		setRenderingHints(graphics)
//		graphics.setColor(Color.BLACK)
//		graphics.fillOval(0, 0, image.getWidth(), image.getHeight())
//		graphics.dispose()
//		image
//	}
//
//	private def setRenderingHints(graphics: Graphics) = {
//		graphics.asInstanceOf[Graphics2D].setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
//		graphics.asInstanceOf[Graphics2D].setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
//	}
//
//}