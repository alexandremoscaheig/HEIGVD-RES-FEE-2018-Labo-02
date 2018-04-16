package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 *
 * @author Olivier Liechti
 */
public class RouletteV2AlexandreMoscaHeigTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);


  @Test
  @TestAuthor(githubId = "alexandremoscaheig")
  public void theServerShouldClearDataStore() throws IOException, EmptyStoreException {
    IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
    assertEquals(0, client.getNumberOfStudents());
    client.loadStudent("sacha");
    client.loadStudent("olivier");
    client.loadStudent("fabienne");

    assertNotEquals(0, client.getNumberOfStudents());

    client.clearDataStore();

    assertEquals(0, client.getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = "alexandremoscaheig")
  public void theServerShouldListStudentsCorrectly() throws IOException, EmptyStoreException {
    IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
    assertEquals(0, client.getNumberOfStudents());
    String[] studentsNames = new String[]{"sacha", "olivier", "fabienne"};

    int i;
    for (i = 0; i < studentsNames.length; i++){
      client.loadStudent(studentsNames[i]);
    }

    assertEquals(i, client.getNumberOfStudents());

    List<Student> students = client.listStudents();
    for (i = 0; i < students.size(); i++){
      assertEquals(students.get(i).getFullname(), studentsNames[i]);
    }
  }

  @Test
  @TestAuthor(githubId = "alexandremoscaheig")
  public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
    assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
  }

}