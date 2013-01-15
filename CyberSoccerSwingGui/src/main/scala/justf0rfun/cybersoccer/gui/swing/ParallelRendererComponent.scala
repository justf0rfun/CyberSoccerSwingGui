package justf0rfun.cybersoccer.gui.swing
import java.awt.image.BufferedImage
import java.awt.Graphics
import akka.actor.actorRef2Scala
import akka.actor.ActorRef
import javax.swing.JComponent
import java.awt.Image

class ParallelRendererComponent extends JComponent {

	var image: Image = null
	
	override def paintComponent(graphics: Graphics) = {
		if (image != null) {
			val clipArea = graphics.getClipBounds()
			var scaleFactorX: Double = getWidth().toDouble / image.getWidth(null).toDouble
//			var scaleFactorY: Double = getHeight().toDouble / image.getHeight(null).toDouble
			var scaleFactorY: Double = scaleFactorX
//			println(scaleFactorX, scaleFactorY)
			graphics.drawImage(image, (clipArea.x * scaleFactorX).toInt, (clipArea.y * scaleFactorY).toInt, (clipArea.width * scaleFactorX).toInt, (clipArea.height * scaleFactorY).toInt, getBackground(), null)
		} else super.paintComponent(graphics)
	}

}