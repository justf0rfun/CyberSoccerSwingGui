package justf0rfun.cybersoccer.gui.swing
import java.awt.image.BufferedImage
import java.awt.Rectangle

object ParallelRenderingProtocol {
	
	sealed trait ParallelRenderingProtocol
	case object PrepareRendering
//	case class DisplayPreparedRendering(bufferImage: BufferedImage, updatedAreas: Iterable[Rectangle])
	case object ComponentRequest

}