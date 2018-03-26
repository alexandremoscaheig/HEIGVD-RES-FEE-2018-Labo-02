package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.net.Socket;
import java.nio.CharBuffer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 * 
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {


  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  private Socket socket;
  private InputStreamReader input;
  private OutputStreamWriter output;
  private InfoCommandResponse info;


  @Override
  public void connect(String server, int port) throws IOException {
    socket = new Socket(server, port);
    this.input = new InputStreamReader(new BufferedInputStream(socket.getInputStream()));
    this.output = new OutputStreamWriter(new BufferedOutputStream(socket.getOutputStream()));
    System.out.println(this.readBuffer());
  }

  @Override
  public void disconnect() throws IOException {
    socket.close();
  }

  @Override
  public boolean isConnected() {
    return socket != null && socket.isConnected();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    this.writeCmd(RouletteV1Protocol.CMD_LOAD);
    output.write(fullname);
    this.writeCmd(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    output.flush();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
      this.writeCmd(RouletteV1Protocol.CMD_LOAD);
    for(Student s : students)
      output.write(s.getFullname());
      this.writeCmd(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    output.flush();

  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    this.writeCmd(RouletteV1Protocol.CMD_RANDOM);
    this.output.flush();

    RandomCommandResponse rcr = JsonObjectMapper.parseJson(this.readBuffer(), RandomCommandResponse.class);
    if(!rcr.getError().isEmpty())
      throw new EmptyStoreException();
    return new Student(rcr.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    this.refreshInfos();
    return this.info.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    this.refreshInfos();
    return this.info.getProtocolVersion();
  }

  private void writeCmd(String cmd) throws IOException{
      this.output.write(cmd + "\n");
  }

  private void refreshInfos() throws IOException{
      this.writeCmd(RouletteV1Protocol.CMD_INFO);
    this.output.flush();

    this.info = JsonObjectMapper.parseJson(this.readBuffer(), InfoCommandResponse.class);
  }


  private String readBuffer() throws IOException{

    char[] buffer = new char[255];
    int i;
    StringBuilder result = new StringBuilder();
    while((i = this.input.read(buffer)) > 0){
      result.append(new String(buffer));
    }
    return result.toString();
  }
}
