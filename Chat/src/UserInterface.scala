import java.awt.Dimension
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintStream
import java.net.Socket

import scala.swing.BorderPanel
import scala.swing.MainFrame
import scala.swing.ScrollPane
import scala.swing.TextArea
import scala.swing.TextField
import scala.swing.event.EditDone


 /* @author Artur
 */
object UserInterface {
  
  def main(args: Array[String]): Unit = {
    
    val address = "localhost"
    val port = 10001  
    val sock = new Socket(address, port)  
    val is = new BufferedReader(new InputStreamReader(sock.getInputStream()))
    val os = new PrintStream(sock.getOutputStream())
    val textArea = new TextArea
    val textField = new TextField {
      listenTo(this)
      reactions += {
        case e: EditDone =>
          if(text.nonEmpty){
            os.println(text)
            text = ""
          }
      }
    }
    
    val frame = new MainFrame{
      title = "Chat Scala"
      contents = new BorderPanel {
        layout += new ScrollPane(textArea) -> BorderPanel.Position.North
        layout += textField -> BorderPanel.Position.South
      }
      size = new Dimension(400, 600)
      centerOnScreen
    }
    
    var flag = true
    actors.Actor.actor {
      while(flag){
        if(is.ready){
          val output = is.readLine
          textArea.append(output + "\n")
        }
        Thread.sleep(200)
      }
    }
    frame.open
  }
}