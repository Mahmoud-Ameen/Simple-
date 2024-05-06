import java.io._
import java.net._
import java.util.{HashMap => JHashMap}

object SimpleWebServer {
  def main(args: Array[String]): Unit = {
    val serverSocket = new ServerSocket(8080)
    println("Server is listening on port 8080...")

    while (true) {
      val clientSocket = serverSocket.accept()
      println(s"Received request from ${clientSocket.getInetAddress}:${clientSocket.getPort}")

      val in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream))
      val out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream))

      val request = in.readLine()
      println(s"Received request: $request")

      // Parse the request to get the endpoint and query parameters
      val endpointAndParams = request.split(" ")(1).split("\\?") // Assuming the first word after the method is the endpoint
      val endpoint = endpointAndParams(0)
      val queryParams = if (endpointAndParams.length > 1) parseQueryParams(endpointAndParams(1)) else new JHashMap[String, String]()

      // Delegate response generation to functions based on the endpoint
      val response = endpoint match {
        case "/hello" => generateHelloResponse()
        case "/greet" => generateGreetResponse(queryParams.getOrDefault("name", "Stranger"))
        case _ => generateNotFoundResponse()
      }

      out.write(response)
      out.flush()
      println("Response sent.")

      clientSocket.close()
    }
  }

  private def parseQueryParams(queryString: String): JHashMap[String, String] = {
    val params = new JHashMap[String, String]()
    queryString.split("&").foreach { param =>
      val keyValue = param.split("=")
      if (keyValue.length == 2) {
        params.put(keyValue(0), keyValue(1))
      }
    }
    params
  }

  private def generateHelloResponse(): String = {
    "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\nHello, World!"
  }

  private def generateGreetResponse(name: String): String = {
    s"HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\nHi from Server, $name!"
  }

  private def generateNotFoundResponse(): String = {
    "HTTP/1.1 404 Not Found\r\nContent-Type: text/plain\r\n\r\nEndpoint not found"
  }
}
