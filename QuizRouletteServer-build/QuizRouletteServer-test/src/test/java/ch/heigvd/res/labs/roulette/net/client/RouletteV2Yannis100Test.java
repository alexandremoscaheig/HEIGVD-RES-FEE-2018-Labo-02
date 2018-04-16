package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
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
public class RouletteV2Yannis100Test {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);


  @Test
  @TestAuthor(githubId = "Yannis100")
  // check the first student so this test is bad
  public void serverShouldRun() throws IOException, EmptyStoreException {
    assertTrue(roulettePair.getServer().isRunning());
  }

  @Test
  @TestAuthor(githubId = "Yannis100")
  // check the first student so this test is bad
  public void clientConnected() throws IOException, EmptyStoreException {
    assertTrue(roulettePair.getClient().isConnected());
  }

  @Test
  @TestAuthor(githubId = "Yannis100")
  // check the first student so this test is bad
  public void serverShouldAddStudent() throws IOException, EmptyStoreException {
    final String student = "student";

    IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
    client.loadStudent(student);
    //assertEquals(new Student(student), client.listStudents().get(0));
    assertNotEquals(0, client.getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = "Yannis100")
  public void serverShouldClearDataStore() throws IOException, EmptyStoreException {
    IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
    assertEquals(0, client.getNumberOfStudents());
    client.loadStudent("dylan");
    assertEquals(1, client.getNumberOfStudents());
    client.clearDataStore();

    assertEquals(0, client.getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = "Yannis100")
  public void serverShouldListStudents() throws IOException, EmptyStoreException {
    IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
    assertEquals(0, client.getNumberOfStudents());
    String[] studentsNames = new String[]{"dylan", "yannis"};

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
  @TestAuthor(githubId = "Yannis100")
  public void serverShouldCountStudents() throws IOException, EmptyStoreException {
    IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
    client.clearDataStore();
    assertEquals(0, client.getNumberOfStudents());
    client.loadStudent("dylan");
    assertEquals(1, client.getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = "Yannis100")
  public void serverShouldReturnTheCorrectVersionNumber() throws IOException {
    assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
  }

}
