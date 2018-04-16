package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 * @author Aebischer Lenny
 * @author Mosca Alexandre
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  private static final Logger LOG = Logger.getLogger(RouletteV2ClientImpl.class.getName());


  private int nbrStudentsAdded = 0;
  private int nbrCommandsSend = 0;
  private boolean success = false;

  @Override
  public void clearDataStore() throws IOException {
    writeCmd(RouletteV2Protocol.CMD_CLEAR);
    output.flush();
    input.readLine();
    ++nbrCommandsSend;
  }

  @Override
  public List<Student> listStudents() throws IOException {
    writeCmd(RouletteV2Protocol.CMD_LIST);
    output.flush();
    StudentsList studentList = JsonObjectMapper.parseJson(input.readLine(), StudentsList.class);
    ++nbrCommandsSend;
    return studentList.getStudents();
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    writeCmd(RouletteV2Protocol.CMD_LOAD);
    output.flush();
    input.readLine();
    writeCmd(fullname);
    output.flush();
    writeCmd(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    output.flush();
    LoadCommandResponse cmdResponse = JsonObjectMapper.parseJson(input.readLine(), LoadCommandResponse.class);
    success = cmdResponse.getStatus().equals(RouletteV2Protocol.SUCCESS);
    nbrStudentsAdded = cmdResponse.getNumberOfNewStudents();
    ++nbrCommandsSend;
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    try {
      writeCmd(RouletteV2Protocol.CMD_LOAD);
      output.flush();
      input.readLine();

      for (Student student : students) {
        writeCmd(student.getFullname());
        output.flush();
      }

      writeCmd(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      output.flush();

      LoadCommandResponse cmdResponse = JsonObjectMapper.parseJson(input.readLine(), LoadCommandResponse.class);
      success = cmdResponse.getStatus().equals(RouletteV2Protocol.SUCCESS);
      nbrStudentsAdded = cmdResponse.getNumberOfNewStudents();
      ++nbrCommandsSend;
    } catch (IOException e) {
      LOG.log(Level.SEVERE, "An error occured during loadStudents : {0}", e.getMessage());
      throw e;
    }
  }

  @Override
  public void disconnect() throws IOException {
    try {
      writeCmd(RouletteV2Protocol.CMD_BYE);
      output.flush();
      ByeCommandResponse response = JsonObjectMapper.parseJson(input.readLine(), ByeCommandResponse.class);
      success = response.getStatus().equals(RouletteV2Protocol.SUCCESS);
      super.disconnect();
      ++nbrCommandsSend;
    } catch (IOException e) {
      LOG.log(Level.SEVERE, "An error occured during connection to socket : {0}", e.getMessage());
      throw e;
    }
  }

  // THIS PART IS FOR REDEFINITION of V1, we need it because we have to count the commands.
  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    ++nbrCommandsSend;
    return super.pickRandomStudent();
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    ++nbrCommandsSend;
    return super.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    ++nbrCommandsSend;
    return super.getProtocolVersion();
  }

}