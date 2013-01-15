package justf0rfun.cybersoccer.gui.swing
import javax.swing.RepaintManager
import javax.swing.JComponent
import akka.actor.ActorRef

class LazyUpdateRequestingRepaintManager(renderer: ActorRef, observedComponent: JComponent) extends RepaintManager {

	renderer ! ParallelRenderingProtocol.PrepareRendering

	RepaintManager.setCurrentManager(this)

	override def markCompletelyClean(component: JComponent) {
		super.markCompletelyClean(component)
		if (component == observedComponent) {
			renderer ! ParallelRenderingProtocol.PrepareRendering
		}
	}

}